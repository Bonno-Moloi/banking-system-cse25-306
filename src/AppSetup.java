import java.util.ArrayList;

// Runs once when the program starts: opens the database, creates the tables,
// loads the saved customers into the Bank, and adds the demo customer
// the very first time the program runs.
public class AppSetup {

    private DatabaseManager db;
    private TransactionDAO transactionDAO;
    private AccountDAO accountDAO;
    private CustomerDAO customerDAO;

    public AppSetup() {
        this(DatabaseManager.DEFAULT_URL);
    }

    // The integration test uses its own temporary database URL.
    public AppSetup(String url) {
        db = new DatabaseManager(url);
        db.createTables();
        transactionDAO = new TransactionDAO(db);
        accountDAO = new AccountDAO(db, transactionDAO);
        customerDAO = new CustomerDAO(db, accountDAO);
    }

    public Bank loadBank() {
        Bank bank = new Bank();

        ArrayList<Customer> saved = customerDAO.findAll();
        for (int i = 0; i < saved.size(); i++) {
            bank.addCustomer(saved.get(i));
        }
        bank.updateCounters();

        // First run: the database is empty, so save the demo customer.
        if (saved.size() == 0) {
            Customer demo = bank.createDemoCustomer();
            customerDAO.insert(demo);
            for (int i = 0; i < demo.getAccounts().size(); i++) {
                accountDAO.insert(demo.getAccounts().get(i), demo.getCustomerID());
            }
            bank.addCustomer(demo);
        }
        return bank;
    }

    public CustomerDAO getCustomerDAO() {
        return customerDAO;
    }

    public AccountDAO getAccountDAO() {
        return accountDAO;
    }

    public TransactionDAO getTransactionDAO() {
        return transactionDAO;
    }
}
