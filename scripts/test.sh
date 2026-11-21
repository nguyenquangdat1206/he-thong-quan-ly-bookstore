#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if [[ ! -f tests/TestSupport.java ]]; then
    echo 'Thieu bo kiem thu trong thu muc tests.'
    exit 1
fi
export BOOKSTORE_TEST_CP="$PWD/build-test"
mkdir -p "$BOOKSTORE_TEST_CP"
java_files=()
while IFS= read -r -d '' source; do
    java_files+=("$source")
done < <(find src tests -name '*.java' -print0)
javac -encoding UTF-8 -d "$BOOKSTORE_TEST_CP" "${java_files[@]}"
failed=0
groups=0
for suite in IntegrationTests FeatureNCCPhieuNhapTests LegacyProductRegressionTests DisplayLogicRegressionTests FullFunctionalAuditTests PersistenceRegressionTests; do
    groups=$((groups + 1))
    printf '\n=== %s ===\n' "$suite"
    if [[ ! -f "tests/$suite.java" ]]; then
        printf 'Thieu ma nguon kiem thu tests/%s.java.\n' "$suite"
        failed=$((failed + 1))
        continue
    fi
    if ! java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Dfile.encoding=UTF-8 -cp "$BOOKSTORE_TEST_CP" "$suite"; then
        failed=$((failed + 1))
    fi
done
test_python="${BOOKSTORE_TEST_PYTHON:-python3}"
if [[ -z "${BOOKSTORE_TEST_PYTHON:-}" && -x .venv/bin/python ]]; then
    test_python="$PWD/.venv/bin/python"
fi
if command -v "$test_python" >/dev/null 2>&1; then
    for suite in test_console.py test_console_pn.py test_legacy_runtime.py test_console_unicode.py test_full_console_audit.py; do
        groups=$((groups + 1))
        printf '\n=== %s ===\n' "$suite"
        if ! "$test_python" -X utf8 "tests/$suite"; then
            failed=$((failed + 1))
        fi
    done
else
    echo 'Thieu Python 3: chua the chay 5 nhom test console.'
    failed=$((failed + 1))
fi
printf '\nTEST SUITES: %s run, %s failed.\n' "$groups" "$failed"
[[ "$failed" -eq 0 ]]
