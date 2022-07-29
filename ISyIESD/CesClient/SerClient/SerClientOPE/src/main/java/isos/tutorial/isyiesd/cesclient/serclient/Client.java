package isos.tutorial.isyiesd.cesclient.serclient;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import isos.tutorial.isyiesd.ceslm.serlm.ILockManager;
import isos.tutorial.isyiesd.cestm.sertm.ITransactionManager;

public class Client {
	private static final int svcPort = 8000;
	static ITransactionManager tm;
	static ILockManager lm;
	public static void main(String[] args) {
		try {
			try {
				Registry registry = LocateRegistry.getRegistry(svcPort);
				tm = (ITransactionManager) registry.lookup("TransactionManager");
				lm = (ILockManager) registry.lookup("LockManager");
				// chamada de métodos remotos
				String XID = tm.beginTransaction();
				System.out.println(XID);
				lm.getLocks(XID);
				System.out.println("Locks requested.");
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
