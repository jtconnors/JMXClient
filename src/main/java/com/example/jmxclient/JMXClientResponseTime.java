package com.example.jmxclient;

/*
 * 1. Chanage the following import statement to point to the management
 *    interface for the mbean you want to access.
 *
 *    In this example we have a custom mbean and import the 
 *    PlatformManagedObject interface.  Later on, we create an inner class
 *    which extends this interface.
 */
import java.lang.management.PlatformManagedObject;

/*
 * 2. Refactor the name of the Class (must still extend JMXClient)
 */
public class JMXClientResponseTime extends JMXClient {
    
    /*
     * Custom management interface as described in Step 1.
     */
    public interface SLAReportMBean extends PlatformManagedObject {
	public long getResponseTime();
    }

    /*
     * 3. Change the value of MBEAN_NAME to the name of the mbean you want
     *    to access.
     */
    private static final String MBEAN_NAME = "SimpleAgent:name=SLAReport";

    /*
     * 4. Change the type of "proxy" and the cast to the right of the
     *    assignment operator to the class name imported in step 1.
     */
    private final SLAReportMBean proxy = 
            (SLAReportMBean) getMbeanProxy(MBEAN_NAME, SLAReportMBean.class);
    
    /*
     * 5. Instantaite the abstract getMbeanAttribute() method so that it makes
     *    the specific method call needed to get the desired attribute value.
     */
    @Override
    public Long getMbeanAttributeValue() {
        long threadCount = proxy.getResponseTime();
        debugLog("Response Time = " + threadCount);
        return threadCount;
    }

    public JMXClientResponseTime(String[] args, String mbeanName) {
        super(args, mbeanName);
    }

    public static void main(String[] args) {
        JMXClientResponseTime client
                = new JMXClientResponseTime(args, MBEAN_NAME);
        client.run();
    }

}
