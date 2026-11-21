#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p build
java_files=()
while IFS= read -r -d '' file; do java_files+=("$file"); done < <(find src -name '*.java' -print0)
javac --release 17 -encoding UTF-8 -d build "${java_files[@]}"
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Dfile.encoding=UTF-8 -cp build Main
