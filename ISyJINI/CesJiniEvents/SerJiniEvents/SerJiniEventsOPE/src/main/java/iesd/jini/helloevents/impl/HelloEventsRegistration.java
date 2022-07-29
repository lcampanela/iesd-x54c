package iesd.jini.helloevents.impl;

import iesd.jini.helloevents.api.HelloEvents;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.jrmp.JrmpExporter;
import net.jini.lease.LeaseListener;
import net.jini.lease.LeaseRenewalEvent;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.JoinManager;
import net.jini.lookup.ServiceIDListener;
import net.jini.lookup.entry.ServiceInfo;

public class HelloEventsRegistration implements ServiceIDListener, LeaseListener {

    private static final Logger LOGGER = Logger.getLogger(HelloEventsRegistration.class.getName());

    protected ServiceID serviceID = null;
    private JoinManager joinManager = null;

    /* ServiceInfo values */
    private static final String PRODUCT = "HelloEvents Service";
    private static final String MANUFACTURER = "MDEOS Example, Inc.";
    private static final String VENDOR = MANUFACTURER;
    private static final String VERSION = "2018-04-17";
    private static final String MODEL = "HelloEventsImpl";
    private static final String SERIAL_NUMBER = "SN_2018-04-17";

    public HelloEventsRegistration() {
        createHelloEventsService();
    }

    private void createHelloEventsService() {
        HelloEventsImpl helloEventsImpl = new HelloEventsImpl();;
        HelloEvents helloEvents = null;

        try {
            helloEvents = (HelloEvents) new JrmpExporter().export(helloEventsImpl);

            String[] groups = {"Desporto", "IESD"};
            ServiceInfo serviceInfo = new ServiceInfo(PRODUCT, MANUFACTURER,
                    VENDOR, VERSION, MODEL, SERIAL_NUMBER);

            Entry[] entries = new Entry[]{serviceInfo};
            LookupDiscoveryManager lookupDiscoveryManager = new LookupDiscoveryManager(
                    groups, null, null);
            System.out.println("helloEvents.Class = " + helloEvents.getClass());
            joinManager = new JoinManager(helloEvents, entries, this,
                    lookupDiscoveryManager, new LeaseRenewalManager());

            LOGGER.finest("HelloEvents service registered...");
            LOGGER.finest("HelloEvents.Class = " + helloEvents.getClass());
            LOGGER.finest("HelloEvents created..." + helloEvents.toString());

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failure in LookupDiscovery..." + ex);
        }
    }

    @Override
    public void serviceIDNotify(ServiceID serviceID) {
        LOGGER.fine("ServiceID = " + serviceID);
        LeaseRenewalManager leaseRenewalManager = joinManager
                .getLeaseRenewalManager();
        LOGGER.fine("leaseRenewalManager = "
                + leaseRenewalManager.getClass());
        ServiceRegistrar[] serviceRegistrars = joinManager.getJoinSet();
        LOGGER.fine("serviceRegistrars.length = "
                + serviceRegistrars.length);

        for (ServiceRegistrar serviceRegistrar : serviceRegistrars) {
            LOGGER.fine("serviceRegistrar = "
                    + serviceRegistrar.getServiceID());
        }
    }

    @Override
    public void notify(LeaseRenewalEvent leaseRenewalEvent) {
        LOGGER.fine("LeaseRenewalEvent expiration = "
                + leaseRenewalEvent.getLease().getExpiration());
    }
}
