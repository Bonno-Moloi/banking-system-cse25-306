import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// Integration test: checks that the controllers, the core model and the JDBC
// classes work together as one system. It does everything the GUI buttons do,
// by calling the same controller methods, and then checks the result in the
// model AND directly in the database with its own SQL queries.
// It uses a temporary in-memory database, so the real data is never touched.
public class IntegrationTest {

    private static int passed = 0;
    private static int failed = 0;
    private static DatabaseManager db;

    public static void main(String[] args) throws Exception {

        String url = "jdbc:h2:mem:integration;DB_CLOSE_DELAY=-1";
        db = new DatabaseManager(url);

        System.out.println("===== INTEGRATION TEST: BANKING SYSTEM =====");
        System.out.println("Layers under test: Controllers -> Core model -> JDBC (DAO) -> Database\n");

        // ---------- Start the application (same wiring as BankingApp) ----------
        AppSetup setup = new AppSetup(url);
        Bank bank = setup.loadBank();
        LoginController login = new LoginController(bank);
        RegistrationController register = new RegistrationController(bank, setup.getCustomerDAO(), setup.getAccountDAO());
        AccountController accounts = new AccountController(bank, login, setup.getAccountDAO(), setup.getTransactionDAO());

        check("IT-01", "Start-up creates the tables and saves the demo customer", "1 customer, 3 accounts, 3 transactions",
                count("SELECT COUNT(*) FROM customers", null) + " customer, "
                        + count("SELECT COUNT(*) FROM accounts", null) + " accounts, "
                        + count("SELECT COUNT(*) FROM transactions", null) + " transactions");

        // ---------- Login ----------
        ActionResult wrong = login.login("john", "wrong");
        check("IT-02", "Login with a wrong password is refused", "false: Invalid username or password.",
                wrong.isOk() + ": " + wrong.getMessage());

        ActionResult right = login.login("john", "pass123");
        check("IT-03", "Login with the correct password works", "true: Login successful.",
                right.isOk() + ": " + right.getMessage());

        check("IT-04", "Dashboard lists the 3 accounts loaded from the database", "3",
                "" + accounts.getAccountSummaries().size());

        String savings = accounts.getAccountNumberAt(0);
        String investment = accounts.getAccountNumberAt(1);
        String cheque = accounts.getAccountNumberAt(2);

        // ---------- Deposits and withdrawals ----------
        ActionResult dep = accounts.deposit(savings, "500");
        check("IT-05", "Deposit BWP500 into Savings (message)", "true: Deposit successful. New balance: BWP2500.00",
                dep.isOk() + ": " + dep.getMessage());
        check("IT-06", "Deposit is saved in the database (balance and transaction)", "BWP2500.00, 2 transactions",
                money(dbBalance(savings)) + ", " + count("SELECT COUNT(*) FROM transactions WHERE account_number = ?", savings) + " transactions");

        ActionResult wd = accounts.withdraw(cheque, "300");
        check("IT-07", "Withdraw BWP300 from Cheque is saved in the database", "true, BWP4700.00, 2 transactions",
                wd.isOk() + ", " + money(dbBalance(cheque)) + ", " + count("SELECT COUNT(*) FROM transactions WHERE account_number = ?", cheque) + " transactions");

        ActionResult blocked = accounts.withdraw(savings, "100");
        check("IT-08", "Withdrawal from Savings is blocked and nothing is saved", "false, BWP2500.00, 2 transactions",
                blocked.isOk() + ", " + money(dbBalance(savings)) + ", " + count("SELECT COUNT(*) FROM transactions WHERE account_number = ?", savings) + " transactions");

        ActionResult tooMuch = accounts.withdraw(cheque, "99999");
        check("IT-09", "Withdrawal above the balance is refused and the database is unchanged", "false, BWP4700.00",
                tooMuch.isOk() + ", " + money(dbBalance(cheque)));

        ActionResult notNumber = accounts.deposit(savings, "abc");
        check("IT-10", "Text that is not a number is refused", "false: Please enter a valid amount.",
                notNumber.isOk() + ": " + notNumber.getMessage());

        // ---------- Interest ----------
        accounts.addInterest(savings);
        check("IT-11", "Savings interest of 0.05% is saved in the database", "BWP2501.25",
                money(dbBalance(savings)));
        accounts.addInterest(investment);
        check("IT-12", "Investment interest of 5% is saved in the database", "BWP1050.00",
                money(dbBalance(investment)));
        ActionResult chequeInterest = accounts.addInterest(cheque);
        check("IT-13", "Cheque accounts do not earn interest", "false: Cheque accounts do not earn interest.",
                chequeInterest.isOk() + ": " + chequeInterest.getMessage());

        // ---------- Open another account ----------
        ActionResult newCheque = accounts.openChequeAccount("Acme Ltd", "Gaborone");
        String newChequeNumber = accounts.getAccountNumberAt(3);
        check("IT-14", "New Cheque account is saved with its employer", "4 accounts, employer Acme Ltd",
                accounts.getAccountSummaries().size() + " accounts, employer "
                        + text("SELECT employer_name FROM accounts WHERE account_number = ?", newChequeNumber));

        login.logout();
        check("IT-15", "Logout ends the session", "false", "" + login.isLoggedIn());

        // ---------- Registration ----------
        ActionResult reg = register.registerIndividual("Amy", "Tau", "Plot 7, Gaborone", "amy", "secret", "800");
        check("IT-16", "Register a new customer with BWP800 opening deposit", "true, 2 customers, Amy has 2 accounts",
                reg.isOk() + ", " + count("SELECT COUNT(*) FROM customers", null) + " customers, Amy has "
                        + count("SELECT COUNT(*) FROM accounts WHERE customer_id = (SELECT customer_id FROM customers WHERE username = ?)", "amy") + " accounts");

        check("IT-17", "The Investment opening deposit is saved as a transaction", "1 transaction, BWP800.00",
                count("SELECT COUNT(*) FROM transactions WHERE account_number = (SELECT account_number FROM accounts WHERE account_type = 'INVESTMENT' AND customer_id = (SELECT customer_id FROM customers WHERE username = ?))", "amy")
                        + " transaction, " + money(text2("SELECT balance FROM accounts WHERE account_type = 'INVESTMENT' AND customer_id = (SELECT customer_id FROM customers WHERE username = ?)", "amy")));

        ActionResult lowDeposit = register.registerIndividual("Bob", "Kay", "Plot 8", "bob", "pw", "200");
        check("IT-18", "Opening deposit below BWP500 is refused and nothing is saved", "false: ...at least BWP500.00 to open., 2 customers",
                lowDeposit.isOk() + ": ..." + lowDeposit.getMessage().substring(lowDeposit.getMessage().indexOf("at least")) + ", "
                        + count("SELECT COUNT(*) FROM customers", null) + " customers");

        ActionResult duplicate = register.registerIndividual("Amy", "Two", "Plot 9", "amy", "pw", "900");
        check("IT-19", "A username that is already taken is refused", "false: That username is already taken., 2 customers",
                duplicate.isOk() + ": " + duplicate.getMessage() + ", " + count("SELECT COUNT(*) FROM customers", null) + " customers");

        ActionResult company = register.registerCompany("Kgosi Traders", "Plot 45, Francistown", "kgosi", "pw", "700");
        check("IT-20", "Register a company customer", "true, 1 COMPANY row",
                company.isOk() + ", " + count("SELECT COUNT(*) FROM customers WHERE customer_type = 'COMPANY'", null) + " COMPANY row");

        login.login("amy", "secret");
        check("IT-21", "The new customer can log in and sees her 2 accounts", "2",
                "" + accounts.getAccountSummaries().size());
        login.logout();

        // ---------- Restart the application: everything must come back from the database ----------
        System.out.println("\n--- Restarting the application (new Bank, controllers loaded from the database) ---\n");
        AppSetup setup2 = new AppSetup(url);
        Bank bank2 = setup2.loadBank();
        LoginController login2 = new LoginController(bank2);
        RegistrationController register2 = new RegistrationController(bank2, setup2.getCustomerDAO(), setup2.getAccountDAO());
        AccountController accounts2 = new AccountController(bank2, login2, setup2.getAccountDAO(), setup2.getTransactionDAO());

        ActionResult again = login2.login("john", "pass123");
        check("IT-22", "After the restart John can still log in", "true", "" + again.isOk());

        ArrayList<String> summaries = accounts2.getAccountSummaries();
        check("IT-23", "Balances are restored: Savings, Investment, Cheque", "BWP2501.25, BWP1050.00, BWP4700.00",
                balanceOf(summaries.get(0)) + ", " + balanceOf(summaries.get(1)) + ", " + balanceOf(summaries.get(2)));

        check("IT-24", "Transaction history of Savings is restored", "3 transactions",
                accounts2.getTransactionLines(accounts2.getAccountNumberAt(0)).size() + " transactions");

        check("IT-25", "The Cheque account opened earlier is restored with its employer", "4 accounts, Employer: Acme Ltd (Gaborone)",
                summaries.size() + " accounts, " + employerLine(accounts2.describeAccount(accounts2.getAccountNumberAt(3))));

        login2.logout();
        ActionResult amyAgain = login2.login("amy", "secret");
        ActionResult kgosiAgain = login2.login("kgosi", "pw");
        check("IT-26", "Customers registered before the restart can log in (Individual and Company)", "true, true",
                amyAgain.isOk() + ", " + kgosiAgain.isOk());
        login2.logout();

        ActionResult newAfter = register2.registerIndividual("Zed", "Moyo", "Plot 1", "zed", "pw", "600");
        check("IT-27", "New customer after the restart gets new unique IDs and account numbers", "true, 10 accounts in total",
                newAfter.isOk() + ", " + count("SELECT COUNT(*) FROM accounts", null) + " accounts in total");

        // ---------- Full user flow across a second restart ----------
        System.out.println("\n--- Full user flow: login, deposit, logout, restart, login ---\n");
        login2.login("john", "pass123");
        ActionResult flowDeposit = accounts2.deposit(accounts2.getAccountNumberAt(0), "100");
        login2.logout();

        AppSetup setup3 = new AppSetup(url);
        Bank bank3 = setup3.loadBank();
        LoginController login3 = new LoginController(bank3);
        AccountController accounts3 = new AccountController(bank3, login3, setup3.getAccountDAO(), setup3.getTransactionDAO());
        login3.login("john", "pass123");
        check("IT-28", "Deposit made before a restart is still there afterwards", "true, BWP2601.25 (database BWP2601.25)",
                flowDeposit.isOk() + ", " + balanceOf(accounts3.getAccountSummaries().get(0))
                        + " (database " + money(dbBalance(accounts3.getAccountNumberAt(0))) + ")");

        // ---------- Summary ----------
        System.out.println("\n===== SUMMARY =====");
        System.out.println("Tests run: " + (passed + failed) + "   Passed: " + passed + "   Failed: " + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ---------- Checking ----------

    private static void check(String id, String what, String expected, String actual) {
        boolean ok = expected.equals(actual);
        if (ok) {
            passed++;
        } else {
            failed++;
        }
        System.out.println(id + " | " + what + " | Expected: " + expected + " | Actual: " + actual
                + " | " + (ok ? "PASS" : "FAIL"));
    }

    // ---------- Direct database queries (do not use the DAO classes) ----------

    private static int count(String sql, String parameter) {
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (parameter != null) {
                ps.setString(1, parameter);
            }
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static String text(String sql, String parameter) {
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, parameter);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getString(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static double text2(String sql, String parameter) {
        return Double.parseDouble(text(sql, parameter));
    }

    private static double dbBalance(String accountNumber) {
        return text2("SELECT balance FROM accounts WHERE account_number = ?", accountNumber);
    }

    private static String money(double amount) {
        return "BWP" + String.format("%.2f", amount);
    }

    // "Savings | S1001 | Status: Active | Balance: BWP2501.25" -> "BWP2501.25"
    private static String balanceOf(String summary) {
        return summary.substring(summary.indexOf("Balance: ") + 9);
    }

    // The "Employer: ..." line of an account description
    private static String employerLine(String description) {
        int start = description.indexOf("Employer:");
        return description.substring(start).replaceAll("\\s+", " ");
    }
}
