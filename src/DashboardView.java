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
public class DashboardView {

    private BankingApp app;
    private BankController controller;
    private BorderPane root;
    private ListView<Account> accountList;
    private TextField employerNameField;
    private TextField employerAddressField;
    private Label messageLabel;

    public DashboardView(BankingApp app, BankController controller) {
        this.app = app;
        this.controller = controller;

        // ----- Top: welcome message and logout -----
        Label welcome = ViewHelper.titleLabel(controller.getWelcomeText());
        Label details = new Label(controller.getCustomerDetails());
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
        accountList = new ListView<Account>();
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
        accountList.getItems().setAll(controller.getAccounts());
    }

    private void handleOpenAccount() {
        Account selected = accountList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            ViewHelper.showMessage(messageLabel, "Please select an account from the list first.", false);
        } else {
            app.showAccount(selected);
        }
    }

    private void handleOpenCheque() {
        String message = controller.openChequeAccount(employerNameField.getText(), employerAddressField.getText());
        ViewHelper.showMessage(messageLabel, message, controller.isLastActionOk());
        employerNameField.clear();
        employerAddressField.clear();
        refreshAccounts();
    }

    private void handleLogout() {
        controller.logout();
        app.showLogin();
    }
}
