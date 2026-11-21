#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p build
java_files=()
while IFS= read -r -d '' file; do java_files+=("$file"); done < <(find src tests -name '*.java' -print0)
javac --release 17 -encoding UTF-8 -d build "${java_files[@]}"
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Dfile.encoding=UTF-8 -cp build IntegrationTests
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Dfile.encoding=UTF-8 -cp build FeatureNCCPhieuNhapTests
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Dfile.encoding=UTF-8 -cp build LegacyProductRegressionTests
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Dfile.encoding=UTF-8 -cp build DisplayLogicRegressionTests
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Dfile.encoding=UTF-8 -cp build FullFunctionalAuditTests
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Dfile.encoding=UTF-8 -cp build PersistenceRegressionTests
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Dfile.encoding=UTF-8 -cp build DeepAuditRegressionTests
python3 tests/test_console.py
python3 tests/test_console_pn.py
python3 tests/test_legacy_runtime.py
python3 tests/test_console_unicode.py
python3 tests/test_full_console_audit.py
python3 tests/test_data_audit.py
python3 tests/test_deep_console.py
python3 scripts/audit_data.py
