package iesd.jini.bank.impl;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.logging.Level;
import java.util.logging.Logger;

import iesd.jini.bank.api.RemoteBank;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.discovery.DiscoveryManagement;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.jrmp.JrmpExporter;
import net.jini.lookup.JoinManager;
import net.jini.lookup.ServiceIDListener;
import net.jini.lookup.entry.ServiceInfo;

public class BankServicesRegistration implements ServiceIDListener {

    private static final Logger LOGGER = Logger.getLogger(BankServicesRegistration.class.getName());
    private static final String[] SERVICE_GROUP = {"Desporto", "IESD"};

    protected ServiceID serviceID;
    private JoinManager joinManager;
    private DiscoveryManagement discoveryManager;
    public RemoteBankImpl remoteBankImpl;

    public BankServicesRegistration(String bankName) throws RemoteException {
        try {
            remoteBankImpl = new RemoteBankImpl();
            remoteBankImpl.remoteBankProxy = (RemoteBank) new JrmpExporter().export(remoteBankImpl);

            // it is possible to add unicast references
            LookupLocator[] lookupLocators = null; // {new LookupLocator("jini://<host>")};
            ServiceInfo serviceInfo = new ServiceInfo(bankName, "ISEL-ADEETC", "iesd", "0.15", "Join Manager",
                    "SN-121212");
            Entry[] entries = new Entry[]{serviceInfo};

            // Setup LookupDiscoveryManager(look for reggie)
            this.discoveryManager = new LookupDiscoveryManager(SERVICE_GROUP, lookupLocators, null);

            // Service Registration
            this.joinManager = new JoinManager(remoteBankImpl.remoteBankProxy, entries, this, discoveryManager, null);

            LOGGER.info("Bank service registered: " + bankName + ": " + remoteBankImpl.remoteBankProxy);

        } catch (ExportException ex) {
            LOGGER.log(Level.SEVERE, "Failure to export", ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Failure while create LookupDiscovery...", ex);
        }
        System.out.println("A new BankService was started ...");
    }

    public void serviceIDNotify(ServiceID serviceID) {
        LOGGER.finest("ServiceID: " + serviceID);
        LOGGER.finest("LeaseRenewalManager: " + joinManager.getLeaseRenewalManager());
        ServiceRegistrar[] serviceRegistrars = joinManager.getJoinSet();
        LOGGER.finest("Number of serviceRegistrars: " + serviceRegistrars.length);
        for (ServiceRegistrar serviceRegistrar : serviceRegistrars) {
            LOGGER.finest("ServiceRegistrar: " + serviceRegistrar.getServiceID());
        }
    }
}
