#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 4 || $# -gt 5 ]]; then
  echo "Usage: $0 GET|POST /api/path request-count concurrency [output-directory]" >&2
  exit 2
fi
method=$1
path=$2
count=$3
concurrency=$4
[[ $method =~ ^(GET|POST)$ && $path == /api/v1/concurrency/* && $count =~ ^[1-9][0-9]*$ && $concurrency =~ ^[1-9][0-9]*$ ]] || exit 2
directory=${5:-$(mktemp -d /tmp/concurrency-load.XXXXXX)}
mkdir -p "$directory"
base=http://127.0.0.1:8080
started=$(date +%s.%N)

# Each curl writes its own files; parallel workers never append to a shared file.
seq 1 "$count" | xargs -P "$concurrency" -n 1 bash -c '
  method=$1; path=$2; directory=$3; base=$4; index=$5
  if [[ $path == */db-pool/* ]]; then
    path="$path?accountId=$(( (index - 1) % 100 + 1 ))"
  fi
  curl --silent --show-error --max-time 30 -X "$method" \
    -o "$directory/$index.json" -w "%{http_code}\t%{time_total}\n" \
    "$base$path" > "$directory/$index.tsv"
' _ "$method" "$path" "$directory" "$base" || echo "Some curl workers failed; inspect status 000 and stderr." >&2

finished=$(date +%s.%N)
python3 - "$directory" "$method" "$path" "$count" "$concurrency" "$started" "$finished" <<'PY'
import collections
import json
import math
import pathlib
import sys

directory, method, path, count, concurrency, started, finished = sys.argv[1:]
count = int(count)
statuses = collections.Counter()
latencies = []
results = set()
for index in range(1, count + 1):
    status, seconds = (pathlib.Path(directory) / f"{index}.tsv").read_text().strip().split()
    statuses[status] += 1
    latencies.append(float(seconds) * 1000)
    if status == "200":
        body = json.loads((pathlib.Path(directory) / f"{index}.json").read_text())
        results.add(json.dumps(body.get("result"), sort_keys=True))
latencies.sort()
duration = float(finished) - float(started)
successful = sum(value for status, value in statuses.items() if status.startswith("2"))
summary = {
    "method": method, "path": path, "requests": count, "concurrency": int(concurrency),
    "durationSeconds": round(duration, 4), "successful": successful,
    "failedOrRejected": count - successful, "statuses": dict(statuses),
    "responsesPerSecond": round(count / duration, 2),
    "successfulResponsesPerSecond": round(successful / duration, 2),
    "latencyMs": {
        "mean": round(sum(latencies) / count, 3),
        **{name: round(latencies[max(0, math.ceil(count * quantile) - 1)], 3)
           for name, quantile in [("p50", .5), ("p95", .95), ("p99", .99)]},
        "min": round(latencies[0], 3), "max": round(latencies[-1], 3),
    },
    "distinctSuccessfulResults": [json.loads(value) for value in sorted(results)],
    "rawDirectory": directory,
}
(pathlib.Path(directory) / "summary.json").write_text(json.dumps(summary, indent=2) + "\n")
print(json.dumps(summary, indent=2))
PY
