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
        addDemoCustomer();
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

    // A demo customer so the system can be tested straight away (john / pass123).
    private void addDemoCustomer() {
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

        addCustomer(john);
    }
}
