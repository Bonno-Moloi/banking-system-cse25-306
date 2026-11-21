import java.util.ArrayList;

// Abstract class: an Account cannot be created directly.
// Only SavingsAccount, InvestmentAccount and ChequeAccount can be created.
public abstract class Account {

    private String accountNumber;
    private double balance;
    private String branch;
    private String status;   // Pending, Active, Frozen or Closed (from the state diagram)
    private ArrayList<Transaction> transactions;
    private String lastMessage;   // explains why the last deposit/withdrawal failed

    public Account(String accountNumber, String branch) {
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.balance = 0;
        this.status = "Active";
        this.transactions = new ArrayList<Transaction>();
        this.lastMessage = "";
    }

    // Each subclass says what type of account it is (used for display).
    public abstract String getAccountType();

    public String getLastMessage() {
        return lastMessage;
    }

    // Saves the reason why an action failed.
    protected void fail(String message) {
        lastMessage = message;
    }

    // ----- Getters -----
    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public String getBranch() {
        return branch;
    }

    public String getStatus() {
        return status;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    // ----- Business logic -----

    // Deposit money. Returns true if it worked, false if not.
    public boolean deposit(double amount) {
        if (amount <= 0) {
            fail("Deposit failed: amount must be greater than 0.");
            return false;
        }
        if (!status.equals("Active")) {
            fail("Deposit failed: account is " + status + ".");
            return false;
        }
        balance = balance + amount;
        transactions.add(new Transaction("Deposit", amount, balance));
        return true;
    }

    // Withdraw money. Returns true if it worked, false if not.
    // SavingsAccount overrides this method to block withdrawals.
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            fail("Withdrawal failed: amount must be greater than 0.");
            return false;
        }
        if (!status.equals("Active")) {
            fail("Withdrawal failed: account is " + status + ".");
            return false;
        }
        if (amount > balance) {
            fail("Withdrawal failed: insufficient funds.");
            return false;
        }
        balance = balance - amount;
        transactions.add(new Transaction("Withdrawal", amount, balance));
        return true;
    }

    // Used by the subclasses to add interest to the balance.
    protected void applyInterest(double interestAmount) {
        balance = balance + interestAmount;
        transactions.add(new Transaction("Interest", interestAmount, balance));
    }

    // ----- Account states (Active -> Frozen -> Active -> Closed) -----
    public void freeze() {
        if (status.equals("Active")) {
            status = "Frozen";
        }
    }

    public void unfreeze() {
        if (status.equals("Frozen")) {
            status = "Active";
        }
    }

    // An account can only be closed when the balance is zero.
    public boolean close() {
        if (balance == 0 && status.equals("Active")) {
            status = "Closed";
            return true;
        }
        fail("Account cannot be closed. Balance must be zero and account must be Active.");
        return false;
    }


}
