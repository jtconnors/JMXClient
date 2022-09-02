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

exec_cmd "$JAVA_HOME/bin/java -Dcom.sun.management.jmxremote.authenticate=$JMX_REMOTE_AUTHENTICATE -Dcom.sun.management.jmxremote.port=$PORT -Dcom.sun.management.jmxremote.ssl=$JMX_REMOTE_SSL -XX:StartFlightRecording=name=$JFR_NAME -cp ./target/$MAINJAR $LATENCIES_MAINCLASS"
