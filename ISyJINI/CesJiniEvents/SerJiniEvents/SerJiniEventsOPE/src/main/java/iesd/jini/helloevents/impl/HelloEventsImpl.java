package iesd.jini.helloevents.impl;

import java.rmi.RemoteException;

import javax.swing.event.EventListenerList;

import iesd.jini.helloevents.api.HelloEvents;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;

/**
 * @author iesd
 */
public class HelloEventsImpl implements HelloEvents {

    private static final Logger LOGGER = Logger.getLogger(HelloEventsImpl.class.getName());

    private EventListenerList eventListenerList = new EventListenerList();

    public HelloEventsImpl() {
        LOGGER.fine("Constructor of HelloEventsImpl()...");
    }

    @Override
    public EventRegistration addRemoteListener(RemoteEventListener listener)
            throws RemoteException {
        eventListenerList.add(RemoteEventListener.class, listener);
        LOGGER.fine("Added listener: " + listener);
        LOGGER.fine("RemoteEventListener Count: " + eventListenerList.getListenerCount());
        return new EventRegistration(0, this, null, 0);
    }

    @Override
    public void removeRemoteListener(RemoteEventListener remoteEventListener) throws RemoteException {
        LOGGER.fine("RemoteEventListener Count: " + eventListenerList.getListenerCount());
        LOGGER.fine("Remove listener: " + remoteEventListener);
        eventListenerList.remove(RemoteEventListener.class, remoteEventListener);
        LOGGER.fine("RemoteEventListener Count: " + eventListenerList.getListenerCount());
    }

    @Override
    public void publishEvent(RemoteEvent remoteEvent) {
        RemoteEventListener[] listeners = eventListenerList.getListeners(RemoteEventListener.class);
        LOGGER.fine("RemoteEvent = " + remoteEvent);
        LOGGER.fine("Notify EventListenerList count = " + eventListenerList.getListenerCount());
        for (RemoteEventListener listener : listeners) {
            LOGGER.fine("Invoke listener:" + listener);
            try {
                listener.notify(remoteEvent);
            } catch (UnknownEventException ex) {
                LOGGER.log(Level.WARNING, "Exception while invoke listener: ", ex);
            } catch (RemoteException ex) {
                eventListenerList.remove(RemoteEventListener.class, listener);
                LOGGER.fine("Remove listener: " + listener);
            }
        }
    }
}
