@echo off
cd ..\bin
java -Dpython.path=../lib/Lib -classpath ../build;../lib/jython-2.1.jar;../lib/looks-1.2.2.jar org.jext.Jext %1 %2 %3 %4 %5 %6 %7 %8 %9
cd ..\src
