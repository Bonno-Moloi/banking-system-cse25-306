import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

// Boundary class: the dashboard that lists the customer's accounts.
// It only talks to the controllers, never to the core model.
public class DashboardView {

    private BankingApp app;
    private LoginController loginController;
    private AccountController accountController;
    private BorderPane root;
    private ListView<String> accountList;
    private TextField employerNameField;
    private TextField employerAddressField;
    private Label messageLabel;

    public DashboardView(BankingApp app, LoginController loginController, AccountController accountController) {
        this.app = app;
        this.loginController = loginController;
        this.accountController = accountController;

        // ----- Top: welcome message and logout -----
        Label welcome = ViewHelper.titleLabel(accountController.getWelcomeText());
        Label details = new Label(accountController.getCustomerDetails());
        details.setStyle("-fx-text-fill: gray;");
        VBox welcomeBox = new VBox(4, welcome, details);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button logoutButton = ViewHelper.secondaryButton("Logout");
        logoutButton.setOnAction(e -> handleLogout());
        HBox top = new HBox(10, welcomeBox, spacer, logoutButton);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(0, 0, 15, 0));

        // ----- Centre: list of accounts -----
        accountList = new ListView<String>();
        accountList.setPrefHeight(200);
        Button openButton = ViewHelper.primaryButton("Open Selected Account");
        openButton.setOnAction(e -> handleOpenAccount());
        VBox centre = new VBox(8, ViewHelper.headingLabel("Your Accounts"), accountList, openButton);

        // ----- Bottom: open a new Cheque account -----
        employerNameField = new TextField();
        employerNameField.setPromptText("Employer name (optional)");
        employerAddressField = new TextField();
        employerAddressField.setPromptText("Employer address (optional)");
        Button chequeButton = ViewHelper.secondaryButton("Open Cheque Account");
        chequeButton.setOnAction(e -> handleOpenCheque());
        HBox chequeRow = new HBox(10, employerNameField, employerAddressField, chequeButton);
        messageLabel = new Label();
        VBox bottom = new VBox(8, ViewHelper.headingLabel("Open a new Cheque account"), chequeRow, messageLabel);
        bottom.setPadding(new Insets(15, 0, 0, 0));

        root = new BorderPane();
        root.setTop(top);
        root.setCenter(centre);
        root.setBottom(bottom);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F4F6FA;");

        refreshAccounts();
    }

    public Parent getRoot() {
        return root;
    }

    private void refreshAccounts() {
        accountList.getItems().setAll(accountController.getAccountSummaries());
    }

    private void handleOpenAccount() {
        int index = accountList.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            ViewHelper.showMessage(messageLabel, "Please select an account from the list first.", false);
        } else {
            app.showAccount(accountController.getAccountNumberAt(index));
        }
    }

    private void handleOpenCheque() {
        ActionResult result = accountController.openChequeAccount(
                employerNameField.getText(), employerAddressField.getText());
        ViewHelper.showMessage(messageLabel, result.getMessage(), result.isOk());
        employerNameField.clear();
        employerAddressField.clear();
        refreshAccounts();
    }

    private void handleLogout() {
        loginController.logout();
        app.showLogin();
    }
}
