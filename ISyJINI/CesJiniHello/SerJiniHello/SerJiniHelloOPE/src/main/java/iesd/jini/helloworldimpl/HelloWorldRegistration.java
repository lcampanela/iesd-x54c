package iesd.jini.helloworldimpl;

import iesd.jini.helloworld.api.HelloWorld;
import java.io.IOException;
import java.rmi.server.ExportException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.discovery.DiscoveryManagement;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.export.Exporter;
import net.jini.jrmp.JrmpExporter;
import net.jini.lease.LeaseRenewalEvent;
import net.jini.lookup.JoinManager;
import net.jini.lookup.ServiceIDListener;
import net.jini.lookup.entry.ServiceInfo;

public class HelloWorldRegistration implements ServiceIDListener {

    private static final Logger LOGGER = Logger.getLogger(HelloWorldRegistration.class.getName());

    private static final String[] SERVICE_GROUP = {"Desporto", "IESD"};
    /* ServiceInfo values */
    private static final String PRODUCT = "HelloWorld Service";
    private static final String MANUFACTURER = "MDEOS Example, Inc.";
    private static final String VENDOR = MANUFACTURER;
    private static final String VERSION = "2018-04-17";
    private static final String MODEL = "HelloWorldImpl";
    private static final String SERIAL_NUMBER = "SN_2018-04-17";

    private DiscoveryManagement discoveryManager;
    private JoinManager joinManager;
    private Exporter exporter;
    private HelloWorld helloWorldImpl;
    private HelloWorld helloWorldProxy;

    public HelloWorldRegistration() {

        try {

            LOGGER.info("Export and registration of HelloWorldImpl");

            this.helloWorldImpl = new HelloWorldImpl();
            this.exporter = new JrmpExporter();
            this.helloWorldProxy = (HelloWorld) this.exporter.export(this.helloWorldImpl);

            //final LookupLocator lookup = new LookupLocator("jini://10.10.30.99");
            LookupLocator[] lookupLocators = null; //{lookup};

            ServiceInfo serviceInfo = new ServiceInfo(PRODUCT, MANUFACTURER,
                    VENDOR, VERSION, MODEL, SERIAL_NUMBER);

            Entry[] entries = new Entry[]{serviceInfo};

            //Setup LookupDiscoveryManager(look for reggie)
            this.discoveryManager = new LookupDiscoveryManager(SERVICE_GROUP, lookupLocators, null);

            //Service Registration
            this.joinManager = new JoinManager(this.helloWorldProxy, entries, this,
                    discoveryManager, null);

            LOGGER.info("HelloWorld service registered: " + helloWorldProxy);

        } catch (ExportException ex) {
            LOGGER.log(Level.SEVERE, "Failure to export", ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Failure while create LookupDiscovery...", ex);
        }
    }

    /**
     * This method is call every time a new registrar(e.g Reggie) is found
     *
     * @param serviceID
     */
    @Override
    public void serviceIDNotify(ServiceID serviceID) {
        LOGGER.finest("ServiceID: " + serviceID);
        LOGGER.finest("LeaseRenewalManager: " + joinManager.getLeaseRenewalManager());
        ServiceRegistrar[] serviceRegistrars = joinManager.getJoinSet();
        LOGGER.finest("Number of serviceRegistrars: " + serviceRegistrars.length);
        for (ServiceRegistrar serviceRegistrar : serviceRegistrars) {
            LOGGER.finest("ServiceRegistrar: " + serviceRegistrar.getServiceID());
        }
    }

    //@Override
    public void notify(LeaseRenewalEvent leaseRenewalEvent) {
        LOGGER.finest("LeaseRenewalEvent expiration: "
                + leaseRenewalEvent.getLease().getExpiration());
    }
}
