# Banking System (OOAD Assignment)

Java project with a JavaFX GUI.

## Structure
**Entity classes (core model):** Account (abstract), SavingsAccount, InvestmentAccount,
ChequeAccount, Customer (abstract), Individual, Company, Transaction

**Control class:** BankController (links the GUI to the core model)

**Boundary classes (GUI, no business logic):** LoginView, RegisterView, DashboardView, AccountView
(plus BankingApp, which starts the program and switches screens, and ViewHelper for shared styling)

**Console test of the core model:** Main

## Run the GUI (GitHub Codespaces)
```
mvn javafx:run
```
Then open the **Ports** tab, click the globe icon next to port **6080**, and enter the password `vscode`
to see the application window.

## Run the console test only
```
mvn compile
java -cp target/classes Main
```

## Demo login
Username: `john`   Password: `pass123`
