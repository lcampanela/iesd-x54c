/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iesd.jini.helloworldclient;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.ServiceDiscoveryManager;
import org.mdeos.jini.helper.ConfigureJiniFramework;

import iesd.jini.helloworld.api.HelloWorld;
import java.net.URISyntaxException;

public class ClientMainWithLookup {

    private static final Logger LOG = Logger.getLogger(ClientMainWithLookup.class.getName());
    private static final long TIMEOUT = 100000L;

    public static void main(String argv[]) {
        try {

            ConfigureJiniFramework.copyDefaulEmbeddedDirToDefaultFileSystemDir();

            System.setProperty("java.rmi.server.useCodebaseOnly", "false");
            ConfigureJiniFramework.setSecurity(
                    ConfigureJiniFramework.JINI_RUNTIME_CONFDIR + "/HelloWorldClient.policy");

            /**
             * EXECUTA
             */
            new ClientMainWithLookup();

            // stay around long enough to receive replies
            Object keepAlive = new Object();
            synchronized (keepAlive) {
                try {
                    keepAlive.wait();
                } catch (InterruptedException e) {
                }
            }
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            LOG.log(Level.SEVERE, "Exception_1 while lookup", ex);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public ClientMainWithLookup() throws InterruptedException, RemoteException, IOException {

        System.out.println("Setup DiscoveryManagement");

        LookupDiscoveryManager discoveryManager = new LookupDiscoveryManager(
                LookupDiscovery.ALL_GROUPS, null, null);

        ServiceDiscoveryManager clientMgr = new ServiceDiscoveryManager(discoveryManager, new LeaseRenewalManager());

        Class<?>[] classes = new Class<?>[]{HelloWorld.class};
        ServiceTemplate template = new ServiceTemplate(null, classes, null);

        // Try to find the service, blocking until timeout if necessary
        System.out.println("Discovering HelloWorld service, blocking for ms: " + TIMEOUT);

        ServiceItem item = clientMgr.lookup(template, null, TIMEOUT);

        if (item == null || item.service == null
                || (item.service instanceof HelloWorld) == false) {
            // couldn't find a service in time
            throw new IllegalStateException("No Found HelloWorld");
        }

        // Get the service
        HelloWorld helloWorld = (HelloWorld) item.service;

        // Now we have a suitable service, use it
        System.out.println("Call hello with message: " + helloWorld.sayHello("Eu sou o cliente"));
    }

} // ClientLookup

