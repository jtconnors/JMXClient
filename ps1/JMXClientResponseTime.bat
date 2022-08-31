@echo off
call incljava.bat

REM =============================================================================
REM Change these variables to point to the client application you want to run and
REM the interval and threshold arguments you want to set

set JMX_SERVER_PROGRAM=com.example.latencies.Latencies
set JFR_DUMP_FILE_NAME=Latencies.jfr
set CLASSPATH_ARGS=-cp JMXClient.jar
set MAIN_CLASS=com.example.jmxclient.JMXClientResponseTime
set PROGRAM_ARGS=-debug -interval:2000 -threshold:900

REM =============================================================================

REM
REM Get the PID of the JMX server program in question
REM

%JCMD_EXE% | findstr %JMX_SERVER_PROGRAM% > PID.txt
set /p PIDSTR=<PID.txt
del PID.txt
for /f "tokens=1" %%i in ("%PIDSTR%") do set JAVA_PID=%%i

%JAVA_EXE% %CLASSPATH_ARGS% %MAIN_CLASS% %PROGRAM_ARGS%

REM
REM If we get here, the threshold has been exceeded.  Dump the flight recoder file
REM

echo Threshold exceeded
%JCMD_EXE% %JAVA_PID% JFR.dump name='HotSpot default' filename=%JFR_DUMP_FILE_NAME%

