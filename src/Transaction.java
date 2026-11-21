import java.util.Date;

// Records one transaction (deposit, withdrawal or interest) on an account.
public class Transaction {

    private String type;
    private double amount;
    private double balanceAfter;
    private Date date;

    public Transaction(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.date = new Date();
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public Date getDate() {
        return date;
    }

}
