package isos.tutorial.isyiesd.cestm.sertm;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.UUID;

@SuppressWarnings("serial")
public class TransactionManager extends UnicastRemoteObject implements ITransactionManager {
	private static int svcPort = 8000;
	private static Scanner scan = new Scanner(System.in);
	private static String[] tokens = new String[20];
	private int size = 0;
	protected TransactionManager() throws RemoteException {
		super(8001);
		System.out.println("Transaction Manager started.");
	}

	
	public static void main(String[] args) {
		try {
			Registry registry = LocateRegistry.createRegistry(svcPort);
			registry.rebind("TransactionManager", new TransactionManager());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Press enter to stop.");
		scan.nextLine(); scan.close();
		System.out.println("Transaction Manager stoped.");
	}
	
	@Override
	public String beginTransaction() {
		String XID = UUID.randomUUID().toString();
		tokens[size++] = XID;
		System.out.println("beginTransaction: "+XID);
		return XID;
	}

	@Override
	public void endTransaction(String token) {
		// TODO Auto-generated method stub
		System.out.println("endTransaction: "+token);
	}

	@Override
	public void abortTransaction(String token) {
		System.out.println("abortTransaction "+token);		
	}

	@Override
	public void registerServer(String token, String serverID) {
		System.out.println("registerServer: "+token+" "+serverID);		
	}
}
