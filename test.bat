@echo off
setlocal
cd /d "%~dp0"
chcp 65001 >nul
if not exist build mkdir build
set "SRC_FILE=%TEMP%\oop-test-%RANDOM%-%RANDOM%.txt"
(for /r src %%F in (*.java) do @echo "%%F") > "%SRC_FILE%"
(for /r tests %%F in (*.java) do @echo "%%F") >> "%SRC_FILE%"
javac -encoding UTF-8 -d build @"%SRC_FILE%"
set "RESULT=%ERRORLEVEL%"
del "%SRC_FILE%" >nul 2>nul
if not "%RESULT%"=="0" exit /b %RESULT%
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build IntegrationTests
if errorlevel 1 exit /b 1
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build FeatureNCCPhieuNhapTests
if errorlevel 1 exit /b 1
where py >nul 2>nul
if not errorlevel 1 (
  py -3 tests\test_console.py
  if errorlevel 1 exit /b 1
  py -3 tests\test_console_pn.py
  if errorlevel 1 exit /b 1
) else (
  echo Bo qua test console tu dong vi may chua cai Python 3.
)
endlocal
