package isos.tutorial.isyiesd.cestm.sertm;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ITransactionManager extends Remote {
	public String beginTransaction() throws RemoteException;
	public void endTransaction(String token) throws RemoteException;
	public void abortTransaction(String token) throws RemoteException;
	
	public void registerServer(String token, String serverID) throws RemoteException;
}