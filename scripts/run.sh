#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p build
mapfile -d '' java_files < <(find src -name '*.java' -print0)
javac -encoding UTF-8 -d build "${java_files[@]}"
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Dfile.encoding=UTF-8 -cp build Main
