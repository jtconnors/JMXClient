#!/bin/bash

#
# Move to the directory containing this script so we can source the env.sh
# properties that follow
#
cd `dirname $0`

#
# Common properties shared by scripts
#
. env.sh

#
# Get the PID of the JMX server program in question
#
JCMD_STR=`$JAVA_HOME/bin/jcmd | grep $LATENCIES_MAINCLASS`
JCMD_STR_ARR=($JCMD_STR)
JAVA_PID=${JCMD_STR_ARR[0]}

#
# Run the Java command
#
MAINCLASS=com.example.jmxclient.JMXClientThreadCount
exec_cmd "$JAVA_HOME/bin/java -cp ./target/$MAINJAR $MAINCLASS -debug -interval:2000 -threshold:20"

#
# If we get here, the threshold has been exceeded.  Dump the flight recoder file
#
exec_cmd "$JAVA_HOME/bin/jcmd $JAVA_PID JFR.dump name=$JFR_NAME filename=$JFR_FILENAME"
