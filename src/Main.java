// Simple test program to show the core model working (no GUI, no database).
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
        System.out.println();
        john.printAccounts();

        // 4. Investment account minimum balance rule
        System.out.println("\n--- Investment account minimum balance test ---");
        try {
            InvestmentAccount bad = new InvestmentAccount("I9999", "Main Mall Branch", 200);
            System.out.println("Created: " + bad);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // 5. Deposits
        System.out.println("\n--- Deposit tests ---");
        savings.deposit(2000);
        cheque.deposit(5000);
        savings.deposit(-50);   // invalid amount

        // 6. Withdrawals
        System.out.println("\n--- Withdrawal tests ---");
        System.out.println("Withdraw 300 from Cheque: " + cheque.withdraw(300));
        System.out.println("Withdraw 200 from Investment: " + investment.withdraw(200));
        System.out.println("Withdraw 100 from Savings: " + savings.withdraw(100));      // blocked
        System.out.println("Withdraw 99999 from Cheque: " + cheque.withdraw(99999));    // insufficient funds

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
        cheque.deposit(100);    // blocked while frozen
        cheque.unfreeze();
        System.out.println("Cheque status: " + cheque.getStatus());

        // 9. Transactions
        System.out.println("\n--- Transaction history ---");
        savings.printTransactions();
        System.out.println();
        investment.printTransactions();

        // 10. Company customer
        System.out.println("\n--- Company customer ---");
        Company shop = new Company("C002", "Kgosi Traders", "Plot 45, Francistown", "kgosi", "secret");
        shop.openAccount(new SavingsAccount("S1002", "Francistown Branch"));
        shop.printAccounts();
        System.out.println("Has required accounts: " + shop.hasRequiredAccounts());

        System.out.println("\n===== END OF TEST =====");
    }
}
