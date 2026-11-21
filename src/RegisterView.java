import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// Boundary class: the registration screen for new customers.
public class RegisterView {

    private BankingApp app;
    private BankController controller;
    private VBox root;

    private ComboBox<String> typeBox;
    private TextField firstNameField;
    private TextField surnameField;
    private TextField companyNameField;
    private TextField addressField;
    private TextField usernameField;
    private PasswordField passwordField;
    private TextField depositField;
    private Label messageLabel;

    private Label firstNameLabel;
    private Label surnameLabel;
    private Label companyNameLabel;

    public RegisterView(BankingApp app, BankController controller) {
        this.app = app;
        this.controller = controller;

        Label title = ViewHelper.titleLabel("Register");
        Label subtitle = new Label("Every customer gets a Savings and an Investment account.");

        typeBox = new ComboBox<String>();
        typeBox.getItems().addAll("Individual", "Company");
        typeBox.setValue("Individual");
        typeBox.setOnAction(e -> updateNameFields());

        firstNameLabel = new Label("First name:");
        firstNameField = new TextField();
        surnameLabel = new Label("Surname:");
        surnameField = new TextField();
        companyNameLabel = new Label("Company name:");
        companyNameField = new TextField();
        addressField = new TextField();
        usernameField = new TextField();
        passwordField = new PasswordField();
        depositField = new TextField();
        depositField.setPromptText("Minimum BWP500.00");

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);
        form.addRow(0, new Label("Customer type:"), typeBox);
        form.addRow(1, firstNameLabel, firstNameField);
        form.addRow(2, surnameLabel, surnameField);
        form.addRow(3, companyNameLabel, companyNameField);
        form.addRow(4, new Label("Address:"), addressField);
        form.addRow(5, new Label("Username:"), usernameField);
        form.addRow(6, new Label("Password:"), passwordField);
        form.addRow(7, new Label("Opening Investment deposit (BWP):"), depositField);

        Button registerButton = ViewHelper.primaryButton("Register");
        registerButton.setOnAction(e -> handleRegister());
        Button backButton = ViewHelper.secondaryButton("Back to Login");
        backButton.setOnAction(e -> app.showLogin());
        HBox buttons = new HBox(12, registerButton, backButton);
        buttons.setAlignment(Pos.CENTER);

        messageLabel = new Label();
        messageLabel.setMaxWidth(420);

        root = new VBox(14, title, subtitle, form, buttons, messageLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #F4F6FA;");

        updateNameFields();
    }

    public Parent getRoot() {
        return root;
    }

    // Shows the name fields that match the chosen customer type (display only).
    private void updateNameFields() {
        boolean individual = typeBox.getValue().equals("Individual");
        firstNameLabel.setVisible(individual);
        firstNameLabel.setManaged(individual);
        firstNameField.setVisible(individual);
        firstNameField.setManaged(individual);
        surnameLabel.setVisible(individual);
        surnameLabel.setManaged(individual);
        surnameField.setVisible(individual);
        surnameField.setManaged(individual);
        companyNameLabel.setVisible(!individual);
        companyNameLabel.setManaged(!individual);
        companyNameField.setVisible(!individual);
        companyNameField.setManaged(!individual);
    }

    private void handleRegister() {
        String message;
        if (typeBox.getValue().equals("Individual")) {
            message = controller.registerIndividual(firstNameField.getText(), surnameField.getText(),
                    addressField.getText(), usernameField.getText(), passwordField.getText(),
                    depositField.getText());
        } else {
            message = controller.registerCompany(companyNameField.getText(),
                    addressField.getText(), usernameField.getText(), passwordField.getText(),
                    depositField.getText());
        }
        ViewHelper.showMessage(messageLabel, message, controller.isLastActionOk());
    }
}
