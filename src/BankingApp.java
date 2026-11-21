import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Starts the JavaFX application, creates the model and controllers once,
// and switches between the screens.
public class BankingApp extends Application {

    private Stage stage;
    private LoginController loginController;
    private RegistrationController registrationController;
    private AccountController accountController;

    public void start(Stage primaryStage) {
        stage = primaryStage;

        Bank bank = new Bank();
        loginController = new LoginController(bank);
        registrationController = new RegistrationController(bank);
        accountController = new AccountController(bank, loginController);

        stage.setTitle("Banking System");
        showLogin();
        stage.show();
    }

    public void showLogin() {
        LoginView view = new LoginView(this, loginController);
        setScreen(view.getRoot(), 460, 460);
    }

    public void showRegister() {
        RegisterView view = new RegisterView(this, registrationController);
        setScreen(view.getRoot(), 520, 600);
    }

    public void showDashboard() {
        DashboardView view = new DashboardView(this, loginController, accountController);
        setScreen(view.getRoot(), 760, 560);
    }

    public void showAccount(String accountNumber) {
        AccountView view = new AccountView(this, accountController, accountNumber);
        setScreen(view.getRoot(), 900, 560);
    }

    private void setScreen(Parent root, double width, double height) {
        stage.setScene(new Scene(root, width, height));
        stage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
