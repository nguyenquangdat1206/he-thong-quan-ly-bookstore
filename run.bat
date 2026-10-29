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
set "SRC_FILE=%TEMP%\oop-run-%RANDOM%-%RANDOM%.txt"
(for /r src %%F in (*.java) do @echo "%%F") > "%SRC_FILE%"
javac -encoding UTF-8 -d build @"%SRC_FILE%"
set "RESULT=%ERRORLEVEL%"
del "%SRC_FILE%" >nul 2>nul
if not "%RESULT%"=="0" exit /b %RESULT%
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build Main
endlocal
