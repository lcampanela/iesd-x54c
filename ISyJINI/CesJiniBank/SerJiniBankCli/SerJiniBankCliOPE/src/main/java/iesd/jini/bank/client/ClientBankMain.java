package iesd.jini.bank.client;

import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mdeos.jini.helper.ConfigureJiniFramework;
import org.mdeos.jini.helper.FindService;

import iesd.jini.bank.api.Account;
import iesd.jini.bank.api.RemoteBank;
import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.CannotCommitException;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.lookup.entry.ServiceInfo;

public class ClientBankMain {

    TransactionManager transactionManager;
    static ClientBankMain clientBank;
    static RemoteBank remoteBank_src;
    static RemoteBank remoteBank_dest;
    String bankId_src = "CGD";
    static int accountId_src = 444;
    String bankId_dest = "BPI";
    static int accountId_dest = 222;
    long amountToTransfer = 100;

    public static void main(String[] args) {
        System.out.println("Starting ClientBank:main()...");
        try {
            // COPY EMBEDDED DIRECTORY 'config' to ''
            ConfigureJiniFramework.copyDefaulEmbeddedDirToDefaultFileSystemDir();

            ConfigureJiniFramework.setSecurity(ConfigureJiniFramework.JINI_RUNTIME_CONFDIR + "/ClientBank.policy");

            // Initialize Find Services
            FindService.InitFindService();

            // For the case a service is registered, there is a need to set the codebase
            //ConfigureJiniFramework.setServerCodebase();
            // set codebaseOnly flag for resolution of class loading from codebase only
            ConfigureJiniFramework.enableRemoteCodebase();;

            clientBank = new ClientBankMain();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (URISyntaxException ex) {
            Logger.getLogger(ClientBankMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClientBankMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LeaseDeniedException e) {
            e.printStackTrace();
        } catch (UnknownTransactionException e) {
            e.printStackTrace();
        } catch (CannotCommitException e) {
            e.printStackTrace();
        }
    }

    public ClientBankMain()
            throws LeaseDeniedException, RemoteException, UnknownTransactionException, CannotCommitException {
        System.out.println("Em construtor ClientBank()...");

        transactionManager = (TransactionManager) FindService.ServiceLookup(TransactionManager.class);

        Entry entries_src[] = {new ServiceInfo(bankId_src, null, null, null, null, null)};
        Entry entries_dest[] = {new ServiceInfo(bankId_dest, null, null, null, null, null)};
        remoteBank_src = (RemoteBank) FindService.ServiceLookup(RemoteBank.class, entries_src);
        remoteBank_dest = (RemoteBank) FindService.ServiceLookup(RemoteBank.class, entries_dest);
        System.out.println("remoteBank_src = " + remoteBank_src.toString());
        System.out.println("remoteBank_dest = " + remoteBank_dest.toString());
        ListBankAccounts(remoteBank_src, bankId_src);
        ListBankAccounts(remoteBank_dest, bankId_dest);

        Transference(remoteBank_src, remoteBank_dest);

        ListBankAccounts(remoteBank_src, bankId_src);
        ListBankAccounts(remoteBank_dest, bankId_dest);
    }

    private void ListBankAccounts(RemoteBank remoteBank, String bank) {
        ArrayList<Account> accounts = null;
        try {
            accounts = remoteBank.getAccounts();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("Lista de contas em " + bank);
        for (Account account : accounts) {
            System.out
                    .println(account.getAccountNumber() + "; " + account.getOwnersName() + "; " + account.getBalance());
        }
    }

    private void Transference(RemoteBank remoteBank_src, RemoteBank remoteBank_dest)
            throws LeaseDeniedException, RemoteException, UnknownTransactionException, CannotCommitException {
        // Begin transaction
        TransactionManager.Created transactionManagerCreated = transactionManager.create(Lease.FOREVER);
        // Operation 1
        remoteBank_src.withdraw(transactionManager, transactionManagerCreated.id, accountId_src, amountToTransfer);
        // Operation 2
        remoteBank_dest.deposit(transactionManager, transactionManagerCreated.id, accountId_dest, amountToTransfer);
        // End transaction
        transactionManager.commit(transactionManagerCreated.id);
    }
}
