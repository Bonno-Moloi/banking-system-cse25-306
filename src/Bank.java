import java.util.ArrayList;

// Core domain class: the bank itself. It keeps all the customers and
// creates unique customer IDs and account numbers.
public class Bank {

    private String branch;
    private ArrayList<Customer> customers;
    private int nextCustomerNumber;
    private int nextAccountNumber;

    public Bank() {
        branch = "Main Mall Branch";
        customers = new ArrayList<Customer>();
        nextCustomerNumber = 1;
        nextAccountNumber = 1001;
    }

    public String getBranch() {
        return branch;
    }

    public String newCustomerId() {
        String id = "C" + nextCustomerNumber;
        nextCustomerNumber++;
        return id;
    }

    public String newAccountNumber(String prefix) {
        String number = prefix + nextAccountNumber;
        nextAccountNumber++;
        return number;
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public boolean usernameTaken(String username) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    // Returns the customer whose username and password match, or null if none do.
    public Customer authenticate(String username, String password) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).login(username, password)) {
                return customers.get(i);
            }
        }
        return null;
    }

    // After customers are loaded from the database, make sure new IDs and
    // account numbers carry on after the highest ones already used.
    public void updateCounters() {
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            int customerNumber = numberPart(customer.getCustomerID());
            if (customerNumber >= nextCustomerNumber) {
                nextCustomerNumber = customerNumber + 1;
            }
            ArrayList<Account> accounts = customer.getAccounts();
            for (int j = 0; j < accounts.size(); j++) {
                int accountNumber = numberPart(accounts.get(j).getAccountNumber());
                if (accountNumber >= nextAccountNumber) {
                    nextAccountNumber = accountNumber + 1;
                }
            }
        }
    }

    // "S1001" -> 1001, "C12" -> 12
    private int numberPart(String text) {
        String digits = text.replaceAll("[^0-9]", "");
        if (digits.equals("")) {
            return 0;
        }
        return Integer.parseInt(digits);
    }

    // Creates the demo customer (john / pass123) with three accounts.
    // It is not added to the bank here; the caller saves it first.
    public Customer createDemoCustomer() {
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

        return john;
    }
}
