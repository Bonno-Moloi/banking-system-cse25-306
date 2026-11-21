// Cheque account: can be linked to an employer for salary deposits.
public class ChequeAccount extends Account {

    private String employerName;
    private String employerAddress;

    public ChequeAccount(String accountNumber, String branch) {
        super(accountNumber, branch);
        this.employerName = "";
        this.employerAddress = "";
    }

    public void linkEmployer(String employerName, String employerAddress) {
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    public String getEmployerName() {
        return employerName;
    }

    public String getEmployerAddress() {
        return employerAddress;
    }
}
