import java.util.ArrayList;

// Controller: sits between the GUI (boundary classes) and the core model.
// The GUI screens only call these methods and display the text they return.
public class BankController {

    private ArrayList<Customer> customers;
    private Customer loggedInCustomer;
    private boolean lastActionOk;
    private int nextCustomerNumber;
    private int nextAccountNumber;
    private String branch = "Main Mall Branch";

    public BankController() {
        customers = new ArrayList<Customer>();
        loggedInCustomer = null;
        lastActionOk = false;
        nextCustomerNumber = 1;
        nextAccountNumber = 1001;
        createDemoCustomer();
    }

    // A demo customer so the system can be tested straight away (john / pass123).
    private void createDemoCustomer() {
        Individual john = new Individual(newCustomerId(), "John", "Dlamini",
                "Plot 123, Gaborone", "john", "pass123");

        SavingsAccount savings = new SavingsAccount(newAccountNumber("S"), branch);
        savings.deposit(2000);
        john.openAccount(savings);

        InvestmentAccount investment = new InvestmentAccount(newAccountNumber("I"), branch, 1000);
        john.openAccount(investment);

        ChequeAccount cheque = new ChequeAccount(newAccountNumber("CH"), branch);
        cheque.linkEmployer("Global Retail Solutions", "Gaborone");
        cheque.deposit(5000);
        john.openAccount(cheque);

        customers.add(john);
    }

    private String newCustomerId() {
        String id = "C" + nextCustomerNumber;
        nextCustomerNumber++;
        return id;
    }

    private String newAccountNumber(String prefix) {
        String number = prefix + nextAccountNumber;
        nextAccountNumber++;
        return number;
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

    private boolean usernameTaken(String username) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEmpty(String text) {
        return text == null || text.trim().equals("");
    }

    // Tells the GUI if the last action worked (used to show green or red messages).
    public boolean isLastActionOk() {
        return lastActionOk;
    }

    // ---------------- Login / Logout ----------------

    public String login(String username, String password) {
        if (isEmpty(username) || isEmpty(password)) {
            lastActionOk = false;
            return "Please enter your username and password.";
        }
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).login(username.trim(), password)) {
                loggedInCustomer = customers.get(i);
                lastActionOk = true;
                return "Login successful.";
            }
        }
        lastActionOk = false;
        return "Invalid username or password.";
    }

    public void logout() {
        loggedInCustomer = null;
    }

    // ---------------- Registration ----------------

    public String registerIndividual(String firstName, String surname, String address,
                                     String username, String password, String depositText) {
        if (isEmpty(firstName) || isEmpty(surname)) {
            lastActionOk = false;
            return "Please enter your first name and surname.";
        }
        String problem = checkCommonFields(address, username, password);
        if (problem != null) {
            lastActionOk = false;
            return problem;
        }
        Individual customer = new Individual(newCustomerId(), firstName.trim(), surname.trim(),
                address.trim(), username.trim(), password);
        return openStartingAccounts(customer, depositText);
    }

    public String registerCompany(String companyName, String address,
                                  String username, String password, String depositText) {
        if (isEmpty(companyName)) {
            lastActionOk = false;
            return "Please enter the company name.";
        }
        String problem = checkCommonFields(address, username, password);
        if (problem != null) {
            lastActionOk = false;
            return problem;
        }
        Company customer = new Company(newCustomerId(), companyName.trim(),
                address.trim(), username.trim(), password);
        return openStartingAccounts(customer, depositText);
    }

    private String checkCommonFields(String address, String username, String password) {
        if (isEmpty(address) || isEmpty(username) || isEmpty(password)) {
            return "Please fill in all the fields.";
        }
        if (usernameTaken(username.trim())) {
            return "That username is already taken.";
        }
        return null;
    }

    // Every customer gets a Savings and an Investment account.
    private String openStartingAccounts(Customer customer, String depositText) {
        double deposit = parseAmount(depositText);
        if (Double.isNaN(deposit)) {
            lastActionOk = false;
            return "Please enter a valid opening deposit amount.";
        }
        InvestmentAccount investment;
        try {
            investment = new InvestmentAccount(newAccountNumber("I"), branch, deposit);
        } catch (IllegalArgumentException e) {
            lastActionOk = false;
            return e.getMessage();
        }
        customer.openAccount(new SavingsAccount(newAccountNumber("S"), branch));
        customer.openAccount(investment);
        customers.add(customer);
        lastActionOk = true;
        return "Registration successful. Savings and Investment accounts opened. You can now log in.";
    }

    // ---------------- Dashboard ----------------

    public String getWelcomeText() {
        return "Welcome, " + loggedInCustomer.getDisplayName();
    }

    public String getCustomerDetails() {
        return "Customer ID: " + loggedInCustomer.getCustomerID()
                + "   |   Address: " + loggedInCustomer.getAddress();
    }

    public ArrayList<Account> getAccounts() {
        return loggedInCustomer.getAccounts();
    }

    public String openChequeAccount(String employerName, String employerAddress) {
        ChequeAccount cheque = new ChequeAccount(newAccountNumber("CH"), branch);
        if (!isEmpty(employerName)) {
            cheque.linkEmployer(employerName.trim(), isEmpty(employerAddress) ? "" : employerAddress.trim());
        }
        loggedInCustomer.openAccount(cheque);
        lastActionOk = true;
        return "Cheque account " + cheque.getAccountNumber() + " opened.";
    }

    // ---------------- Account screen ----------------

    public String describeAccount(Account account) {
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

    public boolean allowsWithdrawals(Account account) {
        return !(account instanceof SavingsAccount);
    }

    public boolean paysInterest(Account account) {
        return (account instanceof SavingsAccount) || (account instanceof InvestmentAccount);
    }

    public String deposit(Account account, String amountText) {
        double amount = parseAmount(amountText);
        if (Double.isNaN(amount)) {
            lastActionOk = false;
            return "Please enter a valid amount.";
        }
        lastActionOk = account.deposit(amount);
        if (lastActionOk) {
            return "Deposit successful. New balance: " + money(account.getBalance());
        }
        return account.getLastMessage();
    }

    public String withdraw(Account account, String amountText) {
        double amount = parseAmount(amountText);
        if (Double.isNaN(amount)) {
            lastActionOk = false;
            return "Please enter a valid amount.";
        }
        lastActionOk = account.withdraw(amount);
        if (lastActionOk) {
            return "Withdrawal successful. New balance: " + money(account.getBalance());
        }
        return account.getLastMessage();
    }

    public String addInterest(Account account) {
        if (account instanceof SavingsAccount) {
            ((SavingsAccount) account).addInterest();
        } else if (account instanceof InvestmentAccount) {
            ((InvestmentAccount) account).addInterest();
        } else {
            lastActionOk = false;
            return "Cheque accounts do not earn interest.";
        }
        lastActionOk = true;
        return "Monthly interest added. New balance: " + money(account.getBalance());
    }

    public ArrayList<String> getTransactionLines(Account account) {
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<Transaction> list = account.getTransactions();
        for (int i = list.size() - 1; i >= 0; i--) {     // newest first
            lines.add(list.get(i).toString());
        }
        return lines;
    }
}
