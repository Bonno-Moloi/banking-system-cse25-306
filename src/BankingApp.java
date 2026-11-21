import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Starts the JavaFX application and switches between the screens.
public class BankingApp extends Application {

    private Stage stage;
    private BankController controller;

    public void start(Stage primaryStage) {
        stage = primaryStage;
        controller = new BankController();
        stage.setTitle("Banking System");
        showLogin();
        stage.show();
    }

    public void showLogin() {
        LoginView view = new LoginView(this, controller);
        setScreen(view.getRoot(), 460, 460);
    }

    public void showRegister() {
        RegisterView view = new RegisterView(this, controller);
        setScreen(view.getRoot(), 520, 600);
    }

    public void showDashboard() {
        DashboardView view = new DashboardView(this, controller);
        setScreen(view.getRoot(), 760, 560);
    }

    public void showAccount(Account account) {
        AccountView view = new AccountView(this, controller, account);
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
