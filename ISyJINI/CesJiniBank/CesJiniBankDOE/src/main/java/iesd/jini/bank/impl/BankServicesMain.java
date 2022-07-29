package iesd.jini.bank.impl;

import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mdeos.jini.helper.ConfigureJiniFramework;
import org.mdeos.jini.helper.FindService;

import net.jini.core.lookup.ServiceID;

public class BankServicesMain {

    protected ServiceID serviceID = null;

    public static void main(String[] args) throws RemoteException {
        System.out.println("BankServicesMain:main().");
        // To guarantee that the services are bound to a remote accessible
        // IP address and not the loopback 127.0.1.1
        try {
            //COPY EMBEDDED DIRECTORY 'config' to ''
            ConfigureJiniFramework.copyDefaulEmbeddedDirToDefaultFileSystemDir();

            ConfigureJiniFramework.setSecurity(
                    ConfigureJiniFramework.JINI_RUNTIME_CONFDIR + "/BankService.policy");
            ConfigureJiniFramework.setServerCodebase();

            FindService.InitFindService();

            // Create initial banks
            new BankServicesRegistration("CGD");
            new BankServicesRegistration("BPI");

            Object keepAlive = new Object();
            synchronized (keepAlive) {
                try {
                    keepAlive.wait();
                } catch (InterruptedException e) {
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (URISyntaxException ex) {
            Logger.getLogger(BankServicesMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BankServicesMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
