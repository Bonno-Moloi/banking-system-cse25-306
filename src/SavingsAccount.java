// Savings account: earns 0.05% interest per month. Withdrawals are NOT allowed.
public class SavingsAccount extends Account {

    private double interestRate = 0.0005;   // 0.05%

    public SavingsAccount(String accountNumber, String branch) {
        super(accountNumber, branch);
    }

    public String getAccountType() {
        return "Savings";
    }

    public double getInterestRate() {
        return interestRate;
    }

    // Overrides withdraw() to block all withdrawals.
    public boolean withdraw(double amount) {
        fail("Withdrawal failed: withdrawals are not allowed from a Savings account.");
        return false;
    }

    // Monthly interest
    public void addInterest() {
        double interest = getBalance() * interestRate;
        applyInterest(interest);
    }
}
