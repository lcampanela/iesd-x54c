package iesd.jini.bank.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.mdeos.jini.helper.ConfigureJiniFramework;
import org.mdeos.jini.helper.FindService;

import iesd.jini.bank.api.Account;
import iesd.jini.bank.api.RemoteBank;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.JoinManager;

public class BankManagerMain {

    private final static Logger LOGGER = Logger.getLogger(BankServicesMain.class.getName());

    class BankItem {

        public String serviceFriendlyName;
        public RemoteBank remote;
        public RemoteBankImpl impl;
        public LookupDiscoveryManager lookupDiscoveryManager;
        public JoinManager joinManager;

        public String toString() {
            return this.serviceFriendlyName;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof BankItem)) {
                return false;
            }
            return this.serviceFriendlyName.equals(((BankItem) obj).serviceFriendlyName);
        }
    }

    class MoneyChecker extends TimerTask {

        public static final int NumTicksToShow = 5;
        private int tick = -1;

        private long getSuposedMoney() {
            return banksModel.size() * 5000 * 4;
        }

        private long getRealMoney() {
            long accAccounts = 0;
            long accWithdraws = 0;
            long accCredits = 0;

            try {
                Enumeration<?> currentBanks = banksModel.elements();
                while (currentBanks.hasMoreElements()) {
                    BankItem currentBank = (BankItem) currentBanks.nextElement();
                    for (Account currentAccount : currentBank.remote.getAccounts()) {
                        System.out.println("GET ACCOUNTS: " + currentAccount.getAccountNumber() + " " + currentAccount.getBalance());
                        accAccounts += currentAccount.getBalance();
                    }
                    accWithdraws += currentBank.remote.getTotalAmountsOfWithdraws();
                    accCredits += currentBank.remote.getTotalAmountsOfCredits();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        getJFrame(),
                        "Error when getting real money from all the banks",
                        ex.getMessage(),
                        JOptionPane.ERROR_MESSAGE);
            }
            return accAccounts + accCredits - accWithdraws;
        }

        public void run() {
            this.tick = (++this.tick) % NumTicksToShow;

            Runnable update;

            final int time = MoneyChecker.NumTicksToShow - tick;

            if (this.tick == 0) {
                update = new Runnable() {
                    @Override
                    public void run() {
                        jLabelNextUpdate.setText("Next update in " + time + " seconds");

                        long sm = getSuposedMoney();
                        long rm = getRealMoney();

                        jTextFieldMoneyIssued.setText("" + sm);
                        jTextFieldMoneyAvailable.setText("" + rm);

                        long diff = sm - rm;

                        jTextFieldMoneyDifference.setText("" + diff);
                        if (diff != 0) {
                            jTextFieldMoneyDifference.setBackground(Color.RED);
                        } else {
                            jTextFieldMoneyDifference.setBackground(Color.GREEN);
                        }
                    }
                };
            } else {
                update = new Runnable() {
                    @Override
                    public void run() {
                        jLabelNextUpdate.setText("Next update in " + time + " seconds");
                    }
                };
            }

            SwingUtilities.invokeLater(update);
        }
    }

    class MyFrameListener extends WindowAdapter {

        public void windowClosing(WindowEvent e) {
            end();
        }
    }

    // Class attributes
    private DefaultListModel<BankItem> banksModel = new DefaultListModel<BankItem>();
    private DefaultListModel<Account> accountsModel = new DefaultListModel<Account>();
    private MoneyChecker checkerTask = null;
    private Timer timerChecker = null;

    // Private methods
    public void init() {
        try {
            jListAvailableBanks.setModel(this.banksModel);
            jListSelBankAccounts.setModel(this.accountsModel);

            createBank("CGD");
            createBank("BPI");

            timerChecker = new Timer();

            checkerTask = new MoneyChecker();

            timerChecker.scheduleAtFixedRate(this.checkerTask, 0, (long) (MoneyChecker.NumTicksToShow * 1000));

            jButtonInit.setEnabled(false);
            jButtonCreateBank.setEnabled(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this.getJFrame(),
                    "Error on init internal data",
                    ex.getMessage(),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void end() {
        if (this.banksModel != null) {
            Enumeration<?> banks = this.banksModel.elements();

            while (banks.hasMoreElements()) {
                BankItem currentBank = (BankItem) banks.nextElement();

                currentBank.joinManager.terminate();
            }
        }
    }

    private void createBank(String proposedName) {
        String name = proposedName.trim();

        try {
            if (name.length() == 0) {
                JOptionPane.showMessageDialog(
                        this.getJFrame(),
                        "The bank name can't be empty",
                        "Error when creating bank",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            BankItem bank = new BankItem();
            bank.serviceFriendlyName = name;

            if (this.banksModel.contains(bank)) {
                JOptionPane.showMessageDialog(
                        this.getJFrame(),
                        "There is already a bank with the name: \"" + name + "\"",
                        "Error when creating bank \"" + name + "\"",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            BankServicesRegistration bankServicesRegistration = new BankServicesRegistration(bank.serviceFriendlyName);
            bank.remote = bankServicesRegistration.remoteBankImpl.remoteBankProxy;
            bank.impl = bankServicesRegistration.remoteBankImpl;
            System.out.println("bank.serviceFriendlyName = " + bank.remote.toString());

            banksModel.addElement(bank);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this.getJFrame(),
                    ex.toString(),
                    "Error when creating bank \"" + name + "\"",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showCurrentBank() {
        BankItem bank = (BankItem) this.jListAvailableBanks.getSelectedValue();
        if (bank != null) {
            this.accountsModel.clear();
            try {
                List<Account> accounts = bank.remote.getAccounts();

                for (Account account : accounts) {
                    this.accountsModel.addElement(account);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this.getJFrame(),
                        ex.getMessage(),
                        "Error when listing accounts for bank bank \"" + bank.serviceFriendlyName + "\"",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JFrame jFrame = null;  //  @jve:decl-index=0:visual-constraint="10,10"
    private JPanel jContentPane = null;
    private JMenuBar jJMenuBar = null;
    private JMenu fileMenu = null;
    private JMenuItem exitMenuItem = null;
    private JLabel jLabelAvailableBanks = null;
    private JLabel jLabelNameNewBank = null;
    private JTextField jTextFieldNewNameBank = null;
    private JButton jButtonCreateBank = null;
    private JPanel jPanelCurrentBank = null;
    private JScrollPane jScrollPaneSelBankAccounts = null;
    private JList<Account> jListSelBankAccounts = null;
    private JList<BankItem> jListAvailableBanks = null;

    private JScrollPane jScrollPaneAvailableBanks = null;

    private JPanel jPanelMoneyControl = null;
    private JLabel jLabelMoneyIssued = null;
    private JLabel jLabelMoneyAvailable = null;
    private JTextField jTextFieldMoneyIssued = null;
    private JTextField jTextFieldMoneyAvailable = null;
    private JPanel jPanelInitTasks = null;
    private JButton jButtonInit = null;
    private JLabel jLabelNextUpdate = null;
    private JLabel jLabelMoneyDifference = null;
    private JTextField jTextFieldMoneyDifference = null;

    /**
     * This method initializes jTextFieldNewNameBank
     *
     * @return javax.swing.JTextField
     */
    private JTextField getJTextFieldNewNameBank() {
        if (jTextFieldNewNameBank == null) {
            jTextFieldNewNameBank = new JTextField();
            jTextFieldNewNameBank.setBounds(new Rectangle(215, 35, 271, 21));
        }
        return jTextFieldNewNameBank;
    }

    /**
     * This method initializes jButtonCreateBank
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonCreateBank() {
        if (jButtonCreateBank == null) {
            jButtonCreateBank = new JButton();
            jButtonCreateBank.setBounds(new Rectangle(215, 65, 271, 21));
            jButtonCreateBank.setEnabled(false);
            jButtonCreateBank.setText("Create Bank");
            jButtonCreateBank.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    createBank(jTextFieldNewNameBank.getText());
                }
            });
        }
        return jButtonCreateBank;
    }

    /**
     * This method initializes jPanelCurrentBank
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanelCurrentBank() {
        if (jPanelCurrentBank == null) {
            jPanelCurrentBank = new JPanel();
            jPanelCurrentBank.setLayout(null);
            jPanelCurrentBank.setBounds(new Rectangle(215, 95, 271, 171));
            jPanelCurrentBank.setBorder(BorderFactory.createTitledBorder(null, "Accounts for selected bank", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            jPanelCurrentBank.add(getJScrollPaneSelBankAccounts(), null);
        }
        return jPanelCurrentBank;
    }

    /**
     * This method initializes jScrollPaneSelBankAccounts
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPaneSelBankAccounts() {
        if (jScrollPaneSelBankAccounts == null) {
            jScrollPaneSelBankAccounts = new JScrollPane();
            jScrollPaneSelBankAccounts.setBounds(new Rectangle(5, 30, 251, 136));
            jScrollPaneSelBankAccounts.setViewportView(getJListselBankAccounts());
        }
        return jScrollPaneSelBankAccounts;
    }

    /**
     * This method initializes jListselBankAccounts
     *
     * @return javax.swing.JList
     */
    private JList<Account> getJListselBankAccounts() {
        if (jListSelBankAccounts == null) {
            jListSelBankAccounts = new JList<Account>();
        }
        return jListSelBankAccounts;
    }

    /**
     * This method initializes jListAvailableBanks
     *
     * @return javax.swing.JList
     */
    private JList<BankItem> getJListAvailableBanks() {
        if (jListAvailableBanks == null) {
            jListAvailableBanks = new JList<BankItem>();
            jListAvailableBanks
                    .addListSelectionListener(new javax.swing.event.ListSelectionListener() {
                        public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                            showCurrentBank();
                        }
                    });
        }
        return jListAvailableBanks;
    }

    /**
     * This method initializes jScrollPaneAvailableBanks
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPaneAvailableBanks() {
        if (jScrollPaneAvailableBanks == null) {
            jScrollPaneAvailableBanks = new JScrollPane();
            jScrollPaneAvailableBanks.setBounds(new Rectangle(5, 35, 201, 231));
            jScrollPaneAvailableBanks.setViewportView(getJListAvailableBanks());
        }
        return jScrollPaneAvailableBanks;
    }

    /**
     * This method initializes jPanelMoneyControl
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanelMoneyControl() {
        if (jPanelMoneyControl == null) {
            jLabelMoneyDifference = new JLabel();
            jLabelMoneyDifference.setBounds(new Rectangle(15, 115, 351, 21));
            jLabelMoneyDifference.setText("Difference");
            jLabelNextUpdate = new JLabel();
            jLabelNextUpdate.setBounds(new Rectangle(15, 25, 451, 21));
            jLabelNextUpdate.setText("");
            jLabelMoneyAvailable = new JLabel();
            jLabelMoneyAvailable.setBounds(new Rectangle(15, 85, 351, 21));
            jLabelMoneyAvailable.setText("Money currently available");
            jLabelMoneyIssued = new JLabel();
            jLabelMoneyIssued.setBounds(new Rectangle(15, 55, 351, 21));
            jLabelMoneyIssued.setText("Money issued (Total number of banks * sum of all accounts)");
            jPanelMoneyControl = new JPanel();
            jPanelMoneyControl.setLayout(null);
            jPanelMoneyControl.setBounds(new Rectangle(5, 275, 481, 141));
            jPanelMoneyControl.setBorder(BorderFactory.createTitledBorder(null, "Money Invariants", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            jPanelMoneyControl.add(jLabelMoneyIssued, null);
            jPanelMoneyControl.add(jLabelMoneyAvailable, null);
            jPanelMoneyControl.add(getJTextFieldMoneyIssued(), null);
            jPanelMoneyControl.add(getJTextFieldMoneyAvailable(), null);
            jPanelMoneyControl.add(jLabelNextUpdate, null);
            jPanelMoneyControl.add(jLabelMoneyDifference, null);
            jPanelMoneyControl.add(getJTextFieldMoneyDifference(), null);
        }
        return jPanelMoneyControl;
    }

    /**
     * This method initializes jTextFieldMoneyIssued
     *
     * @return javax.swing.JTextField
     */
    private JTextField getJTextFieldMoneyIssued() {
        if (jTextFieldMoneyIssued == null) {
            jTextFieldMoneyIssued = new JTextField();
            jTextFieldMoneyIssued.setBounds(new Rectangle(375, 55, 91, 21));
            jTextFieldMoneyIssued.setEditable(false);
        }
        return jTextFieldMoneyIssued;
    }

    /**
     * This method initializes jTextFieldMoneyAvailable
     *
     * @return javax.swing.JTextField
     */
    private JTextField getJTextFieldMoneyAvailable() {
        if (jTextFieldMoneyAvailable == null) {
            jTextFieldMoneyAvailable = new JTextField();
            jTextFieldMoneyAvailable.setBounds(new Rectangle(375, 85, 91, 21));
            jTextFieldMoneyAvailable.setEditable(false);
        }
        return jTextFieldMoneyAvailable;
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
            jPanelInitTasks.setBounds(new Rectangle(495, 5, 181, 411));
            jPanelInitTasks.setBorder(BorderFactory.createTitledBorder(null, "Init Tasks", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
            jPanelInitTasks.add(getJButtonInit(), null);
        }
        return jPanelInitTasks;
    }

    /**
     * This method initializes jButtonInit
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonInit() {
        if (jButtonInit == null) {
            jButtonInit = new JButton();
            jButtonInit.setBounds(new Rectangle(15, 25, 151, 21));
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
     * This method initializes jTextFieldMoneyDifference
     *
     * @return javax.swing.JTextField
     */
    private JTextField getJTextFieldMoneyDifference() {
        if (jTextFieldMoneyDifference == null) {
            jTextFieldMoneyDifference = new JTextField();
            jTextFieldMoneyDifference.setBounds(new Rectangle(375, 115, 91, 21));
            jTextFieldMoneyDifference.setEditable(false);
        }
        return jTextFieldMoneyDifference;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        ConfigureJiniFramework.setupLoggingConfig();
        LOGGER.log(Level.INFO, "Starting BankManagerMain...");
        try {
            //COPY EMBEDDED DIRECTORY 'config' to ''
            ConfigureJiniFramework.copyDefaulEmbeddedDirToDefaultFileSystemDir();
            ConfigureJiniFramework.setSecurity(
                    ConfigureJiniFramework.JINI_RUNTIME_CONFDIR + "/BankService.policy");

            // For the case a service is registered, there 
            ConfigureJiniFramework.setServerCodebase();

            // set codebaseOnly flag for resolution of class loading from codebase only
            ConfigureJiniFramework.enableRemoteCodebase();

            FindService.InitFindService();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (URISyntaxException ex) {
            Logger.getLogger(BankServicesMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BankServicesMain.class.getName()).log(Level.SEVERE, null, ex);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                BankManagerMain application = new BankManagerMain();
                application.getJFrame().setVisible(true);
            }
        });
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
            jFrame.addWindowListener(new MyFrameListener());
            jFrame.setResizable(false);
            jFrame.setJMenuBar(getJJMenuBar());
            jFrame.setSize(692, 474);
            jFrame.setContentPane(getJContentPane());
            jFrame.setTitle("Bank Manager - JINI Transactions");
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
            jLabelNameNewBank = new JLabel();
            jLabelNameNewBank.setBounds(new Rectangle(215, 5, 271, 21));
            jLabelNameNewBank.setText("Bank Name");
            jLabelAvailableBanks = new JLabel();
            jLabelAvailableBanks.setBounds(new Rectangle(5, 5, 201, 21));
            jLabelAvailableBanks.setText("Available Banks");
            jContentPane = new JPanel();
            jContentPane.setLayout(null);
            jContentPane.add(jLabelAvailableBanks, null);
            jContentPane.add(jLabelNameNewBank, null);
            jContentPane.add(getJTextFieldNewNameBank(), null);
            jContentPane.add(getJButtonCreateBank(), null);
            jContentPane.add(getJPanelCurrentBank(), null);
            jContentPane.add(getJScrollPaneAvailableBanks(), null);
            jContentPane.add(getJPanelMoneyControl(), null);
            jContentPane.add(getJPanelInitTasks(), null);
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
}
