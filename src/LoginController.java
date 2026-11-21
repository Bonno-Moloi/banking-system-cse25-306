// Controller: handles logging in and out. The login screen talks to this class,
// and this class talks to the core model (Bank and Customer).
public class LoginController {

    private Bank bank;
    private Customer loggedInCustomer;

    public LoginController(Bank bank) {
        this.bank = bank;
        this.loggedInCustomer = null;
    }

    public ActionResult login(String username, String password) {
        if (username == null || username.trim().equals("")
                || password == null || password.equals("")) {
            return new ActionResult(false, "Please enter your username and password.");
        }
        Customer customer = bank.authenticate(username.trim(), password);
        if (customer == null) {
            return new ActionResult(false, "Invalid username or password.");
        }
        loggedInCustomer = customer;
        return new ActionResult(true, "Login successful.");
    }

    public void logout() {
        loggedInCustomer = null;
    }

    public boolean isLoggedIn() {
        return loggedInCustomer != null;
    }

    // Used by the other controllers (not by the screens).
    public Customer getLoggedInCustomer() {
        return loggedInCustomer;
    }
}
