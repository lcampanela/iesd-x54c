package iesd.jini.helloworldimpl;

import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mdeos.jini.helper.ConfigureJiniFramework;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class HelloWorldMain implements BundleActivator {

    private final static Logger LOGGER = Logger.getLogger(HelloWorldMain.class.getName());

    public static void main(String[] args) throws RemoteException {

        ConfigureJiniFramework.setupLoggingConfig();
        LOGGER.log(Level.INFO, "Starting HelloWorldMain...");
        try {

            //COPY EMBEDDED DIRECTORY 'config' to ''
            ConfigureJiniFramework.copyDefaulEmbeddedDirToDefaultFileSystemDir();

            ConfigureJiniFramework.setSecurity(
                    ConfigureJiniFramework.JINI_RUNTIME_CONFDIR + "/HelloWorldService.policy");

            System.setProperty("java.rmi.server.hostname", ConfigureJiniFramework.getIpv4NonLoopbackAddress());
            ConfigureJiniFramework.setServerCodebase();

            // REGIST HELLO 
            new HelloWorldRegistration();
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
            Logger.getLogger(HelloWorldMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HelloWorldMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Starting HelloWorldMain bundle...");
		HelloWorldMain.main(null);		
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping HelloWorldMain bundle...");
	}
}
