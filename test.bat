@echo off
setlocal
cd /d "%~dp0"
chcp 65001 >nul
where javac >nul 2>nul
if errorlevel 1 (
  echo Khong tim thay javac. Vui long cai JDK 17+ va them vao PATH.
  exit /b 1
)
if not exist build mkdir build
rem Java argfiles interpret backslashes as escapes (e.g. C:\Users\tplam has \t).
rem Use forward slashes in all paths given to javac via @argfile.
set "SRC_FILE=%TEMP%\oop-test-%RANDOM%-%RANDOM%.txt"
type nul > "%SRC_FILE%"
for /r src %%F in (*.java) do call :appendJava "%%~fF" "%SRC_FILE%"
for /r tests %%F in (*.java) do call :appendJava "%%~fF" "%SRC_FILE%"
javac --release 17 -encoding UTF-8 -d build @"%SRC_FILE%"
set "RESULT=%ERRORLEVEL%"
del "%SRC_FILE%" >nul 2>nul
if not "%RESULT%"=="0" exit /b %RESULT%
set "TEST_FAILED=0"
for %%T in (IntegrationTests FeatureNCCPhieuNhapTests LegacyProductRegressionTests DisplayLogicRegressionTests FullFunctionalAuditTests PersistenceRegressionTests DeepAuditRegressionTests) do call :runJava %%T
where py >nul 2>nul
if errorlevel 1 goto :missingPython
for %%T in (test_console.py test_console_pn.py test_legacy_runtime.py test_console_unicode.py test_full_console_audit.py test_data_audit.py test_deep_console.py) do call :runPython %%T
py -3 -X utf8 scripts\audit_data.py
if errorlevel 1 set "TEST_FAILED=1"
if "%TEST_FAILED%"=="1" (
  echo ONE OR MORE TEST SUITES FAILED
  exit /b 1
)
echo ALL TEST SUITES PASSED
exit /b 0

:runJava
echo Running %~1
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build %~1
if errorlevel 1 set "TEST_FAILED=1"
exit /b 0

:runPython
echo Running %~1
py -3 -X utf8 "tests\%~1"
if errorlevel 1 set "TEST_FAILED=1"
exit /b 0

:missingPython
echo INCOMPLETE: Khong tim thay Python launcher py. Chua chay test console.
exit /b 2

:appendJava
set "ONE_SOURCE=%~1"
set "ONE_SOURCE=%ONE_SOURCE:\=/%"
>>"%~2" echo "%ONE_SOURCE%"
exit /b 0
