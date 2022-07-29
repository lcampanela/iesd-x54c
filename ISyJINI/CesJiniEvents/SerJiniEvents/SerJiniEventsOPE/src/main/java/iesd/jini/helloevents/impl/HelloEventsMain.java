package iesd.jini.helloevents.impl;

import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mdeos.jini.helper.ConfigureJiniFramework;

import net.jini.core.lookup.ServiceID;

public class HelloEventsMain {

    protected ServiceID serviceID = null;

    public static void main(String[] args) throws RemoteException {

        ConfigureJiniFramework.setupLoggingConfig();
        try {
            ConfigureJiniFramework.copyDefaulEmbeddedDirToDefaultFileSystemDir();

            ConfigureJiniFramework.setSecurity(
                    ConfigureJiniFramework.JINI_RUNTIME_CONFDIR + "/HelloEventsService.policy");

            ConfigureJiniFramework.setServerHostname(ConfigureJiniFramework.getIpv4NonLoopbackAddress());
            ConfigureJiniFramework.setServerCodebase();

            //CREATE 
            new HelloEventsRegistration();
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
            Logger.getLogger(HelloEventsMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HelloEventsMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
