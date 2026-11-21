import java.text.SimpleDateFormat;
import java.util.ArrayList;

// Controller: handles everything to do with the logged-in customer's accounts.
// The screens only give it account numbers and text typed by the user. It finds
// the account in the core model, asks it to do the work, and returns text to display.
public class AccountController {

    private Bank bank;
    private LoginController loginController;

    public AccountController(Bank bank, LoginController loginController) {
        this.bank = bank;
        this.loginController = loginController;
    }

    // ---------------- Dashboard ----------------

    public String getWelcomeText() {
        return "Welcome, " + loginController.getLoggedInCustomer().getDisplayName();
    }

    public String getCustomerDetails() {
        Customer customer = loginController.getLoggedInCustomer();
        return "Customer ID: " + customer.getCustomerID() + "   |   Address: " + customer.getAddress();
    }

    // One line of text per account, for the account list.
    public ArrayList<String> getAccountSummaries() {
        ArrayList<String> summaries = new ArrayList<String>();
        ArrayList<Account> accounts = loginController.getLoggedInCustomer().getAccounts();
        for (int i = 0; i < accounts.size(); i++) {
            Account a = accounts.get(i);
            summaries.add(a.getAccountType() + " | " + a.getAccountNumber()
                    + " | Status: " + a.getStatus() + " | Balance: " + money(a.getBalance()));
        }
        return summaries;
    }

    // The account number of the account at this position in the list.
    public String getAccountNumberAt(int index) {
        return loginController.getLoggedInCustomer().getAccounts().get(index).getAccountNumber();
    }

    public ActionResult openChequeAccount(String employerName, String employerAddress) {
        ChequeAccount cheque = new ChequeAccount(bank.newAccountNumber("CH"), bank.getBranch());
        if (!isEmpty(employerName)) {
            String address = isEmpty(employerAddress) ? "" : employerAddress.trim();
            cheque.linkEmployer(employerName.trim(), address);
        }
        loginController.getLoggedInCustomer().openAccount(cheque);
        return new ActionResult(true, "Cheque account " + cheque.getAccountNumber() + " opened.");
    }

    // ---------------- Account screen ----------------

    public String getAccountTitle(String accountNumber) {
        return findAccount(accountNumber).getAccountType() + " Account";
    }

    public String describeAccount(String accountNumber) {
        Account account = findAccount(accountNumber);
        String text = "Account type:   " + account.getAccountType()
                + "\nAccount number: " + account.getAccountNumber()
                + "\nBranch:         " + account.getBranch()
                + "\nStatus:         " + account.getStatus()
                + "\nBalance:        " + money(account.getBalance());
        if (account instanceof SavingsAccount) {
            text = text + "\nInterest:       0.05% per month"
                    + "\nWithdrawals:    Not allowed";
        }
        if (account instanceof InvestmentAccount) {
            text = text + "\nInterest:       5% per month"
                    + "\nMin. opening:   BWP500.00";
        }
        if (account instanceof ChequeAccount) {
            ChequeAccount cheque = (ChequeAccount) account;
            if (cheque.getEmployerName().equals("")) {
                text = text + "\nEmployer:       Not linked";
            } else {
                text = text + "\nEmployer:       " + cheque.getEmployerName()
                        + " (" + cheque.getEmployerAddress() + ")";
            }
        }
        return text;
    }

    public boolean allowsWithdrawals(String accountNumber) {
        return !(findAccount(accountNumber) instanceof SavingsAccount);
    }

    public boolean paysInterest(String accountNumber) {
        Account account = findAccount(accountNumber);
        return (account instanceof SavingsAccount) || (account instanceof InvestmentAccount);
    }

    public ActionResult deposit(String accountNumber, String amountText) {
        double amount = parseAmount(amountText);
        if (Double.isNaN(amount)) {
            return new ActionResult(false, "Please enter a valid amount.");
        }
        Account account = findAccount(accountNumber);
        if (account.deposit(amount)) {
            return new ActionResult(true, "Deposit successful. New balance: " + money(account.getBalance()));
        }
        return new ActionResult(false, account.getLastMessage());
    }

    public ActionResult withdraw(String accountNumber, String amountText) {
        double amount = parseAmount(amountText);
        if (Double.isNaN(amount)) {
            return new ActionResult(false, "Please enter a valid amount.");
        }
        Account account = findAccount(accountNumber);
        if (account.withdraw(amount)) {
            return new ActionResult(true, "Withdrawal successful. New balance: " + money(account.getBalance()));
        }
        return new ActionResult(false, account.getLastMessage());
    }

    public ActionResult addInterest(String accountNumber) {
        Account account = findAccount(accountNumber);
        if (account instanceof SavingsAccount) {
            ((SavingsAccount) account).addInterest();
        } else if (account instanceof InvestmentAccount) {
            ((InvestmentAccount) account).addInterest();
        } else {
            return new ActionResult(false, "Cheque accounts do not earn interest.");
        }
        return new ActionResult(true, "Monthly interest added. New balance: " + money(account.getBalance()));
    }

    // One line of text per transaction, newest first.
    public ArrayList<String> getTransactionLines(String accountNumber) {
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<Transaction> list = findAccount(accountNumber).getTransactions();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = list.size() - 1; i >= 0; i--) {
            Transaction t = list.get(i);
            lines.add(format.format(t.getDate()) + " | " + t.getType() + " | " + money(t.getAmount())
                    + " | Balance: " + money(t.getBalanceAfter()));
        }
        return lines;
    }

    // ---------------- Helpers ----------------

    // Finds one of the logged-in customer's accounts by its number.
    private Account findAccount(String accountNumber) {
        return loginController.getLoggedInCustomer().findAccount(accountNumber);
    }

    // Turns the text typed by the user into a number. Returns NaN if it is not a number.
    private double parseAmount(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    private String money(double amount) {
        return "BWP" + String.format("%.2f", amount);
    }

    private boolean isEmpty(String text) {
        return text == null || text.trim().equals("");
    }
}
