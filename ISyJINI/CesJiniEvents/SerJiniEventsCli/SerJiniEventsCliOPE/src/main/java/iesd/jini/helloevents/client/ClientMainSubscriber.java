package iesd.jini.helloevents.client;

import iesd.jini.helloevents.api.HelloEvents;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.export.Exporter;
import net.jini.jrmp.JrmpExporter;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.LookupCache;
import net.jini.lookup.ServiceDiscoveryEvent;
import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.lookup.ServiceItemFilter;
import org.mdeos.jini.helper.ConfigureJiniFramework;

/**
 *
 * @author iesd
 */
public class ClientMainSubscriber implements ServiceDiscoveryListener {

    private static final Logger LOGGER = Logger.getLogger(ClientMainSubscriber.class.getName());

    private static final HelloEventsFilter FLTR_AVAILABLE = new HelloEventsFilter();
    private final LookupDiscoveryManager discoveryManager;
    private final ServiceDiscoveryManager serviceDiscoveryManager;
    private final RemoteEventListener helloEventsListener = new HelloRemoteEventsListener();
    private final RemoteEventListener remoteEventListenerProxy;
    private final LookupCache lookupCache;
    private final Exporter exporter;

    public static void main(String[] args) {

        ConfigureJiniFramework.setupLoggingConfig();
        LOGGER.log(Level.INFO, "Starting APP...");
        try {
            ConfigureJiniFramework.copyDefaulEmbeddedDirToDefaultFileSystemDir();

            System.setProperty("java.rmi.server.useCodebaseOnly", "false");
            ConfigureJiniFramework.setSecurity(ConfigureJiniFramework.JINI_RUNTIME_CONFDIR + "/HelloEventsClient.policy");

            /**
             * EXECUTA
             */
            new ClientMainSubscriber();

            // stay around long enough to receive replies
            Object keepAlive = new Object();
            synchronized (keepAlive) {
                try {
                    keepAlive.wait();
                } catch (InterruptedException e) {
                }
            }

        } catch (URISyntaxException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public ClientMainSubscriber() throws ExportException, IOException {

        LOGGER.info("Setup DiscoveryManagement");

        exporter = new JrmpExporter();
        /**
         * CREATE LISTENER PROXY
         */
        this.remoteEventListenerProxy
                = (RemoteEventListener) exporter.export(helloEventsListener);

        this.discoveryManager
                = new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                        null, null);

        this.serviceDiscoveryManager
                = new ServiceDiscoveryManager(this.discoveryManager,
                        new LeaseRenewalManager());

        Class<?>[] classes = new Class<?>[]{HelloEvents.class};
        ServiceTemplate template = new ServiceTemplate(null, classes, null);

        LOGGER.info("Lookup tempalte: " + template);
        this.lookupCache = this.serviceDiscoveryManager.createLookupCache(template, null, this);

        LOGGER.fine("LookupCache: " + lookupCache.toString());
    }

    /**
     * Intended only for use by util classes.
     *
     * @return the cache of discovered services
     */
    public LookupCache getLookupCache() {
        return lookupCache;
    }

    public HelloEvents findHelloEvents() throws RemoteException {
        final ServiceItem result = getLookupCache().lookup(FLTR_AVAILABLE);
        if (result != null) {
            return ((HelloEvents) result.service);
        }
        return null;
    }

    // methods for ServiceDiscoveryListener
    @Override
    public void serviceAdded(ServiceDiscoveryEvent serviceDiscoveryEvent) {
        // evt.getPreEventServiceItem() == null
        ServiceItem postItem = serviceDiscoveryEvent.getPostEventServiceItem();
        LOGGER.fine("Service appeared: " + postItem.service.getClass().toString());

        addRemoteListener(postItem);
    }

    // methods for ServiceDiscoveryListener
    @Override
    public void serviceChanged(ServiceDiscoveryEvent serviceDiscoveryEvent) {

        ServiceItem preItem = serviceDiscoveryEvent.getPostEventServiceItem();
        LOGGER.fine("Service preItem = " + preItem.toString());
        ServiceItem postItem = serviceDiscoveryEvent.getPreEventServiceItem();
        LOGGER.fine("Service changed: " + postItem.service.getClass().toString());

        addRemoteListener(postItem);
    }

    // methods for ServiceDiscoveryListener
    @Override
    public void serviceRemoved(ServiceDiscoveryEvent evt) {
        ServiceItem preItem = evt.getPreEventServiceItem();
        LOGGER.fine("Service disappeared: " + preItem.service.getClass().toString());
    }

    private void addRemoteListener(ServiceItem item) {

        try {
            if (item == null || item.service == null || (item.service instanceof HelloEvents) == false) {
                // couldn't find a service in time
                throw new IllegalStateException("No Found HelloEvents");
            }

            HelloEvents helloEvents = (HelloEvents) item.service;

            //REGIST THE LISTENER 
            helloEvents.addRemoteListener(remoteEventListenerProxy);
            LOGGER.info("Listener resgisted: " + remoteEventListenerProxy);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    static class HelloRemoteEventsListener implements RemoteEventListener {

        public HelloRemoteEventsListener() {

        }

        @Override
        public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {
            LOGGER.info("Received remoteEvent: " + remoteEvent.toString());
        }
    }

    static final class HelloEventsFilter implements ServiceItemFilter {

        @Override
        public boolean check(ServiceItem si) {
            LOGGER.fine("Service Filter: " + si.service);
            if (si.service instanceof HelloEvents) {
                return true;
            }

            return false;
        }
    }
}
