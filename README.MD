# Mimicking Java Mission Control Triggers Outside Java Mission Control

## Introduction
Java Mission Control triggers enable you to selectively dump detailed runtime information about your Java application when user-defined conditions are met.  In order to take advantage of this powerful feature, you must create and enable trigger rules inside the Java Mission Control client.  So the question becomes, is it possible to mimic trigger-like behavior outside of Mission Control?  This project aims to show how you can, with a simple JMX client program and some scripting.

The original blog posting on this topic can be found at [https://jtconnors.github.io/blog/content/mimicking_java_flight_recorder_triggers.html](https://jtconnors.github.io/blog/content/mimicking_java_flight_recorder_triggers.html)

## Helper Classes and Scripts for Mimicking Mission Control Triggers

1.  [The Java Applications Included](#applications)
2.  [Sample JMX Managed Application](#latencies)
3.  [Command Line Arguments to JMX Client Applications](#args)
4.  [Helper Files Provided in This Repository](#files)
5.  [How to Run the Example](#howtorun)
6.  [Notes](#notes)

<a id="markdown-applications" name="applications"></a>
### 1. The Java Applications Included

Under the com/example/jmxclient source directory are three Java source files:
* [JMXClient.java](src/main/java/com/example/jmxclient/JMXClient.java) - this is an abstract class that does most of the heavy lifting.  It is responsible for creating a JMX client connection to a JMX server application.  It must be extended to become a real client representing a specific mbean.

* [JMXClientThreadCount.java](src/main/java/com/example/jmxclient/JMXClientThreadCount.java) - this is the first of two example JMX clients that extends the aforementioned JMXClient class. It accesses the  ```java.lang:type=Threading``` mbean and makes a call to the mbean's ```getThreadCount()``` method to retrieve the managed application's thread count.  In order to understand how this JMX client was created, the source code for this example instructs you to make 5 straightforward modifications.

* [JMXClientResponseTime.java](src/main/java/com/example/jmxclient/JMXClientResponseTime.java) - this is the second of two sample JMX clients extending the ```JMXClient``` class.  It accesses a custom mbean called ```SimpleAgent:name=SLAReport``` and makes a call to the mbean's ```getResponseTime()``` method. Just like the first example above (```JMXClientThreadCount.java```), the source highlights the 5 modifications needed to make a client of this type.  The main difference here is the simple custom bean proxy (one method) is defined as an inner class.

<a id="markdown-latencies" name="latencies"></a>
### 2. Sample JMX Managed Application

Under the ```com/example/latencies``` source directory is our simple JMX managed
application called ```Latencies```.  When started, this program prompts the user
to hit ```<Enter>```.  Each time it is pressed, an additional thread is created
along with extra latency too.  We'll use the our two sample JMX client
programs listed above to get thread count and response time data on this
running application.

<a id="markdown-args" name="args"></a>
### 3. Command Line Arguments to JMX Client Applications

When running JMXClient derived programs, here's the list of available command-line arguments.  This message can be printed out with the ```-help``` or ```-?``` option.

      -help | --help | -?
            Print this screen for command-line argument options and exit
      -debug
            enable debug output
      -once
            retrieve mbean value and output value once
      -host:hostname (default: localhost)
            Specify host name (or IP Address) of JMX server
      -port:PORT_NUMBER (default 9999)
            Specify port for JMX connection
      -interval:milliseconds (default: 1000ms)
            Specify polling interval in milliseconds. Polling will continue
            indefinitely until polled mbean value exceeds threshold.  This
            must be used in conjunction with the -threshold:value option.
      -threshold:value
            Specify threshold mbean value which will terminate program

Our sample JMX scripts (see below) use the ```-interval``` and ```-threshold``` options to mimic Java Mission Control trigger behavior.


<a id="markdown-files" name="files"></a>
### 4. Files Provided in This Repository

* Once this project is built, ```JMXClient-jdk17.jar``` will be created in the ```target/``` directory.  It contains all the class files needed to run the Latencies,
        ```JMXClientThreadCount``` and ```JMXClientResponseTime``` programs.  This is created by running the ```mvn package``` command from this project's main directory.

* [Latencies.ps1](ps1\Latencies.ps1)- PowerShell script used to run the ```Latencies``` Program.

* [JMXClientThreadCount.ps1](ps1\JMXClientThreadCount.ps1) - PowerShell script used to run the ```JMXClientThreadCount``` JMX client application with the following arguments: ```-interval:2000 -threshold:20```.  This instructs the program to continue polling the ```Latencies``` program every 2000ms (2 seconds) until the thread count exceeds 20.  Once exceeded, a Java Flight Recorder dump will take place. 
    
* [JMXClientResponseTime.ps1](ps1\JMXClientResponseTime.ps1) - PowerShell script used to run the ```JMXClientResponseTime``` JMX client application with the following arguments: ```-interval:2000 -threshold:900```. This instructs the program to continue polling the ```Latencies``` program every 2000ms (2 seconds) until the response time exceeds 900ms.  Once exceeded, a Java Flight Recorder dump will take place.

* [env.ps1](ps1\env.ps1) - This is a PowerShell file which is included by the three PowerShell scripts listed above.

<a id="markdown-howtorun" name="howtorun"></a>
### 5. How to Run the Example

1. Perform ```mvn clean package``` on this (JMXClient) project.

2. Run the ```ps1\Latencies.ps1 script```.  This program instructs the user to hit
       ```<Enter>``` to add threads.  DO NOT HIT ```<Enter>``` YET.

4. Run either the ```ps1\JMXClientThreadCount.ps1``` or ```ps1\JMXClientResponseTime.ps1```
       script. It will continually poll the Latencies program until the
       designated "trigger" threshold is met.

5. Return to the ```Latencies.ps1``` window and hit ```<ENTER>``` 4 or 5 times.  This 
       should cause the "trigger" threshold to be met causing a Java Flight 
       Recorder dump of the Latencies program to the ```Latencies.jfr``` file.

6. With the created ```Latencies.jfr``` file, you can start Java Mission Control
       (```jmc```) and open the flight recorder file.

<a id="markdown-notes" name="notes"></a>
### 5. Notes
* The scripts to facilitate running these sample programs are Windows-specific.  With a little effort they can be ported to linux/MacOS
* The scripts require that a version of JDK 17 be used.  The reason for this dependency is that earlier JDK versions used different command-line arguments to invoke Java Flight Recorder.  