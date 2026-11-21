// Controller: handles registering new customers.
// It checks the input, asks the core model to create the customer and accounts,
// and asks the DAO classes to save them in the database.
public class RegistrationController {

    private Bank bank;
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;

    public RegistrationController(Bank bank, CustomerDAO customerDAO, AccountDAO accountDAO) {
        this.bank = bank;
        this.customerDAO = customerDAO;
        this.accountDAO = accountDAO;
    }

    public ActionResult registerIndividual(String firstName, String surname, String address,
                                           String username, String password, String depositText) {
        if (isEmpty(firstName) || isEmpty(surname)) {
            return new ActionResult(false, "Please enter your first name and surname.");
        }
        ActionResult problem = checkCommonFields(address, username, password);
        if (problem != null) {
            return problem;
        }
        Individual customer = new Individual(bank.newCustomerId(), firstName.trim(), surname.trim(),
                address.trim(), username.trim(), password);
        return openStartingAccounts(customer, depositText);
    }

    public ActionResult registerCompany(String companyName, String address,
                                        String username, String password, String depositText) {
        if (isEmpty(companyName)) {
            return new ActionResult(false, "Please enter the company name.");
        }
        ActionResult problem = checkCommonFields(address, username, password);
        if (problem != null) {
            return problem;
        }
        Company customer = new Company(bank.newCustomerId(), companyName.trim(),
                address.trim(), username.trim(), password);
        return openStartingAccounts(customer, depositText);
    }

    private boolean isEmpty(String text) {
        return text == null || text.trim().equals("");
    }

    // Returns an error result, or null if the common fields are fine.
    private ActionResult checkCommonFields(String address, String username, String password) {
        if (isEmpty(address) || isEmpty(username) || isEmpty(password)) {
            return new ActionResult(false, "Please fill in all the fields.");
        }
        if (bank.usernameTaken(username.trim())) {
            return new ActionResult(false, "That username is already taken.");
        }
        return null;
    }

    // Every customer gets a Savings and an Investment account.
    private ActionResult openStartingAccounts(Customer customer, String depositText) {
        double deposit;
        try {
            deposit = Double.parseDouble(depositText.trim());
        } catch (NumberFormatException e) {
            return new ActionResult(false, "Please enter a valid opening deposit amount.");
        }
        SavingsAccount savings = new SavingsAccount(bank.newAccountNumber("S"), bank.getBranch());
        InvestmentAccount investment;
        try {
            investment = new InvestmentAccount(bank.newAccountNumber("I"), bank.getBranch(), deposit);
        } catch (IllegalArgumentException e) {
            return new ActionResult(false, e.getMessage());
        }
        customer.openAccount(savings);
        customer.openAccount(investment);

        // Save to the database first, then add to the bank in memory.
        try {
            customerDAO.insert(customer);
            for (int i = 0; i < customer.getAccounts().size(); i++) {
                accountDAO.insert(customer.getAccounts().get(i), customer.getCustomerID());
            }
        } catch (DatabaseException e) {
            return new ActionResult(false, e.getMessage());
        }
        bank.addCustomer(customer);
        return new ActionResult(true,
                "Registration successful. Savings and Investment accounts opened. You can now log in.");
    }
}
