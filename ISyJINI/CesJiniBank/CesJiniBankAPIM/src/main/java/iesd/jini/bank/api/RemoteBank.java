package iesd.jini.bank.api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import net.jini.core.transaction.server.TransactionManager;
import net.jini.core.transaction.server.TransactionParticipant;

public interface RemoteBank extends Remote, TransactionParticipant {

    public void deposit(
            TransactionManager transactionManager,
            long transactionID,
            long accountID,
            long amount) throws RemoteException;

    public long withdraw(
            TransactionManager transactionManager,
            long transactionID,
            long accountID,
            long amount) throws RemoteException;

    public long read(
            TransactionManager transactionManager,
            long transactionID,
            long accountID) throws RemoteException;

    public long write(
            TransactionManager transactionManager,
            long transactionID,
            long accountID,
            long amount) throws RemoteException;

    public long getTotalAmountsOfCredits() throws RemoteException;

    public long getTotalAmountsOfWithdraws() throws RemoteException;

    public ArrayList<Account> getAccounts() throws RemoteException;

    public Account getAccount(long accountID) throws RemoteException;
}
