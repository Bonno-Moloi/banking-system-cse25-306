import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

// Boundary class: the login screen. It only shows things and collects input.
public class LoginView {

    private BankingApp app;
    private LoginController loginController;
    private VBox root;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label messageLabel;

    public LoginView(BankingApp app, LoginController loginController) {
        this.app = app;
        this.loginController = loginController;

        Label title = ViewHelper.titleLabel("Banking System");
        Label subtitle = new Label("Please log in to continue");

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(260);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(260);
        passwordField.setOnAction(e -> handleLogin());      // pressing Enter logs in

        Button loginButton = ViewHelper.primaryButton("Login");
        loginButton.setOnAction(e -> handleLogin());

        Button registerButton = ViewHelper.secondaryButton("New customer? Register");
        registerButton.setOnAction(e -> app.showRegister());

        messageLabel = new Label();
        Label hint = new Label("Demo login: john / pass123");
        hint.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");

        root = new VBox(12, title, subtitle, usernameField, passwordField,
                loginButton, messageLabel, registerButton, hint);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #F4F6FA;");
    }

    public Parent getRoot() {
        return root;
    }

    private void handleLogin() {
        ActionResult result = loginController.login(usernameField.getText(), passwordField.getText());
        ViewHelper.showMessage(messageLabel, result.getMessage(), result.isOk());
        if (result.isOk()) {
            app.showDashboard();
        }
    }
}
