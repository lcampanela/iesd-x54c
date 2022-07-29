package org.mdeos.jini.helper;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.ServiceDiscoveryManager;

public class FindService {

    private static final Logger LOGGER = Logger.getLogger(FindService.class.getName());

    public FindService() {
        LOGGER.fine("FindService():constructor...");
    }

    static final long DELAY = 10000L;
    static ServiceItem item = null;
    static LookupDiscoveryManager lookupDiscoveryManager = null;
    static ServiceDiscoveryManager serviceDiscoveryManager = null;
    static String groups[] = LookupDiscovery.ALL_GROUPS;

    static public void InitFindService() {
        LOGGER.fine("FindService.InitFindService()...");
        try {
            lookupDiscoveryManager = new LookupDiscoveryManager(groups, null, null);
            serviceDiscoveryManager = new ServiceDiscoveryManager(lookupDiscoveryManager, new LeaseRenewalManager());
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        LOGGER.fine("FindService.InitFindService() #...");
    }

    static public Object ServiceLookup(Class<?> _class) {
        return ServiceLookup(_class, null, DELAY);
    }

    static public Object ServiceLookup(Class<?> _class, Entry[] _entries) {
        return ServiceLookup(_class, _entries, DELAY);
    }

    static public Object ServiceLookup(Class<?> _class, Entry[] entries, long timeout) {

        if (_class == null) {
            throw new IllegalArgumentException("Service/_class is null...");
        }
        LOGGER.fine("FindService.Servicelookup()..." + _class.getName() + " Delay=" + timeout);
        Object result = null;
        try {
            Class<?>[] classes = new Class<?>[]{_class};
            ServiceTemplate template = new ServiceTemplate(null, classes, entries);
            if (serviceDiscoveryManager == null) {
                LOGGER.fine("FindService.Servicelookup()...serviceDiscoveryManager = NULL");
                throw new NullPointerException("FindService.Servicelookup: serviceDiscoveryManager is NULL...");
            }
            item = serviceDiscoveryManager.lookup(template, null, timeout);
            if (item == null) {
                LOGGER.fine("FindService.Servicelookup: Service not found...");
                throw new NullPointerException("FindService.Servicelookup: Service not found...");
            }

            System.out.println("FindService.Servicelookup(): item.serviceID = " + item.serviceID);
            result = (Object) item.service;
            if (result == null) {
                LOGGER.fine("FindService.Servicelookup: Service is null...");
                throw new NullPointerException("FindService.Servicelookup: Service is null...");
            }
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
