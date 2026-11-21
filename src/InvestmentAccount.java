// Investment account: earns 5% interest per month.
// It must be opened with at least BWP500.00.
public class InvestmentAccount extends Account {

    private double interestRate = 0.05;               // 5%
    private double minimumOpeningBalance = 500;

    // The opening deposit is passed in so the minimum can be checked.
    public InvestmentAccount(String accountNumber, String branch, double openingDeposit) {
        super(accountNumber, branch);
        if (openingDeposit < minimumOpeningBalance) {
            throw new IllegalArgumentException(
                    "An Investment account needs at least BWP" + String.format("%.2f", minimumOpeningBalance) + " to open.");
        }
        deposit(openingDeposit);
    }

    // Used only when an existing account is loaded back from the database
    // (its opening deposit was already checked when it was first opened).
    public InvestmentAccount(String accountNumber, String branch) {
        super(accountNumber, branch);
    }

    public String getAccountType() {
        return "Investment";
    }

    public double getInterestRate() {
        return interestRate;
    }

    public double getMinimumOpeningBalance() {
        return minimumOpeningBalance;
    }

    // Monthly interest
    public void addInterest() {
        double interest = getBalance() * interestRate;
        applyInterest(interest);
    }
}
