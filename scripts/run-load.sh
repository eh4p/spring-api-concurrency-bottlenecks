#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 4 ]]; then
  echo "Usage: $0 /api/bottlenecks/scenario/version requests concurrency payload.json" >&2
  exit 2
fi
path=$1
count=$2
concurrency=$3
payload=$4
[[ $path =~ ^/api/bottlenecks/(thread-pool|db-pool|lock-contention|cpu)/(violation|solution)$ &&
   $count =~ ^[1-9][0-9]*$ && $concurrency =~ ^[1-9][0-9]*$ && -r $payload ]] || exit 2
output=$(mktemp -d /tmp/api-saturation-load.XXXXXX)
started=$(date +%s.%N)
# Each worker owns a file, avoiding concurrent appends and interleaved status lines.
seq 1 "$count" | xargs -P "$concurrency" -n 1 bash -c '
  curl --silent --show-error --max-time 30 --json "@$1" \
    -o /dev/null -w "%{http_code} %{time_total}\n" \
    "http://127.0.0.1:8080$2" > "$3/$4.txt"
' _ "$payload" "$path" "$output" || echo "Some curl requests failed; counted as errors." >&2
finished=$(date +%s.%N)
awk -v started="$started" -v finished="$finished" -v count="$count" -v concurrency="$concurrency" '
  { responses++; if ($1 ~ /^2[0-9][0-9]$/) successful++; total += $2 }
  END {
    duration = finished - started
    printf "requests=%d concurrency=%d successful=%d errors=%d total_seconds=%.3f mean_ms=%.3f requests_per_second=%.2f\n",
      count, concurrency, successful, count-successful, duration, total*1000/count, count/duration
  }
' "$output"/*.txt
# Remove only files created by this invocation.
rm -r -- "$output"
