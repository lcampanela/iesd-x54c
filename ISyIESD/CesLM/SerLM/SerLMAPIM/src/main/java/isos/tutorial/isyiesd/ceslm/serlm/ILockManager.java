package isos.tutorial.isyiesd.ceslm.serlm;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ILockManager extends Remote {
	public void getLocks(String token) throws RemoteException;
	public void releaseLocks(String token) throws RemoteException;
}
