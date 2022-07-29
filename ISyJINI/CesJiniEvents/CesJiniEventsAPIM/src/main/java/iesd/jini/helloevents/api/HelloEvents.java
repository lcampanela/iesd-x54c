package iesd.jini.helloevents.api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import net.jini.core.event.EventRegistration;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;

/**
 *
 * @author iesd
 */
public interface HelloEvents extends Remote {

    public EventRegistration addRemoteListener(RemoteEventListener remoteEventListener) throws RemoteException;

    public void removeRemoteListener(RemoteEventListener remoteEventListener) throws RemoteException;

    public void publishEvent(RemoteEvent remoteEvent) throws RemoteException;
}
