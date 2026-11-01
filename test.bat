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
javac -encoding UTF-8 -d build @"%SRC_FILE%"
set "RESULT=%ERRORLEVEL%"
del "%SRC_FILE%" >nul 2>nul
if not "%RESULT%"=="0" exit /b %RESULT%
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build IntegrationTests
if errorlevel 1 exit /b 1
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build FeatureNCCPhieuNhapTests
if errorlevel 1 exit /b 1
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build LegacyProductRegressionTests
if errorlevel 1 exit /b 1
where py >nul 2>nul
if not errorlevel 1 (
  py -3 tests\test_console.py
  if errorlevel 1 exit /b 1
  py -3 tests\test_console_pn.py
  if errorlevel 1 exit /b 1
  py -3 tests\test_legacy_runtime.py
  if errorlevel 1 exit /b 1
) else (
  echo Bo qua test console tu dong vi may chua cai Python 3.
)
exit /b 0

:appendJava
set "ONE_SOURCE=%~1"
set "ONE_SOURCE=%ONE_SOURCE:\=/%"
>>"%~2" echo "%ONE_SOURCE%"
exit /b 0
