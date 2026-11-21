# Banking System (OOAD Assignment)

Java project with a JavaFX GUI, built in three separate layers.

## Layers
**Boundary classes (GUI) - only talk to the controllers:**
LoginView, RegisterView, DashboardView, AccountView
(plus BankingApp, which starts the program, and ViewHelper for shared styling)

**Control classes - check input and manage the flow:**
LoginController, RegistrationController, AccountController
(ActionResult is the answer a controller gives back to a screen)

**Entity classes (core domain model) - business rules, no GUI code:**
Bank, Account (abstract), SavingsAccount, InvestmentAccount, ChequeAccount,
Customer (abstract), Individual, Company, Transaction

**Console test of the core model only:** Main

## Run the GUI (GitHub Codespaces)
```
mvn javafx:run
```
Open the **Ports** tab, click the link on the "Desktop (JavaFX window)" row, click Connect,
and enter the password `vscode` to see the application window.

## Run the console test only
```
mvn compile
java -cp target/classes Main
```

## Demo login
Username: `john`   Password: `pass123`
