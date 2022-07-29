package iesd.jini.helloevents.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.server.ExportException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mdeos.jini.helper.ConfigureJiniFramework;

import iesd.jini.helloevents.api.HelloEvents;
import java.util.Date;
import net.jini.core.event.RemoteEvent;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.ServiceDiscoveryManager;

public class ClientMainEventPublisher2 {

    private static final Logger LOGGER = Logger.getLogger(ClientMainEventPublisher2.class.getName());
    private static final long TIMEOUT = 100000L;

    private LookupDiscoveryManager discoveryManager;
    private ServiceDiscoveryManager serviceDiscoveryManager;

    public static void main(String argv[]) {

        ConfigureJiniFramework.setupLoggingConfig();

        LOGGER.info("Starting APP");
        try {
            ConfigureJiniFramework.copyDefaulEmbeddedDirToDefaultFileSystemDir();

            ConfigureJiniFramework.setSecurity(
                    ConfigureJiniFramework.JINI_RUNTIME_CONFDIR + "/HelloEventsClient.policy");

            /**
             * EXECUTA
             */
            new ClientMainEventPublisher2();

        } catch (ExportException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, "Exception wainting", ex);
        } catch (URISyntaxException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public ClientMainEventPublisher2() throws IOException, InterruptedException {
        LOGGER.info("Setup LookupDiscoveryManager");

        this.discoveryManager
                = new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                        null, null);

        this.serviceDiscoveryManager
                = new ServiceDiscoveryManager(discoveryManager,
                        new LeaseRenewalManager());

        Class<?>[] classes = new Class<?>[]{HelloEvents.class};
        ServiceTemplate template = new ServiceTemplate(null, classes, null);

        LOGGER.info("Lookup tempalte: " + template);
        ServiceItem item = this.serviceDiscoveryManager.lookup(template, null, TIMEOUT);

        if (item == null || item.service == null || (item.service instanceof HelloEvents) == false) {
            // couldn't find a service in time
            throw new IllegalStateException("No Found HelloEvents");
        }

        LOGGER.info("Found an HelloEvents: " + item.toString());
        HelloEvents helloEvents = (HelloEvents) item.service;

        // Now we have a suitable service, use it
        long seqNum = 0L;
        long eventID = 5555L;
        RemoteEvent remoteEvent = new RemoteEvent("EVENT 2: " + new Date(), eventID, seqNum++, null);

        helloEvents.publishEvent(remoteEvent);
        LOGGER.info("Publish/Send an event message: " + remoteEvent.toString());
    }
}
