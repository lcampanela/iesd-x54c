package iesd.jini.bank.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.mdeos.jini.helper.ConfigureJiniFramework;
import org.mdeos.jini.helper.FindService;

import iesd.jini.bank.api.Account;
import iesd.jini.bank.api.RemoteBank;
import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.ServiceDiscoveryEvent;
import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.lookup.entry.ServiceInfo;

public class MoneyTransferMain implements ServiceDiscoveryListener {

    // Helper classes
    class BankItem {

        private String serviceFriendlyName;

        private RemoteBank bank;

        public BankItem(String serviceFriendlyName, RemoteBank bank) {
            this.serviceFriendlyName = serviceFriendlyName;
            this.bank = bank;
        }

        public RemoteBank getBank() {
            return this.bank;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof BankItem)) {
                return false;
            }

            return this.serviceFriendlyName.equals(((BankItem) obj).serviceFriendlyName);
        }

        @Override
        public String toString() {
            return this.serviceFriendlyName;
        }
    }

    // Private attributes
    private DefaultListModel<BankItem> modelBanks;
    private DefaultListModel<Account> accountsSourceModel;
    private DefaultListModel<Account> accountsDestinationModel;
    private LookupDiscovery lookupDiscovery = null; // @jve:decl-index=0:
    private ServiceDiscoveryManager serviceDiscoveryManager = null;
    private TransactionManager transactionManager = null;
    private RemoteBank sourceBank;
    private RemoteBank destinationBank;

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            // COPY EMBEDDED DIRECTORY 'config' to ''
            ConfigureJiniFramework.copyDefaulEmbeddedDirToDefaultFileSystemDir();
            ConfigureJiniFramework.setSecurity(ConfigureJiniFramework.JINI_RUNTIME_CONFDIR + "/ClientBank.policy");

            FindService.InitFindService();
            // For the case a service is registered, there 
            //ConfigureJiniFramework.setServerCodebase();

            // set codebaseOnly flag for resolution of class loading from codebase only
            ConfigureJiniFramework.enableRemoteCodebase();;

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (URISyntaxException ex) {
            Logger.getLogger(MoneyTransferMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MoneyTransferMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MoneyTransferMain application = new MoneyTransferMain();
                application.getJFrame().setVisible(true);
            }
        });
    }

    // Private methods
    private void enableComponent(final JComponent comp, final boolean state) {
        Runnable update = new Runnable() {
            @Override
            public void run() {
                comp.setEnabled(state);
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            update.run();
        } else {
            SwingUtilities.invokeLater(update);
        }
    }

    private void addMessage(final String message) {
        Runnable update = new Runnable() {
            @Override
            public void run() {
                jTextAreaMessages.append(message + "\n\r");
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            update.run();
        } else {
            SwingUtilities.invokeLater(update);
        }
    }

    private void init() {
        try {
            this.modelBanks = new DefaultListModel<BankItem>();
            this.accountsSourceModel = new DefaultListModel<Account>();
            this.accountsDestinationModel = new DefaultListModel<Account>();
            this.jListAvailableBanks.setModel(this.modelBanks);
            this.jListSourceAccounts.setModel(this.accountsSourceModel);
            this.jListDestinationAccounts.setModel(this.accountsDestinationModel);

            this.lookupDiscovery = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
            this.serviceDiscoveryManager = new ServiceDiscoveryManager(this.lookupDiscovery, new LeaseRenewalManager());

            ServiceTemplate template = new ServiceTemplate(null, new Class[]{RemoteBank.class}, null);
            this.serviceDiscoveryManager.createLookupCache(template, null, this);

            enableComponent(this.jButtonInit, false);
            enableComponent(this.jButtonFindTransactionManager, true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this.getJFrame(), "Error ont init", ex.getMessage(),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void findTransactionManager() {
        this.transactionManager = (TransactionManager) FindService.ServiceLookup(TransactionManager.class);
        enableComponent(this.jButtonFindTransactionManager, false);
        enableComponent(this.jButtonDoTransfer, true);
    }

    private void updateAccounts(String bankName, RemoteBank bank, DefaultListModel<Account> model) {
        try {
            model.clear();

            for (Account acc : bank.getAccounts()) {
                model.addElement(acc);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this.getJFrame(),
                    "Error when getting accounts from bank: \"" + bankName + "\"", ex.getMessage(),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private RemoteBank updateBank(DefaultListModel<Account> model, JTextField bank) {
        RemoteBank result = null;

        BankItem selBank = this.jListAvailableBanks.getSelectedValue();
        if (selBank != null) {
            bank.setText(selBank.serviceFriendlyName);
            updateAccounts(selBank.serviceFriendlyName, selBank.bank, model);
            result = selBank.bank;
        }
        return result;
    }

    private void updateSourceBank() {
        this.sourceBank = this.updateBank(accountsSourceModel, this.jTextFieldSourceBankName);
    }

    private void updateDestinationBank() {
        this.destinationBank = this.updateBank(accountsDestinationModel, this.jTextFieldDestinationBankName);
    }

    private void doTransfer() {
        try {
            long amountToTransfer = Long.parseLong(this.jTextFieldAmount.getText());

            if (this.sourceBank == null || this.destinationBank == null) {
                JOptionPane.showMessageDialog(this.getJFrame(), "Source and destination banks can't be null",
                        "Error when making transaction", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Account src = this.jListSourceAccounts.getSelectedValue();
            Account dst = this.jListDestinationAccounts.getSelectedValue();
            if (src == null || dst == null) {
                JOptionPane.showMessageDialog(this.getJFrame(), "Source and destination accounts can't be null",
                        "Error when making transaction", JOptionPane.ERROR_MESSAGE);
                return;
            }
            this.jTextAreaMessages.setText(null);
            // Begin Transaction
            TransactionManager.Created transaction = this.transactionManager.create(Lease.FOREVER);
            this.addMessage("Transaction created with id: " + transaction.id);
            new LeaseRenewalManager().renewUntil(transaction.lease, Lease.FOREVER, null);
            long sleepTime = this.jSliderSleepTime.getModel().getValue();
            // Transactions actions
            this.addMessage("Going to read balance from source account...");
            long temp1 = sourceBank.read(this.transactionManager, transaction.id, src.getAccountNumber());
            this.addMessage("Current balance is: " + temp1);
            this.addMessage("Going to sleep for " + sleepTime + " miliseconds before write source account...");
            Thread.sleep(sleepTime);
            this.addMessage("Going to write balance to source account...");
            sourceBank.write(this.transactionManager, transaction.id, src.getAccountNumber(), temp1 - amountToTransfer);
            this.addMessage("Going to read balance from destination account...");
            long temp2 = destinationBank.read(this.transactionManager, transaction.id, dst.getAccountNumber());
            this.addMessage("Current balance is: " + temp2);
            this.addMessage("Going to sleep for " + sleepTime + " miliseconds before write destination account...");
            Thread.sleep(sleepTime);
            this.addMessage("Going to write balance to destination account...");
            destinationBank.write(this.transactionManager, transaction.id, dst.getAccountNumber(),
                    temp2 + amountToTransfer);
            this.addMessage("Going to commit transaction...");
            // Commit transaction
            this.transactionManager.commit(transaction.id);
            this.addMessage("Transaction is done. Going to update GUI...");
            int idxSrc = this.jListSourceAccounts.getSelectedIndex();
            int idxDst = this.jListDestinationAccounts.getSelectedIndex();
            this.updateAccounts(this.jTextFieldSourceBankName.getText(), this.sourceBank, this.accountsSourceModel);
            this.updateAccounts(this.jTextFieldDestinationBankName.getText(), this.destinationBank,
                    this.accountsDestinationModel);
            this.jListSourceAccounts.setSelectedIndex(idxSrc);
            this.jListDestinationAccounts.setSelectedIndex(idxDst);
        } catch (Exception ex) {
            String message;

            if (ex.getLocalizedMessage() == null) {
                message = ex.toString();
            } else {
                message = ex.getLocalizedMessage();
            }
            JOptionPane.showMessageDialog(this.getJFrame(), message, "Error when making transaction",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Implementation of ServiceDiscoveryListener
    public void serviceAdded(ServiceDiscoveryEvent serviceDiscoveryEvent) {
        ServiceItem item = serviceDiscoveryEvent.getPostEventServiceItem();

        Entry[] serviceEntries = item.attributeSets;
        String serviceFriendlyName = "N.A";

        for (int idxEntries = 0; idxEntries < serviceEntries.length; ++idxEntries) {
            if (serviceEntries[idxEntries] instanceof ServiceInfo) {
                serviceFriendlyName = ((ServiceInfo) serviceEntries[idxEntries]).name;
                break;
            }
        }

        final BankItem sb = new BankItem(serviceFriendlyName, (RemoteBank) item.service);

        Runnable update = new Runnable() {
            @Override
            public void run() {
                modelBanks.addElement(sb);
            }
        };
        SwingUtilities.invokeLater(update);
    }

    public void serviceChanged(ServiceDiscoveryEvent serviceDiscoveryEvent) {
        System.out.println("MoneyTransfer#serviceChanged(...)");
    }

    public void serviceRemoved(ServiceDiscoveryEvent serviceDiscoveryEvent) {
        ServiceItem item = serviceDiscoveryEvent.getPreEventServiceItem();

        Entry[] serviceEntries = item.attributeSets;
        String serviceFriendlyName = "N.A";

        for (int idxEntries = 0; idxEntries < serviceEntries.length; ++idxEntries) {
            if (serviceEntries[idxEntries] instanceof ServiceInfo) {
                serviceFriendlyName = ((ServiceInfo) serviceEntries[idxEntries]).name;
                break;
            }
        }

        BankItem sb = new BankItem(serviceFriendlyName, null);

        if (this.modelBanks.contains(sb)) {
            this.modelBanks.removeElement(sb);

            if (this.sourceBank != null) {
                if (this.sourceBank.equals((RemoteBank) item.service)) {
                    this.sourceBank = null;
                    this.jTextFieldSourceBankName.setText("");
                    this.accountsSourceModel.clear();
                }
            }
            if (this.destinationBank != null) {
                if (this.destinationBank.equals((RemoteBank) item.service)) {
                    this.jTextFieldDestinationBankName.setText("");
                    this.destinationBank = null;
                    this.accountsDestinationModel.clear();
                }
            }
        }
    }

    private JFrame jFrame = null; // @jve:decl-index=0:visual-constraint="10,10"
    private JPanel jContentPane = null;
    private JMenuBar jJMenuBar = null;
    private JMenu fileMenu = null;
    private JMenuItem exitMenuItem = null;
    private JMenuItem saveMenuItem = null;
    private JPanel jPanelAvailableBanks = null;
    private JScrollPane jScrollPaneAvailableBanks = null;
    private JList<BankItem> jListAvailableBanks = null;
    private JButton jButtonSetSource = null;
    private JButton jButtonSetDestination = null;
    private JPanel jPanelSourceBank = null;
    private JTextField jTextFieldSourceBankName = null;
    private JPanel jPanelDestinationBank = null;
    private JTextField jTextFieldDestinationBankName = null;
    private JScrollPane jScrollPaneSourceAccounts = null;
    private JScrollPane jScrollPaneDestinationAccounts = null;
    private JList<Account> jListSourceAccounts = null;
    private JList<Account> jListDestinationAccounts = null;
    private JPanel jPanelTransferDetails = null;
    private JLabel jLabelTransferAmount = null;
    private JTextField jTextFieldAmount = null;
    private JButton jButtonDoTransfer = null;
    private JPanel jPanelInitTasks = null;
    private JButton jButtonInit = null;
    private JButton jButtonFindTransactionManager = null;
    private JPanel jPanelMessages = null;
    private JScrollPane jScrollPaneMessages = null;
    private JTextArea jTextAreaMessages = null;
    private JLabel jLabelSleepTime = null;
    private JSlider jSliderSleepTime = null;

    /**
     * This method initializes jPanelAvailableBanks
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanelAvailableBanks() {
        if (jPanelAvailableBanks == null) {
            jPanelAvailableBanks = new JPanel();
            jPanelAvailableBanks.setLayout(null);
            jPanelAvailableBanks.setBounds(new Rectangle(5, 5, 221, 291));
            jPanelAvailableBanks.setBorder(
                    BorderFactory.createTitledBorder(null, "Available Banks", TitledBorder.DEFAULT_JUSTIFICATION,
                            TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            jPanelAvailableBanks.add(getJScrollPaneAvailableBanks(), null);
        }
        return jPanelAvailableBanks;
    }

    /**
     * This method initializes jScrollPaneAvailableBanks
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPaneAvailableBanks() {
        if (jScrollPaneAvailableBanks == null) {
            jScrollPaneAvailableBanks = new JScrollPane();
            jScrollPaneAvailableBanks.setBounds(new Rectangle(15, 25, 191, 251));
            jScrollPaneAvailableBanks.setViewportView(getJListAvailableBanks());
        }
        return jScrollPaneAvailableBanks;
    }

    /**
     * This method initializes jListAvailableBanks
     *
     * @return javax.swing.JList
     */
    private JList<BankItem> getJListAvailableBanks() {
        if (jListAvailableBanks == null) {
            jListAvailableBanks = new JList<BankItem>();
        }
        return jListAvailableBanks;
    }

    /**
     * This method initializes jButtonSetSource
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonSetSource() {
        if (jButtonSetSource == null) {
            jButtonSetSource = new JButton();
            jButtonSetSource.setText(">");
            jButtonSetSource.setBounds(new Rectangle(5, 75, 41, 21));
            jButtonSetSource.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateSourceBank();
                }
            });
        }
        return jButtonSetSource;
    }

    /**
     * This method initializes jButtonSetDestination
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonSetDestination() {
        if (jButtonSetDestination == null) {
            jButtonSetDestination = new JButton();
            jButtonSetDestination.setText(">");
            jButtonSetDestination.setBounds(new Rectangle(5, 65, 41, 21));
            jButtonSetDestination.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateDestinationBank();
                }
            });
        }
        return jButtonSetDestination;
    }

    /**
     * This method initializes jPanelSourceBank
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanelSourceBank() {
        if (jPanelSourceBank == null) {
            jPanelSourceBank = new JPanel();
            jPanelSourceBank.setLayout(null);
            jPanelSourceBank.setBounds(new Rectangle(235, 5, 221, 141));
            jPanelSourceBank
                    .setBorder(BorderFactory.createTitledBorder(null, "Source Bank", TitledBorder.DEFAULT_JUSTIFICATION,
                            TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            jPanelSourceBank.add(getJButtonSetSource(), null);
            jPanelSourceBank.add(getJTextFieldSourceBankName(), null);
            jPanelSourceBank.add(getJScrollPaneSourceAccounts(), null);
        }
        return jPanelSourceBank;
    }

    /**
     * This method initializes jTextFieldSourceBankName
     *
     * @return javax.swing.JTextField
     */
    private JTextField getJTextFieldSourceBankName() {
        if (jTextFieldSourceBankName == null) {
            jTextFieldSourceBankName = new JTextField();
            jTextFieldSourceBankName.setBounds(new Rectangle(55, 15, 161, 21));
            jTextFieldSourceBankName.setEditable(false);
        }
        return jTextFieldSourceBankName;
    }

    /**
     * This method initializes jPanelDestinationAccount
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanelDestinationAccount2() {
        if (jPanelDestinationBank == null) {
            jPanelDestinationBank = new JPanel();
            jPanelDestinationBank.setLayout(null);
            jPanelDestinationBank.setBounds(new Rectangle(235, 155, 221, 141));
            jPanelDestinationBank.setBorder(
                    BorderFactory.createTitledBorder(null, "Destination Bank", TitledBorder.DEFAULT_JUSTIFICATION,
                            TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            jPanelDestinationBank.add(getJButtonSetDestination(), null);
            jPanelDestinationBank.add(getJTextFieldDestinationBankName(), null);
            jPanelDestinationBank.add(getJScrollPaneDestinationAccounts(), null);
        }
        return jPanelDestinationBank;
    }

    /**
     * This method initializes jTextFieldDestinationBankName
     *
     * @return javax.swing.JTextField
     */
    private JTextField getJTextFieldDestinationBankName() {
        if (jTextFieldDestinationBankName == null) {
            jTextFieldDestinationBankName = new JTextField();
            jTextFieldDestinationBankName.setBounds(new Rectangle(55, 15, 161, 21));
            jTextFieldDestinationBankName.setEditable(false);
        }
        return jTextFieldDestinationBankName;
    }

    /**
     * This method initializes jScrollPaneSourceAccounts
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPaneSourceAccounts() {
        if (jScrollPaneSourceAccounts == null) {
            jScrollPaneSourceAccounts = new JScrollPane();
            jScrollPaneSourceAccounts.setBounds(new Rectangle(55, 45, 161, 91));
            jScrollPaneSourceAccounts.setViewportView(getJListSourceAccounts());
        }
        return jScrollPaneSourceAccounts;
    }

    /**
     * This method initializes jScrollPaneDestinationAccounts
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPaneDestinationAccounts() {
        if (jScrollPaneDestinationAccounts == null) {
            jScrollPaneDestinationAccounts = new JScrollPane();
            jScrollPaneDestinationAccounts.setBounds(new Rectangle(55, 45, 161, 91));
            jScrollPaneDestinationAccounts.setViewportView(getJListDestinationAccounts());
        }
        return jScrollPaneDestinationAccounts;
    }

    /**
     * This method initializes jListSourceAccounts
     *
     * @return javax.swing.JList
     */
    private JList<Account> getJListSourceAccounts() {
        if (jListSourceAccounts == null) {
            jListSourceAccounts = new JList<Account>();
        }
        return jListSourceAccounts;
    }

    /**
     * This method initializes jListDestinationAccounts
     *
     * @return javax.swing.JList
     */
    private JList<Account> getJListDestinationAccounts() {
        if (jListDestinationAccounts == null) {
            jListDestinationAccounts = new JList<Account>();
        }
        return jListDestinationAccounts;
    }

    /**
     * This method initializes jPanelTransferDetails
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanelTransferDetails() {
        if (jPanelTransferDetails == null) {
            jLabelSleepTime = new JLabel();
            jLabelSleepTime.setBounds(new Rectangle(235, 25, 441, 21));
            jLabelSleepTime.setText("Sleep time between operations");
            jLabelTransferAmount = new JLabel();
            jLabelTransferAmount.setBounds(new Rectangle(15, 25, 121, 21));
            jLabelTransferAmount.setText("Amount to Transfer");
            jPanelTransferDetails = new JPanel();
            jPanelTransferDetails.setLayout(null);
            jPanelTransferDetails.setBounds(new Rectangle(5, 305, 681, 121));
            jPanelTransferDetails.setBorder(
                    BorderFactory.createTitledBorder(null, "Transfer Details", TitledBorder.DEFAULT_JUSTIFICATION,
                            TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            jPanelTransferDetails.add(jLabelTransferAmount, null);
            jPanelTransferDetails.add(getJTextFieldAmount(), null);
            jPanelTransferDetails.add(getJButtonDoTransfer(), null);
            jPanelTransferDetails.add(jLabelSleepTime, null);
            jPanelTransferDetails.add(getJSliderSleepTime(), null);
        }
        return jPanelTransferDetails;
    }

    /**
     * This method initializes jTextFieldAmount
     *
     * @return javax.swing.JTextField
     */
    private JTextField getJTextFieldAmount() {
        if (jTextFieldAmount == null) {
            jTextFieldAmount = new JTextField();
            jTextFieldAmount.setBounds(new Rectangle(145, 25, 81, 21));
        }
        return jTextFieldAmount;
    }

    /**
     * This method initializes jButtonDoTransfer
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonDoTransfer() {
        if (jButtonDoTransfer == null) {
            jButtonDoTransfer = new JButton();
            jButtonDoTransfer.setBounds(new Rectangle(15, 60, 211, 21));
            jButtonDoTransfer.setText("Transfer");
            jButtonDoTransfer.setEnabled(false);
            jButtonDoTransfer.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runnable worker = new Runnable() {
                        @Override
                        public void run() {
                            doTransfer();
                        }
                    };
                    Thread th = new Thread(worker);
                    th.start();
                }
            });
        }
        return jButtonDoTransfer;
    }

    /**
     * This method initializes jPanelInitTasks
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanelInitTasks() {
        if (jPanelInitTasks == null) {
            jPanelInitTasks = new JPanel();
            jPanelInitTasks.setLayout(null);
            jPanelInitTasks.setBounds(new Rectangle(465, 5, 221, 291));
            jPanelInitTasks
                    .setBorder(BorderFactory.createTitledBorder(null, "Init Tasks", TitledBorder.DEFAULT_JUSTIFICATION,
                            TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            jPanelInitTasks.add(getJButtonInitDiscoverBanks(), null);
            jPanelInitTasks.add(getJButtonFindTransactionManager(), null);
        }
        return jPanelInitTasks;
    }

    /**
     * This method initializes jButtonInitDiscoverBanks
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonInitDiscoverBanks() {
        if (jButtonInit == null) {
            jButtonInit = new JButton();
            jButtonInit.setBounds(new Rectangle(15, 25, 191, 21));
            jButtonInit.setText("Init");
            jButtonInit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runnable worker = new Runnable() {
                        @Override
                        public void run() {
                            init();
                        }
                    };
                    Thread th = new Thread(worker);
                    th.start();
                }
            });
        }
        return jButtonInit;
    }

    /**
     * This method initializes jButtonFindTransactionManager
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonFindTransactionManager() {
        if (jButtonFindTransactionManager == null) {
            jButtonFindTransactionManager = new JButton();
            jButtonFindTransactionManager.setBounds(new Rectangle(15, 55, 191, 21));
            jButtonFindTransactionManager.setEnabled(false);
            jButtonFindTransactionManager.setText("Find Transaction Manager");
            jButtonFindTransactionManager.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Runnable worker = new Runnable() {
                        @Override
                        public void run() {
                            findTransactionManager();
                        }
                    };
                    Thread th = new Thread(worker);
                    th.start();
                }
            });
        }
        return jButtonFindTransactionManager;
    }

    /**
     * This method initializes jPanelMessages
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanelMessages() {
        if (jPanelMessages == null) {
            jPanelMessages = new JPanel();
            jPanelMessages.setLayout(new BorderLayout());
            jPanelMessages.setBounds(new Rectangle(5, 435, 681, 181));
            jPanelMessages.setBorder(
                    BorderFactory.createTitledBorder(null, "Output Messages", TitledBorder.DEFAULT_JUSTIFICATION,
                            TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            jPanelMessages.add(getJScrollPaneMessages(), BorderLayout.CENTER);
        }
        return jPanelMessages;
    }

    /**
     * This method initializes jScrollPaneMessages
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPaneMessages() {
        if (jScrollPaneMessages == null) {
            jScrollPaneMessages = new JScrollPane();
            jScrollPaneMessages.setViewportView(getJTextAreaMessages());
        }
        return jScrollPaneMessages;
    }

    /**
     * This method initializes jTextAreaMessages
     *
     * @return javax.swing.JTextArea
     */
    private JTextArea getJTextAreaMessages() {
        if (jTextAreaMessages == null) {
            jTextAreaMessages = new JTextArea();
        }
        return jTextAreaMessages;
    }

    /**
     * This method initializes jSliderSleepTime
     *
     * @return javax.swing.JSlider
     */
    private JSlider getJSliderSleepTime() {
        if (jSliderSleepTime == null) {
            jSliderSleepTime = new JSlider();
            jSliderSleepTime.setBounds(new Rectangle(235, 55, 441, 61));
            jSliderSleepTime.setMajorTickSpacing(2500);
            jSliderSleepTime.setMinorTickSpacing(500);
            jSliderSleepTime.setPaintTicks(true);
            jSliderSleepTime.setPaintLabels(true);
            jSliderSleepTime.setToolTipText("Sleep Time in miliseconds");
            jSliderSleepTime.setValue(1000);
            jSliderSleepTime.setMaximum(5000);
        }
        return jSliderSleepTime;
    }

    /**
     * This method initializes jFrame
     *
     * @return javax.swing.JFrame
     */
    private JFrame getJFrame() {
        if (jFrame == null) {
            jFrame = new JFrame();
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setResizable(false);
            jFrame.setJMenuBar(getJJMenuBar());
            jFrame.setSize(695, 670);
            jFrame.setContentPane(getJContentPane());
            jFrame.setTitle("Money Transfer - JINI Transactions");
        }
        return jFrame;
    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(null);
            jContentPane.add(getJPanelAvailableBanks(), null);
            jContentPane.add(getJPanelSourceBank(), null);
            jContentPane.add(getJPanelDestinationAccount2(), null);
            jContentPane.add(getJPanelTransferDetails(), null);
            jContentPane.add(getJPanelInitTasks(), null);
            jContentPane.add(getJPanelMessages(), null);
        }
        return jContentPane;
    }

    /**
     * This method initializes jJMenuBar
     *
     * @return javax.swing.JMenuBar
     */
    private JMenuBar getJJMenuBar() {
        if (jJMenuBar == null) {
            jJMenuBar = new JMenuBar();
            jJMenuBar.add(getFileMenu());
        }
        return jJMenuBar;
    }

    /**
     * This method initializes jMenu
     *
     * @return javax.swing.JMenu
     */
    private JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu();
            fileMenu.setText("File");
            fileMenu.add(getSaveMenuItem());
            fileMenu.add(getExitMenuItem());
        }
        return fileMenu;
    }

    /**
     * This method initializes jMenuItem
     *
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getExitMenuItem() {
        if (exitMenuItem == null) {
            exitMenuItem = new JMenuItem();
            exitMenuItem.setText("Exit");
            exitMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        }
        return exitMenuItem;
    }

    /**
     * This method initializes jMenuItem
     *
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getSaveMenuItem() {
        if (saveMenuItem == null) {
            saveMenuItem = new JMenuItem();
            saveMenuItem.setText("Save");
            saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK, true));
        }
        return saveMenuItem;
    }
}
