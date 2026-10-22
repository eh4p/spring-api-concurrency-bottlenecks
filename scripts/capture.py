#!/usr/bin/env python3
"""Run the curl diagnostic burst and sample Actuator; standard library only."""
import argparse
import json
import pathlib
import subprocess
import tempfile
import time
import urllib.parse
import urllib.request


def metric(name, tag=None):
    query = "?" + urllib.parse.urlencode({"tag": tag}) if tag else ""
    with urllib.request.urlopen(f"http://127.0.0.1:8081/actuator/metrics/{name}{query}", timeout=5) as response:
        return {item["statistic"]: item["value"] for item in json.load(response)["measurements"]}


parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument("method", choices=["GET", "POST"])
parser.add_argument("path")
parser.add_argument("count", type=int)
parser.add_argument("concurrency", type=int)
parser.add_argument("output", nargs="?", default=None)
args = parser.parse_args()
directory = pathlib.Path(args.output or tempfile.mkdtemp(prefix="concurrency-capture-"))
directory.mkdir(parents=True, exist_ok=True)
metrics = {
    "tomcat.threads.busy": "name:http-nio-127.0.0.1-8080",
    "tomcat.threads.current": "name:http-nio-127.0.0.1-8080",
    "tomcat.threads.config.max": "name:http-nio-127.0.0.1-8080",
    "process.cpu.usage": None,
    "system.cpu.usage": None,
    "system.cpu.count": None,
    **{f"hikaricp.connections.{suffix}": "pool:demo-pool"
       for suffix in ["active", "idle", "pending", "max", "acquire", "usage"]},
    **{f"demo.queue.{suffix}|{implementation}": f"implementation:{implementation}"
       for suffix in ["active", "depth", "completed", "rejected", "failed", "completion", "waiting"]
       for implementation in ["violation", "solution"]},
}


def sample():
    return {label: metric(label.split("|")[0], tag) for label, tag in metrics.items()}


baseline = sample()
started = time.monotonic()
samples = []
script = pathlib.Path(__file__).resolve().with_name("load.sh")
with (directory / "load-output.json").open("w") as output:
    process = subprocess.Popen([str(script), args.method, args.path, str(args.count),
                                str(args.concurrency), str(directory)], stdout=output)
    while True:
        values = sample()
        samples.append({"elapsedSeconds": time.monotonic() - started, "metrics": values})
        running = process.poll() is None
        draining = "/work-queue/" in args.path and any(
            values[f"demo.queue.{suffix}|{implementation}"]["VALUE"] > 0
            for suffix in ["active", "depth"] for implementation in ["violation", "solution"])
        if not running and not draining:
            break
        if time.monotonic() - started > 120:
            process.terminate()
            raise SystemExit("Capture exceeded 120 seconds; inspect partial curl outputs and queue metrics.")
        time.sleep(.1)

if process.returncode:
    raise SystemExit(f"load.sh failed with exit code {process.returncode}")
final = sample()
peaks = {label: max(item["metrics"][label]["VALUE"] for item in samples)
         for label in metrics if "VALUE" in baseline[label]}
evidence = {"baseline": baseline, "final": final, "peaks": peaks,
            "captureSecondsIncludingDrain": time.monotonic() - started, "samples": samples}
if "/work-queue/" in args.path:
    with urllib.request.urlopen("http://127.0.0.1:8080/api/v1/concurrency/work-queue/metrics", timeout=5) as response:
        evidence["queuesAfterDrain"] = json.load(response)
(directory / "evidence.json").write_text(json.dumps(evidence, indent=2) + "\n")
print((directory / "summary.json").read_text())
print(json.dumps({"peaks": peaks, "captureSecondsIncludingDrain": evidence["captureSecondsIncludingDrain"],
                  "evidenceFile": str(directory / "evidence.json")}, indent=2))
