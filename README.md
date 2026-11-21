# Banking System (OOAD Assignment)

Java project with a JavaFX GUI and an H2 database accessed through JDBC.

## Layers
**Boundary classes (GUI) - only talk to the controllers:**
LoginView, RegisterView, DashboardView, AccountView
(plus BankingApp, which starts the program, and ViewHelper for shared styling)

**Control classes - check input and manage the flow:**
LoginController, RegistrationController, AccountController
(ActionResult is the answer a controller gives back to a screen)

**Entity classes (core domain model) - business rules, no GUI or database code:**
Bank, Account (abstract), SavingsAccount, InvestmentAccount, ChequeAccount,
Customer (abstract), Individual, Company, Transaction

**Data access classes (JDBC) - all the SQL lives here:**
DatabaseManager (connection + tables), CustomerDAO, AccountDAO, TransactionDAO,
DatabaseException; AppSetup loads the saved data when the program starts

## Database
H2 database, saved in the file `data/bankdb.mv.db` (created on first run, not committed).
Schema: `database/schema.sql` (tables: customers, accounts, transactions).

## Run the GUI (GitHub Codespaces)
```
mvn javafx:run
```
Open the **Ports** tab, click the link on the "Desktop (JavaFX window)" row, click Connect,
and enter the password `vscode` to see the application window.
Data you enter is saved and is still there the next time you run the program.

## Run the tests (console)
```
mvn compile exec:java -Dexec.mainClass=DatabaseTest
mvn compile exec:java -Dexec.mainClass=Main
```
DatabaseTest shows Create, Read, Update and Delete using a temporary in-memory database.
Main tests the core model only.

## Demo login
Username: `john`   Password: `pass123`
