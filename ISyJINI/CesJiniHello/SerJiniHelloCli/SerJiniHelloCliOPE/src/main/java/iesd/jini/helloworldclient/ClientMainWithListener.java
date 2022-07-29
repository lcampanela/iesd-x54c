/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iesd.jini.helloworldclient;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.LookupCache;
import net.jini.lookup.ServiceDiscoveryEvent;
import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.lookup.ServiceDiscoveryManager;
import org.mdeos.jini.helper.ConfigureJiniFramework;

import iesd.jini.helloworld.api.HelloWorld;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import net.jini.core.discovery.LookupLocator;

public class ClientMainWithListener implements ServiceDiscoveryListener, DiscoveryListener {

    private static final Logger LOG = Logger.getLogger(ClientMainWithListener.class.getName());

    private ServiceDiscoveryManager serviceDiscoveryManager;
    private LookupDiscoveryManager discoveryManager;

    public static void main(String argv[]) {

        try {

            ConfigureJiniFramework.copyDefaulEmbeddedDirToDefaultFileSystemDir();
            System.setProperty("java.rmi.server.useCodebaseOnly", "false");
            ConfigureJiniFramework.setSecurity(ConfigureJiniFramework.JINI_RUNTIME_CONFDIR + "/HelloWorldClient.policy");

            /**
             * EXECUTA
             */
            new ClientMainWithListener();

            // stay around long enough to receive replies
            Object keepAlive = new Object();
            synchronized (keepAlive) {
                try {
                    keepAlive.wait();
                } catch (InterruptedException e) {
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public ClientMainWithListener() throws MalformedURLException, IOException {

        System.out.println("Setup DiscoveryManagement");

        //final LookupLocator lookup = new LookupLocator("jini://192.168.99.100");
        LookupLocator[] lookupLocators = null;//{lookup};

        this.discoveryManager = new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS,
                lookupLocators, this);

        this.serviceDiscoveryManager
                = new ServiceDiscoveryManager(discoveryManager,
                        new LeaseRenewalManager());

        Class<?>[] classes = new Class<?>[]{HelloWorld.class};
        ServiceTemplate template = new ServiceTemplate(null, classes, null);

        //
        LookupCache cache = serviceDiscoveryManager.createLookupCache(template, null, this);
        System.out.println("cache = " + cache.toString());

    }

    // methods for ServiceDiscoveryListener
    @Override
    public void serviceAdded(ServiceDiscoveryEvent serviceDiscoveryEvent) {
        // evt.getPreEventServiceItem() == null
        ServiceItem postItem = serviceDiscoveryEvent.getPostEventServiceItem();
        System.out.println("Service appeared serviceID: " + postItem.serviceID.toString());
        callHello(postItem);
    }

    // methods for ServiceDiscoveryListener
    @Override
    public void serviceChanged(ServiceDiscoveryEvent serviceDiscoveryEvent) {

        ServiceItem postItem = serviceDiscoveryEvent.getPostEventServiceItem();
        System.out.println("preItem = " + postItem.toString());
        ServiceItem preItem = serviceDiscoveryEvent.getPreEventServiceItem();
        System.out.println("Service changed serviceID: " + preItem.serviceID.toString());
        callHello(postItem);
    }

    // methods for ServiceDiscoveryListener
    @Override
    public void serviceRemoved(ServiceDiscoveryEvent evt) {
        // evt.getPostEventServiceItem() == null
        ServiceItem preItem = evt.getPreEventServiceItem();
        System.out.println("Service disappeared serviceID: " + preItem.serviceID.toString());
    }

    /**
     * Every time a new ServiceRegistrar is found. we will be advised via a call
     * to this method. methods for DiscoveryListener
     */
    @Override
    public void discovered(DiscoveryEvent anEvent) {
        for (ServiceRegistrar myReg : anEvent.getRegistrars()) {
            System.out.println("ServiceRegistrar discovered: " + myReg.getServiceID());
        }
    }

    /**
     * When a ServiceRegistrar "disappears" due to network partition etc. we
     * will be advised via a call to this method. methods for DiscoveryListener
     */
    @Override
    public void discarded(DiscoveryEvent anEvent) {
        for (ServiceRegistrar myReg : anEvent.getRegistrars()) {
            System.out.println("ServiceRegistrar discarded: " + myReg.getServiceID());
        }
    }

    private void callHello(ServiceItem item) {
        if (item == null || item.service == null || (item.service instanceof HelloWorld) == false) {
            // couldn't find a service in time
            throw new IllegalStateException("No Found HelloWorld");
        }

        // Get the service
        HelloWorld helloWorld = (HelloWorld) item.service;

        // Now we have a suitable service, use it
        try {
            System.out.println("Call hello with message: " + helloWorld.sayHello("Hello em " + new Date()));
        } catch (RemoteException ex) {
            Logger.getLogger(ClientMainWithLookup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
