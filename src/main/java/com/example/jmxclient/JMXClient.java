package com.example.jmxclient;

import java.io.IOException;
import java.lang.management.PlatformManagedObject;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public abstract class JMXClient {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 9999;
    private static final long DEFAULT_POLL_INTERVAL = 1000;  // In milliseconds

    private JMXConnector jmxc;

    protected String host = DEFAULT_HOST;
    protected int port = DEFAULT_PORT;
    protected long pollInterval = DEFAULT_POLL_INTERVAL;
    protected Double threshold;
    protected boolean debug = false;
    protected boolean doPoll = false;
    protected boolean thresholdSet = false;
    protected String mbeanName;


    /*
     * Command-line arguments help message supplied if user specifies
     * either "-help", "--help" or "-?" on command-line
     */
    private static final String[] HELP_MSG = {
        "Command-line options:\n",
        "  -help | --help | -?",
        "\tPrint this screen for command-line argument options and exit",
        "  -debug",
        "\tenable debug output",
        "  -once",
        "\tretrieve mbean value and output value once",
        "  -host:hostname (default: localhost)",
        "\tSpecify host name (or IP Address) of JMX server",
        "  -port:PORT_NUMBER (default 9999)",
        "\tSpecify port for JMX connection",
        "  -interval:milliseconds (default: 1000ms)",
        "\tSpecify polling interval in milliseconds. Polling will continue",
        "\tindefinitely until polled mbean value exceeds threshold.  This",
        "\tmust be used in conjunction with the -threshold:value option.",
        "  -threshold:value",
        "\tSpecify threshold mbean value which will terminate program",
        ""
    };

    /**
     * Get the value of the mbean attribute in question
     *
     * @return the value of the mbeean attribute
     */
    public abstract Object getMbeanAttributeValue();

    /**
     * Get the mbean proxy interface associated with the mbean in question
     *
     * @param mbeanName the name of the mben
     * @param interfaceClass the {@code Class} of the interface
     * @return
     */
    protected PlatformManagedObject getMbeanProxy(String mbeanName,
            Class<? extends PlatformManagedObject> interfaceClass) {
        try {
            return JMX.newMBeanProxy(
                    getMbeanServerConnection(),
                    new ObjectName(mbeanName),
                    interfaceClass,
                    true);
        } catch (IOException | MalformedObjectNameException ex) {
            Logger.getLogger(JMXClient.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Given a valid {@code host} and {@code port} number, construct and return
     * a String representing the address of a JMX API connector server. It takes
     * the form:
     * {@code service:jmx:rmi:///jndi/rmi://{@bold host}:{@bold port}/jmxrmi}
     *
     * @param host host name or IP Address of
     * @param port
     * @return
     */
    public static String GetJMXServiceURL(String host, int port) {
        StringBuilder sb = new StringBuilder();
        sb.append("service:jmx:rmi:///jndi/rmi://");
        sb.append(host);
        sb.append(":");
        sb.append(port);
        sb.append("/jmxrmi");
        return sb.toString();
    }

    /**
     * Get the MBeanServerConnection interface to the MBean Server
     *
     * @return the MBeanServerConnection
     * @throws MalformedURLException
     * @throws IOException
     */
    protected MBeanServerConnection getMbeanServerConnection()
            throws MalformedURLException, IOException {
        JMXServiceURL url;
        debugLog("MBean name = " + mbeanName);
        String urlStr = GetJMXServiceURL(host, port);
        debugLog("URL: " + urlStr);
        url = new JMXServiceURL(urlStr);
        jmxc = JMXConnectorFactory.connect(url, null);

        // Get an MBeanServerConnection
        //
        return jmxc.getMBeanServerConnection();
    }

    protected void closeConnection() {
        if (jmxc != null) {
            try {
                jmxc.close();
            } catch (IOException ex) {
                Logger.getLogger(JMXClient.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Print out the debug message if {@code debug} flag is set
     *
     * @param msg the message to output
     */
    protected void debugLog(String msg) {
        if (debug) {
            Logger.getLogger(JMXClient.class.getName()).info(msg);
        }
    }

    /**
     * Print the value of the mbean attribute
     *
     * @param value the attribute value to be printed
     */
    protected void outputValue(Object value) {
        System.out.println(value);
    }

    /**
     * Get and output the mbean attribute value. Depending on the value of of
     * the {@code doPoll} either perform this task once, or periodically.
     */
    protected void run() {
        Object value;
        if (doPoll) {
            for (;;) {
                try {
                    Thread.sleep(pollInterval);
                } catch (InterruptedException ex) {
                }
                value = getMbeanAttributeValue();
                if (Double.valueOf(value.toString()) > threshold) {
                    debugLog("MBean value: " + Double.valueOf(value.toString())
                            + " exceeded threshold: " + threshold);
                    closeConnection();
                    System.exit(0);
                }
            }
        } else {
            value = getMbeanAttributeValue();
            outputValue(value);
            closeConnection();
        }
    }

    private void parseArgs(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case "-debug":
                    debug = true;
                    continue;
                case "-once":
                    doPoll = false;
                    continue;
                case "-help":
                case "--help":
                case "-?":
                    for (String str : HELP_MSG) {
                        System.out.println(str);
                    }
                    System.exit(0);
            }
            String[] subarg = arg.split(":");
            switch (subarg[0]) {
                case "-port":
                    if (subarg.length > 1) {
                        port = Integer.parseInt(subarg[1]);
                    }
                    break;
                case "-host":
                    if (subarg.length > 1) {
                        try {
                            InetAddress addr = InetAddress.getByName(subarg[1]);
                            host = subarg[1];
                        } catch (UnknownHostException e) {
                            System.err.println("Bad IP address: "
                                    + subarg[1] + " supplied by command-line.");
                        }
                    }
                    break;
                case "-interval":
                    if (subarg.length > 1) {
                        doPoll = true;
                        pollInterval = Long.parseLong(subarg[1]);
                        debugLog("Poll interval set to " + pollInterval + " ms");
                    }
                    break;
                case "-threshold":
                    if (subarg.length > 1) {
                        doPoll = true;
                        threshold = Double.parseDouble(subarg[1]);
                        debugLog("Threshold set to " + threshold);
                        thresholdSet = true;
                    }
                    break;
                default:
                    break;
            }
        }
        if (doPoll) {
            if (!thresholdSet) {
                System.err.println(
                        "Threshold not set, add -threshold:value option");
                for (String str : HELP_MSG) {
                    System.out.println(str);
                }
                System.exit(0);
            }
        }
    }

    public JMXClient(String[] args, String mbeanName) {
        this(args, mbeanName, DEFAULT_HOST, DEFAULT_PORT);
    }

    public JMXClient(String[] args, String mbeanName, String host, int port) {
        this.mbeanName = mbeanName;
        this.host = host;
        this.port = port;
        parseArgs(args);
    }
}
