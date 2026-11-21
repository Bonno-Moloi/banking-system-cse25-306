import java.util.ArrayList;

// Simple console test of the core model only (no GUI, no controllers).
public class Main {

    public static void main(String[] args) {

        System.out.println("===== BANKING SYSTEM - CORE MODEL TEST =====\n");

        // 1. Create an individual customer
        Individual john = new Individual("C001", "John", "Dlamini",
                "Plot 123, Gaborone", "john", "pass123");
        System.out.println("Customer created: " + john.getDisplayName());

        // 2. Login test
        System.out.println("Login (correct password): " + john.login("john", "pass123"));
        System.out.println("Login (wrong password):   " + john.login("john", "wrong"));

        // 3. Open accounts
        SavingsAccount savings = new SavingsAccount("S1001", "Main Mall Branch");
        john.openAccount(savings);
        InvestmentAccount investment = new InvestmentAccount("I2001", "Main Mall Branch", 1000);
        john.openAccount(investment);
        ChequeAccount cheque = new ChequeAccount("CH3001", "Main Mall Branch");
        cheque.linkEmployer("Global Retail Solutions", "Gaborone");
        john.openAccount(cheque);

        System.out.println("\nCustomer has Savings and Investment accounts: " + john.hasRequiredAccounts());
        showAccounts(john);

        // 4. Investment account minimum balance rule
        System.out.println("\n--- Investment account minimum balance test ---");
        try {
            new InvestmentAccount("I9999", "Main Mall Branch", 200);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // 5. Deposits
        System.out.println("\n--- Deposit tests ---");
        System.out.println("Deposit 2000 to Savings: " + savings.deposit(2000));
        System.out.println("Deposit 5000 to Cheque:  " + cheque.deposit(5000));
        System.out.println("Deposit -50 to Savings:  " + savings.deposit(-50) + " (" + savings.getLastMessage() + ")");

        // 6. Withdrawals
        System.out.println("\n--- Withdrawal tests ---");
        System.out.println("Withdraw 300 from Cheque: " + cheque.withdraw(300));
        System.out.println("Withdraw 200 from Investment: " + investment.withdraw(200));
        System.out.println("Withdraw 100 from Savings: " + savings.withdraw(100) + " (" + savings.getLastMessage() + ")");
        System.out.println("Withdraw 99999 from Cheque: " + cheque.withdraw(99999) + " (" + cheque.getLastMessage() + ")");

        // 7. Monthly interest
        System.out.println("\n--- Monthly interest ---");
        System.out.println("Savings before:    BWP" + String.format("%.2f", savings.getBalance()));
        savings.addInterest();
        System.out.println("Savings after:     BWP" + String.format("%.2f", savings.getBalance()) + " (0.05%)");
        System.out.println("Investment before: BWP" + String.format("%.2f", investment.getBalance()));
        investment.addInterest();
        System.out.println("Investment after:  BWP" + String.format("%.2f", investment.getBalance()) + " (5%)");

        // 8. Account states
        System.out.println("\n--- Account state test ---");
        cheque.freeze();
        System.out.println("Cheque status: " + cheque.getStatus());
        System.out.println("Deposit while frozen: " + cheque.deposit(100) + " (" + cheque.getLastMessage() + ")");
        cheque.unfreeze();
        System.out.println("Cheque status: " + cheque.getStatus());

        // 9. Transactions
        System.out.println("\n--- Transaction history ---");
        showTransactions(savings);
        showTransactions(investment);

        // 10. Company customer
        System.out.println("\n--- Company customer ---");
        Company shop = new Company("C002", "Kgosi Traders", "Plot 45, Francistown", "kgosi", "secret");
        shop.openAccount(new SavingsAccount("S1002", "Francistown Branch"));
        showAccounts(shop);
        System.out.println("Has required accounts: " + shop.hasRequiredAccounts());

        System.out.println("\n===== END OF TEST =====");
    }

    private static void showAccounts(Customer customer) {
        System.out.println("Accounts for " + customer.getDisplayName() + ":");
        ArrayList<Account> accounts = customer.getAccounts();
        for (int i = 0; i < accounts.size(); i++) {
            Account a = accounts.get(i);
            System.out.println("  " + a.getAccountType() + " | " + a.getAccountNumber()
                    + " | Status: " + a.getStatus()
                    + " | Balance: BWP" + String.format("%.2f", a.getBalance()));
        }
    }

    private static void showTransactions(Account account) {
        System.out.println("Transactions for " + account.getAccountNumber() + ":");
        ArrayList<Transaction> list = account.getTransactions();
        for (int i = 0; i < list.size(); i++) {
            Transaction t = list.get(i);
            System.out.println("  " + t.getType() + " | BWP" + String.format("%.2f", t.getAmount())
                    + " | Balance: BWP" + String.format("%.2f", t.getBalanceAfter()));
        }
    }
}
