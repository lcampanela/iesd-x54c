package iesd.jini.helloworld.api;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author iesd
 */
public interface HelloWorld extends Remote {

    public String sayHello(String msg) throws RemoteException;
}
