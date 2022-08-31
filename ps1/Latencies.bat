@echo off
call incljava.bat

set CLASSPATH_ARGS=-cp JMXClient.jar
set MAIN_CLASS=com.example.latencies.Latencies
set OTHER_ARGS=

@echo on
%JAVA_EXE% %JVM_OPTS% %JFR_OPTS% %CLASSPATH_ARGS% %OTHER_ARGS% %MAIN_CLASS%
