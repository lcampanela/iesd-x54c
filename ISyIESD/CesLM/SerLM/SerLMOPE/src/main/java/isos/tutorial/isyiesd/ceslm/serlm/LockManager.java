package isos.tutorial.isyiesd.ceslm.serlm;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

@SuppressWarnings("serial")
public class LockManager extends UnicastRemoteObject implements ILockManager {
	private static final int svcPort = 8000;	
	private static Scanner scan = new Scanner(System.in);
	protected LockManager() throws RemoteException {
		//super();
		System.out.println("Lock Manager started.");
	}

	public static void main(String[] args) {
		try {
			Registry registry = LocateRegistry.getRegistry(svcPort);
			registry.rebind("LockManager", new LockManager());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.out.println("Press enter to stop.");
		scan.nextLine(); scan.close();
		System.out.println("Lock Manager stoped.");
	}

	@Override
	public void getLocks(String token) {
		System.out.println("Locks requested.");
	}

	@Override
	public void releaseLocks(String token) {
		// TODO Auto-generated method stub
		
	}

}
