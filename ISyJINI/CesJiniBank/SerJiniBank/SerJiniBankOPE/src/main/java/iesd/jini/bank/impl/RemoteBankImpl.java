package iesd.jini.bank.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import iesd.jini.bank.api.Account;
import iesd.jini.bank.api.RemoteBank;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.server.TransactionConstants;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.core.transaction.server.TransactionParticipant;

public class RemoteBankImpl implements RemoteBank {

    protected RemoteBank remoteBankProxy;
    protected long crashCount = 0;
    protected Hashtable<Long, Account> accounts = new Hashtable<Long, Account>();

    // temporary structure to support transactions coordination
    protected Hashtable<Long, DepositWithdraw> pendingDeposit = new Hashtable<Long, DepositWithdraw>();
    protected Hashtable<Long, DepositWithdraw> pendingWithdraw = new Hashtable<Long, DepositWithdraw>();

    public RemoteBankImpl() {
        System.out.println("Constructor of RemoteBankImpl()...");
        // Inicia base de contas bancarias
        accounts.put(new Long(222), new Account(222, "Antonio", 5000));
        accounts.put(new Long(333), new Account(333, "Maria", 5000));
        accounts.put(new Long(444), new Account(444, "Carlos", 5000));
        accounts.put(new Long(999), new Account(999, "Teresa", 5000));
    }

    // Helper classes - Begin
    class DepositWithdraw {

        long amount;
        long accountID;

        DepositWithdraw(long amount, long accountID) {
            // System.out.println(
            // "DepositWithdraw#ctor("+amount+", "+accountID+")" );
            this.amount = amount;
            this.accountID = accountID;
        }
    }

    class Checker {

        public long readAmount;
        public long writeAmount;

        public Checker(long readAmount) {
            this.readAmount = readAmount;
            this.writeAmount = -1;
        }
    }

    // Helper classes - End
    protected Hashtable<Long, Checker> operationsChecker = new Hashtable<Long, Checker>();

    public long getTotalAmountsOfCredits() throws RemoteException {
        long result = 0;

        synchronized (this.operationsChecker) {
            Enumeration<Checker> checkers = this.operationsChecker.elements();
            while (checkers.hasMoreElements()) {
                Checker currentChecker = checkers.nextElement();
                if (currentChecker.writeAmount > 0) {
                    if (currentChecker.writeAmount > currentChecker.readAmount) {
                        result += (currentChecker.writeAmount - currentChecker.readAmount);
                    }
                }
            }
        }

        System.out.println("getTotalAmountsOfCredits -> " + result);
        return result;
    }

    public long getTotalAmountsOfWithdraws() throws RemoteException {
        long result = 0;

        synchronized (this.operationsChecker) {
            Enumeration<Checker> checkers = this.operationsChecker.elements();
            while (checkers.hasMoreElements()) {
                Checker currentChecker = checkers.nextElement();
                if (currentChecker.writeAmount > 0) {
                    if (currentChecker.writeAmount < currentChecker.readAmount) {
                        result += (currentChecker.readAmount - currentChecker.writeAmount);
                    }
                }
            }
        }
        System.out.println("getTotalAmountsOfWithdraws -> " + result);
        return result;
    }

    // RemoteBank implementation
    public ArrayList<Account> getAccounts() throws RemoteException {
        // System.out.println("RemoteBankImpl#getAccounts");

        ArrayList<Account> _accounts = new ArrayList<Account>();
        for (Enumeration<Account> accountsEnum = accounts.elements(); accountsEnum.hasMoreElements();) {
            _accounts.add(accountsEnum.nextElement());
        }
        return _accounts;
    }

    public Account getAccount(long accountID) throws RemoteException {
        // System.out.println("RemoteBankImpl#getAccount("+accountID+")");
        return accounts.get(new Long(accountID));
    }

    public void deposit(TransactionManager transactionManager, long transactionID, long accountID, long amount)
            throws RemoteException {
        System.out.println("RemoteBankImpl#deposit(...)");
        try {
            transactionManager.join(transactionID, (TransactionParticipant) remoteBankProxy, crashCount);
            pendingDeposit.put(transactionID, new DepositWithdraw(amount, accountID));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long withdraw(TransactionManager transactionManager, long transactionID, long accountID, long amount)
            throws RemoteException {
        System.out.println("RemoteBankImpl#withdraw(...)");
        try {
            transactionManager.join(transactionID, (TransactionParticipant) remoteBankProxy, crashCount);
            pendingWithdraw.put(transactionID, new DepositWithdraw(amount, accountID));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return amount;
    }

    public long read(TransactionManager transactionManager, long transactionID, long accountID) throws RemoteException {
        System.out.println("RemoteBankImpl#read(...)");
        try {
            transactionManager.join(transactionID, (TransactionParticipant) remoteBankProxy, crashCount);
            Account account = this.accounts.get(new Long(accountID));
            long balance = account.getBalance();
            Checker ch = new Checker(balance);
            synchronized (this.operationsChecker) {
                this.operationsChecker.put(transactionID, ch);
            }
            return balance;
        } catch (Exception ex) {
            throw new RemoteException("Exception when reading account", ex);
        }
    }

    public long write(TransactionManager transactionManager, long transactionID, long accountID, long amount)
            throws RemoteException {
        System.out.println("RemoteBankImpl#write(...)");
        try {
            transactionManager.join(transactionID, (TransactionParticipant) remoteBankProxy, crashCount);
            Account account = this.accounts.get(new Long(accountID));
            account.setBalance(amount);
            synchronized (this.operationsChecker) {
                Checker ch = this.operationsChecker.get(transactionID);
                ch.writeAmount = amount;
            }
            return amount;
        } catch (Exception ex) {
            throw new RemoteException("Exception when writing account", ex);
        }
    }

    /*
	 * TransactionParticipant XA methods
     */
    public void abort(TransactionManager transactionManager, long transactionID)
            throws UnknownTransactionException, RemoteException {
        System.out.println("RemoteBankImpl#abort(...)");
    }

    public void commit(TransactionManager transactionManager, long transactionID)
            throws UnknownTransactionException, RemoteException {

        System.out.println("RemoteBankImpl#commit(...)");
        pendingDeposit.remove(transactionID);
        pendingWithdraw.remove(transactionID);
    }

    public int prepare(TransactionManager transactionManager, long transactionID)
            throws UnknownTransactionException, RemoteException {
        System.out.println("RemoteBankImpl#prepare(...)");
        DepositWithdraw depositWithdraw = pendingDeposit.get(transactionID);
        if (depositWithdraw != null) {
            System.out.println("Preparing deposit...");
            Account account = accounts.get(depositWithdraw.accountID);
            account.setBalance(account.getBalance() + depositWithdraw.amount);
            System.out.println("depositWithdraw.accountID = " + depositWithdraw.accountID);
        }
        depositWithdraw = pendingWithdraw.get(transactionID);
        if (depositWithdraw != null) {
            System.out.println("Preparing withdraw...");
            Account account = accounts.get(depositWithdraw.accountID);
            account.setBalance(account.getBalance() - depositWithdraw.amount);
            System.out.println("depositWithdraw.accountID = " + depositWithdraw.accountID);
        }
        return TransactionConstants.PREPARED;
    }

    public int prepareAndCommit(TransactionManager transactionManager, long transactionID)
            throws UnknownTransactionException, RemoteException {
        System.out.println("RemoteBankImpl#prepareAndCommit(...)");
        int resultado = prepare(transactionManager, transactionID);
        if (resultado == TransactionConstants.PREPARED) {
            commit(transactionManager, transactionID);
            resultado = TransactionConstants.COMMITTED;
        }
        return resultado;
    }
}
