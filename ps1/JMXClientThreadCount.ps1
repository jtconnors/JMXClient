####################
#
# All Scripts should have this preamble     
#
Set-variable -Name CMDLINE_ARGS -Value $args

#
# Move to the directory containing this script so we can source the env.ps1
# properties that follow
#
$STARTDIR = pwd | Select-Object | %{$_.ProviderPath}
cd $PSScriptRoot

#
# Common properties shared by scripts
#
. .\env.ps1
if ($Global:JUST_EXIT -eq "true") {
    cd $STARTDIR
    Exit 1
}
#
# End preamble
#
####################

#
# Get the PID of the JMX server program in question
#
$JCMD_STR = & $env:JAVA_HOME\bin\jcmd.exe | Select-String -Pattern $LATENCIES_MAINCLASS
$JAVA_PID = $JCMD_STR.ToString().Split(" `r`n")[0]

#
# Run the Java command
#
Set-Variable -Name MAINCLASS -Value com.example.jmxclient.JMXClientThreadCount
Set-Variable -Name JAVA_ARGS -Value @(
    '-cp',
    """.\target\$MAINJAR""",
    "$MAINCLASS",
    '-debug',
    '-interval:2000',
    '-threshold:20'
)
Exec-Cmd("$env:JAVA_HOME\bin\java.exe", $JAVA_ARGS)

#
# If we get here, the threshold has been exceeded.  Dump the flight recoder file
#
Set-Variable -Name JCMD_ARGS -Value @(
    "$JAVA_PID",
    'JFR.dump',
    "name=$JFR_NAME",
    "filename=$JFR_FILENAME"
)
Exec-Cmd("$env:JAVA_HOME\bin\jcmd.exe", $JCMD_ARGS)


#
# Return to the original directory
#
cd $STARTDIR

