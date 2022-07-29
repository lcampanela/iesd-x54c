package iesd.jini.bank.api;

import java.io.Serializable;

public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private long balance;
    private long accountNumber;
    private String ownersName;

    public Account(long accountNumber, String ownersName, long initialAmount) {
        this.accountNumber = accountNumber;
        this.ownersName = ownersName;
        balance = initialAmount;
    }

    public long getBalance() {
        return this.balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public String getOwnersName() {
        return this.ownersName;
    }

    public long getAccountNumber() {
        return this.accountNumber;
    }

    @Override
    public String toString() {
        return "[" + this.getAccountNumber() + "], " + this.getOwnersName() + ", " + this.getBalance();
    }
}
