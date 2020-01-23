@echo off

REM ==================================
REM Batch for starting Jext on Windows
REM ==================================

REM ==================================
REM SET JEXT HOME IF YOU ASSOCIATE
REM JEXT TO ANY TYPE OF FILE
REM ALSO SET JEXT DRIVE
REM ==================================

REM set JEXT_HOME=C:\Jext
REM set JEXT_DRIVE=C:

cls
if "%JEXT_HOME%"=="" set JEXT_HOME=..
if "%1"=="debug" goto debug

%JEXT_DRIVE%
cd %JEXT_HOME%\bin

start javaw -Dpython.path=%JEXT_HOME%/lib/Lib -classpath "%CLASSPATH%";..\lib\jython-@JYTHON-VERSION@.jar;..\lib\looks-@LOOKS-VERSION@.jar;..\lib\dawn-@DAWN-VERSION@.jar;..\lib\jext-@VERSION@.jar -Xms48m org.jext.Jext %JEXT_ARGS% %1 %2 %3 %4 %5 %6 %7 %8 %9
goto end

:debug
java -Dpython.path=%JEXT_HOME%/lib/Lib -classpath "%CLASSPATH%";..\lib\jython-@JYTHON-VERSION@.jar;..\lib\looks-@LOOKS-VERSION@.jar;..\lib\dawn-@DAWN-VERSION@.jar;..\lib\jext-@VERSION@.jar -Xms48m org.jext.Jext %2 %3 %4 %5 %6 %7 %8 %9 > log

:end
