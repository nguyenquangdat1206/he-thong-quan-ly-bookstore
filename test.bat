@echo off
setlocal
cd /d "%~dp0"
chcp 65001 >nul
where javac >nul 2>nul
if errorlevel 1 (
  echo Khong tim thay javac. Vui long cai JDK 17+ va them vao PATH.
  exit /b 1
)
where java >nul 2>nul
if errorlevel 1 (
  echo Khong tim thay java. Vui long cai JDK 17+ va them vao PATH.
  exit /b 1
)
if not exist tests\TestSupport.java (
  echo Thieu bo kiem thu trong thu muc tests.
  exit /b 1
)
set "BOOKSTORE_TEST_CP=%CD%\build-test"
if not exist "%BOOKSTORE_TEST_CP%" mkdir "%BOOKSTORE_TEST_CP%"
rem Java argfiles interpret backslashes as escapes (e.g. C:\Users\tplam has \t).
rem Use forward slashes in all paths given to javac via @argfile.
set "SRC_FILE=%BOOKSTORE_TEST_CP%\sources.txt"
type nul > "%SRC_FILE%"
for /r src %%F in (*.java) do call :appendJava "%%~fF" "%SRC_FILE%"
for /r tests %%F in (*.java) do call :appendJava "%%~fF" "%SRC_FILE%"
javac -encoding UTF-8 -d "%BOOKSTORE_TEST_CP%" @"%SRC_FILE%"
set "RESULT=%ERRORLEVEL%"
del "%SRC_FILE%" >nul 2>nul
if not "%RESULT%"=="0" exit /b %RESULT%
set "FAILED=0"
set "GROUPS=0"
for %%T in (IntegrationTests FeatureNCCPhieuNhapTests LegacyProductRegressionTests DisplayLogicRegressionTests FullFunctionalAuditTests PersistenceRegressionTests) do call :runJava %%T
set "PYTHON="
set "PYTHON_ARGS="
if defined BOOKSTORE_TEST_PYTHON (
  set "PYTHON=%BOOKSTORE_TEST_PYTHON%"
  goto pythonReady
)
if exist "%~dp0.venv\Scripts\python.exe" (
  set "PYTHON=%~dp0.venv\Scripts\python.exe"
  goto pythonReady
)
where py >nul 2>nul
if not errorlevel 1 (
  set "PYTHON=py"
  set "PYTHON_ARGS=-3"
  goto pythonReady
)
where python >nul 2>nul
if not errorlevel 1 set "PYTHON=python"
:pythonReady
if not defined PYTHON (
  echo Thieu Python 3: chua the chay 5 nhom test console.
  set /a FAILED+=1 >nul
  goto summary
)
for %%T in (test_console.py test_console_pn.py test_legacy_runtime.py test_console_unicode.py test_full_console_audit.py) do call :runPython %%T
:summary
echo.
echo TEST SUITES: %GROUPS% run, %FAILED% failed.
if not "%FAILED%"=="0" exit /b 1
exit /b 0

:runJava
set /a GROUPS+=1 >nul
echo.
echo === %~1 ===
if not exist "tests\%~1.java" (
  echo Thieu ma nguon kiem thu tests\%~1.java.
  set /a FAILED+=1 >nul
  exit /b 0
)
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp "%BOOKSTORE_TEST_CP%" %~1
if errorlevel 1 set /a FAILED+=1 >nul
exit /b 0

:runPython
set /a GROUPS+=1 >nul
echo.
echo === %~1 ===
"%PYTHON%" %PYTHON_ARGS% -X utf8 "tests\%~1"
if errorlevel 1 set /a FAILED+=1 >nul
exit /b 0

:appendJava
set "ONE_SOURCE=%~1"
set "ONE_SOURCE=%ONE_SOURCE:\=/%"
>>"%~2" echo "%ONE_SOURCE%"
exit /b 0
