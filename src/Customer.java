import java.util.ArrayList;

// Abstract class: a Customer is either an Individual or a Company.
public abstract class Customer {

    private String customerID;
    private String address;
    private String username;
    private String password;
    private ArrayList<Account> accounts;

    public Customer(String customerID, String address, String username, String password) {
        this.customerID = customerID;
        this.address = address;
        this.username = username;
        this.password = password;
        this.accounts = new ArrayList<Account>();
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Each subclass returns the right name (full name or company name).
    public abstract String getDisplayName();

    // Login check: username and password must match.
    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    // Adds an account to this customer.
    public void openAccount(Account account) {
        accounts.add(account);
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    // Finds one of the customer's accounts by its number (null if not found).
    public Account findAccount(String accountNumber) {
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getAccountNumber().equals(accountNumber)) {
                return accounts.get(i);
            }
        }
        return null;
    }

    // Business rule: a customer must have a Savings and an Investment account.
    public boolean hasRequiredAccounts() {
        boolean hasSavings = false;
        boolean hasInvestment = false;
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i) instanceof SavingsAccount) {
                hasSavings = true;
            }
            if (accounts.get(i) instanceof InvestmentAccount) {
                hasInvestment = true;
            }
        }
        return hasSavings && hasInvestment;
    }

}
