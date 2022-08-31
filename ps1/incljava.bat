REM
REM IF JAVA_HOME isn't already set, it should be set here
REM
REM set JAVA_HOME=C:\devel\jdk\defaultjdk

set PORT=9999

set JAVA_EXE=%JAVA_HOME%\bin\java.exe
set JCMD_EXE=%JAVA_HOME%\bin\jcmd.exe

set JVM_OPTS=-Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.port=%PORT% -Dcom.sun.management.jmxremote.ssl=false

REM
REM The following JFR Flags are specific to Oracle JDK8.  They will be different
REM for JDK11 and later versions.
REM
set JFR_OPTS=-XX:+UnlockCommercialFeatures -XX:+FlightRecorder -XX:FlightRecorderOptions=defaultrecording=true
