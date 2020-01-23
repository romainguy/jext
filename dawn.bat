@echo off

REM ==================================
REM Batch for starting Dawn on Windows
REM ==================================

java -classpath "%CLASSPATH%";..\lib\dawn-@DAWN-VERSION@.jar;..\lib\jext-@VERSION@.jar org.jext.dawn.Dawn %1 %2
