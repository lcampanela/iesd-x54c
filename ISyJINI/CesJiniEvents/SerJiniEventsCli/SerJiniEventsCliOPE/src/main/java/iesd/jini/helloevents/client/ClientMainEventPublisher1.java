/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iesd.jini.helloevents.client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.LookupCache;
import net.jini.lookup.ServiceDiscoveryEvent;
import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.lookup.ServiceDiscoveryManager;
import org.mdeos.jini.helper.ConfigureJiniFramework;

import iesd.jini.helloevents.api.HelloEvents;
import java.net.URISyntaxException;
import java.rmi.server.ExportException;
import java.util.Date;
import net.jini.core.event.RemoteEvent;

public class ClientMainEventPublisher1 implements ServiceDiscoveryListener {

    private static final Logger LOGGER = Logger.getLogger(ClientMainEventPublisher1.class.getName());

    private LookupDiscoveryManager discoveryManager;
    private ServiceDiscoveryManager serviceDiscoveryManager;

    public static void main(String argv[]) {

        ConfigureJiniFramework.setupLoggingConfig();

        try {
            ConfigureJiniFramework.copyDefaulEmbeddedDirToDefaultFileSystemDir();
            System.setProperty("java.rmi.server.useCodebaseOnly", "false");
            ConfigureJiniFramework.setSecurity(
                    ConfigureJiniFramework.JINI_RUNTIME_CONFDIR + "/HelloEventsClient.policy");

            /**
             * EXECUTA
             */
            new ClientMainEventPublisher1();

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

    public ClientMainEventPublisher1() throws IOException {
        LOGGER.info("Setup DiscoveryManagement");

        this.discoveryManager
                = new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                        null, null);

        this.serviceDiscoveryManager
                = new ServiceDiscoveryManager(discoveryManager,
                        new LeaseRenewalManager());

        Class<?>[] classes = new Class<?>[]{HelloEvents.class};
        ServiceTemplate template = new ServiceTemplate(null, classes, null);

        LOGGER.info("Lookup tempalte: " + template);
        LookupCache cache = serviceDiscoveryManager.createLookupCache(template, null, this);
        LOGGER.fine("LookupCache = " + cache.toString());

    }

    // methods for ServiceDiscoveryListener
    @Override
    public void serviceAdded(ServiceDiscoveryEvent serviceDiscoveryEvent) {
        // evt.getPreEventServiceItem() == null
        ServiceItem postItem = serviceDiscoveryEvent.getPostEventServiceItem();
        LOGGER.fine("Service appeared: " + postItem.service.getClass().toString());

        publishHelloEvent(postItem);
    }

    // methods for ServiceDiscoveryListener
    @Override
    public void serviceChanged(ServiceDiscoveryEvent serviceDiscoveryEvent) {

        ServiceItem preItem = serviceDiscoveryEvent.getPostEventServiceItem();
        LOGGER.fine("Service preItem = " + preItem.toString());
        ServiceItem postItem = serviceDiscoveryEvent.getPreEventServiceItem();
        LOGGER.fine("Service changed: " + postItem.service.getClass().toString());

        publishHelloEvent(postItem);
    }

    // methods for ServiceDiscoveryListener
    @Override
    public void serviceRemoved(ServiceDiscoveryEvent evt) {
        ServiceItem preItem = evt.getPreEventServiceItem();
        LOGGER.fine("Service disappeared: " + preItem.service.getClass().toString());
    }

    private void publishHelloEvent(ServiceItem item) {

        try {
            if (item == null || item.service == null || (item.service instanceof HelloEvents) == false) {
                // couldn't find a service in time
                throw new IllegalStateException("No Found HelloEvents");
            }

            HelloEvents helloEvents = (HelloEvents) item.service;

            // Now we have a suitable service, use it
            long seqNum = 0L;
            long eventID = 5555L;
            RemoteEvent remoteEvent = new RemoteEvent("EVENTO 1: " + new Date(), eventID, seqNum++, null);

            helloEvents.publishEvent(remoteEvent);
            LOGGER.fine("Ppublish/Send an event message: " + remoteEvent.toString());
        } catch (ExportException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

}
