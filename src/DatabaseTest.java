import java.util.ArrayList;

// Console test of the database layer. It uses a temporary in-memory database,
// so it never touches the real data. Run it to show Create, Read, Update and Delete.
public class DatabaseTest {

    public static void main(String[] args) {

        DatabaseManager db = new DatabaseManager("jdbc:h2:mem:banktest;DB_CLOSE_DELAY=-1");
        db.createTables();
        TransactionDAO transactionDAO = new TransactionDAO(db);
        AccountDAO accountDAO = new AccountDAO(db, transactionDAO);
        CustomerDAO customerDAO = new CustomerDAO(db, accountDAO);

        System.out.println("===== DATABASE (JDBC) TEST =====\n");
        System.out.println("Tables created: customers, accounts, transactions\n");

        // ---------- CREATE ----------
        System.out.println("--- CREATE ---");
        Individual john = new Individual("C1", "John", "Dlamini", "Plot 123, Gaborone", "john", "pass123");
        customerDAO.insert(john);
        SavingsAccount savings = new SavingsAccount("S1001", "Main Mall Branch");
        savings.deposit(2000);
        InvestmentAccount investment = new InvestmentAccount("I1002", "Main Mall Branch", 1000);
        ChequeAccount cheque = new ChequeAccount("CH1003", "Main Mall Branch");
        cheque.linkEmployer("Global Retail Solutions", "Gaborone");
        accountDAO.insert(savings, "C1");
        accountDAO.insert(investment, "C1");
        accountDAO.insert(cheque, "C1");

        Company shop = new Company("C2", "Kgosi Traders", "Plot 45, Francistown", "kgosi", "secret");
        customerDAO.insert(shop);
        accountDAO.insert(new SavingsAccount("S1004", "Francistown Branch"), "C2");
        System.out.println("Saved 2 customers and 4 accounts.\n");

        // ---------- READ ----------
        System.out.println("--- READ (all customers) ---");
        showAll(customerDAO.findAll());

        System.out.println("\n--- READ (one customer by ID: C1) ---");
        Customer found = customerDAO.findById("C1");
        System.out.println(found.getDisplayName() + " | " + found.getAddress()
                + " | accounts: " + found.getAccounts().size());
        System.out.println("Customer C99 exists? " + (customerDAO.findById("C99") != null));

        System.out.println("\n--- READ (one account by number: CH1003) ---");
        ChequeAccount loaded = (ChequeAccount) accountDAO.findByAccountNumber("CH1003");
        System.out.println(loaded.getAccountType() + " | " + loaded.getAccountNumber()
                + " | employer: " + loaded.getEmployerName());

        // ---------- UPDATE ----------
        System.out.println("\n--- UPDATE ---");
        savings.deposit(500);
        accountDAO.update(savings);
        transactionDAO.insert(savings.getTransactions().get(savings.getTransactions().size() - 1), "S1001");
        john.setAddress("Plot 999, Francistown");
        customerDAO.update(john);

        Account reloaded = accountDAO.findByAccountNumber("S1001");
        System.out.println("Savings balance after deposit: BWP" + String.format("%.2f", reloaded.getBalance()));
        System.out.println("Savings transactions saved: " + reloaded.getTransactions().size());
        System.out.println("John's new address: " + customerDAO.findById("C1").getAddress());

        // ---------- DELETE ----------
        System.out.println("\n--- DELETE ---");
        accountDAO.delete("CH1003");
        System.out.println("Cheque account deleted. John now has "
                + customerDAO.findById("C1").getAccounts().size() + " accounts.");
        customerDAO.delete("C2");
        System.out.println("Customer C2 (and their accounts) deleted. Customers left: "
                + customerDAO.findAll().size());

        System.out.println("\n--- Final contents of the database ---");
        showAll(customerDAO.findAll());
        System.out.println("\n===== END OF TEST =====");
    }

    private static void showAll(ArrayList<Customer> customers) {
        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            System.out.println(c.getCustomerID() + " | " + c.getDisplayName() + " | " + c.getAddress());
            ArrayList<Account> accounts = c.getAccounts();
            for (int j = 0; j < accounts.size(); j++) {
                Account a = accounts.get(j);
                System.out.println("    " + a.getAccountType() + " | " + a.getAccountNumber()
                        + " | " + a.getStatus() + " | BWP" + String.format("%.2f", a.getBalance())
                        + " | transactions: " + a.getTransactions().size());
            }
        }
    }
}
