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

// Boundary class: shows one account and lets the customer deposit, withdraw
// and view transactions. It only talks to the AccountController, using the
// account number to say which account it means.
public class AccountView {

    private BankingApp app;
    private AccountController accountController;
    private String accountNumber;
    private BorderPane root;

    private Label detailsLabel;
    private TextField amountField;
    private Button withdrawButton;
    private Button interestButton;
    private Label messageLabel;
    private ListView<String> transactionList;

    public AccountView(BankingApp app, AccountController accountController, String accountNumber) {
        this.app = app;
        this.accountController = accountController;
        this.accountNumber = accountNumber;

        // ----- Top: title and back button -----
        Label title = ViewHelper.titleLabel(accountController.getAccountTitle(accountNumber));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button backButton = ViewHelper.secondaryButton("Back to Accounts");
        backButton.setOnAction(e -> app.showDashboard());
        HBox top = new HBox(10, title, spacer, backButton);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(0, 0, 15, 0));

        // ----- Left: account details and actions -----
        detailsLabel = new Label();
        detailsLabel.setStyle("-fx-font-family: monospace; -fx-background-color: white; "
                + "-fx-padding: 12; -fx-border-color: #C8D0E0;");

        amountField = new TextField();
        amountField.setPromptText("Amount (BWP)");

        Button depositButton = ViewHelper.primaryButton("Deposit");
        depositButton.setOnAction(e -> handleDeposit());
        withdrawButton = ViewHelper.primaryButton("Withdraw");
        withdrawButton.setOnAction(e -> handleWithdraw());
        interestButton = ViewHelper.secondaryButton("Add Monthly Interest");
        interestButton.setOnAction(e -> handleInterest());

        // Buttons that do not apply to this account are greyed out.
        withdrawButton.setDisable(!accountController.allowsWithdrawals(accountNumber));
        interestButton.setDisable(!accountController.paysInterest(accountNumber));

        HBox actionButtons = new HBox(10, depositButton, withdrawButton);
        messageLabel = new Label();
        messageLabel.setMaxWidth(340);

        VBox left = new VBox(10, ViewHelper.headingLabel("Account Details"), detailsLabel,
                ViewHelper.headingLabel("Make a Transaction"), amountField, actionButtons,
                interestButton, messageLabel);
        left.setPrefWidth(360);

        // ----- Right: transaction history -----
        transactionList = new ListView<String>();
        VBox.setVgrow(transactionList, Priority.ALWAYS);
        VBox right = new VBox(8, ViewHelper.headingLabel("Transactions (newest first)"), transactionList);
        right.setPadding(new Insets(0, 0, 0, 20));
        HBox.setHgrow(right, Priority.ALWAYS);

        HBox centre = new HBox(left, right);

        root = new BorderPane();
        root.setTop(top);
        root.setCenter(centre);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F4F6FA;");

        refresh();
    }

    public Parent getRoot() {
        return root;
    }

    // Re-reads the details and transactions so the screen shows the latest values.
    private void refresh() {
        detailsLabel.setText(accountController.describeAccount(accountNumber));
        transactionList.getItems().setAll(accountController.getTransactionLines(accountNumber));
    }

    private void handleDeposit() {
        ActionResult result = accountController.deposit(accountNumber, amountField.getText());
        ViewHelper.showMessage(messageLabel, result.getMessage(), result.isOk());
        amountField.clear();
        refresh();
    }

    private void handleWithdraw() {
        ActionResult result = accountController.withdraw(accountNumber, amountField.getText());
        ViewHelper.showMessage(messageLabel, result.getMessage(), result.isOk());
        amountField.clear();
        refresh();
    }

    private void handleInterest() {
        ActionResult result = accountController.addInterest(accountNumber);
        ViewHelper.showMessage(messageLabel, result.getMessage(), result.isOk());
        refresh();
    }
}
