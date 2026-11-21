// A customer who is a company.
public class Company extends Customer {

    private String companyName;

    public Company(String customerID, String companyName,
                   String address, String username, String password) {
        super(customerID, address, username, password);
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getDisplayName() {
        return companyName;
    }
}
