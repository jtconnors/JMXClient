package com.example.jmxclient;

/*
 * 1. Chanage the following import statement to point to the management
 *    interface for the mbean you want to access.
 */
import java.lang.management.ThreadMXBean;

/*
 * 2. Refactor the name of the Class (must still extend JMXClient)
 */
public class JMXClientThreadCount extends JMXClient {

    /*
     * 3. Change the value of MBEAN_NAME to the name of the mbean you want
     *    to access.
     */
    private static final String MBEAN_NAME = "java.lang:type=Threading";

    /*
     * 4. Change the type of "proxy" and the cast to the right of the
     *    assignment operator to the class name imported in step 1.
     */
    private final ThreadMXBean proxy = (ThreadMXBean) getMbeanProxy(MBEAN_NAME,
            ThreadMXBean.class);
    
    /*
     * 5. Instantaite the abstract getMbeanAttribute() method so that it makes
     *    the specific method call needed to get the desired attribute value.
     */
    @Override
    public Integer getMbeanAttributeValue() {
        int threadCount = proxy.getThreadCount();
        debugLog("Thread Count = " + threadCount);
        return threadCount;
    }

    public JMXClientThreadCount(String[] args, String mbeanName) {
        super(args, mbeanName);
    }

    public static void main(String[] args) {
        JMXClientThreadCount client
                = new JMXClientThreadCount(args, MBEAN_NAME);
        client.run();
    }

}
