import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// Data access class for the accounts table.
// SavingsAccount, InvestmentAccount and ChequeAccount all share one table;
// the account_type column says which one a row is.
public class AccountDAO {

    private DatabaseManager db;
    private TransactionDAO transactionDAO;

    public AccountDAO(DatabaseManager db, TransactionDAO transactionDAO) {
        this.db = db;
        this.transactionDAO = transactionDAO;
    }

    // CREATE: saves the account and the transactions it already has
    public void insert(Account account, String customerId) {
        String sql = "INSERT INTO accounts (account_number, account_type, customer_id, branch, "
                + "balance, status, employer_name, employer_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, account.getAccountNumber());
            ps.setString(2, account.getAccountType().toUpperCase());
            ps.setString(3, customerId);
            ps.setString(4, account.getBranch());
            ps.setDouble(5, account.getBalance());
            ps.setString(6, account.getStatus());
            ps.setString(7, employerNameOf(account));
            ps.setString(8, employerAddressOf(account));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not save the account", e);
        }
        ArrayList<Transaction> transactions = account.getTransactions();
        for (int i = 0; i < transactions.size(); i++) {
            transactionDAO.insert(transactions.get(i), account.getAccountNumber());
        }
    }

    // READ: one account (with its transactions), or null if it does not exist
    public Account findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return buildAccount(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Could not read the account", e);
        }
    }

    // READ: all accounts of one customer, in the order they were opened
    public ArrayList<Account> findByCustomer(String customerId) {
        String sql = "SELECT * FROM accounts WHERE customer_id = ? "
                + "ORDER BY CAST(REGEXP_REPLACE(account_number, '[^0-9]', '') AS INT)";
        ArrayList<Account> list = new ArrayList<Account>();
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(buildAccount(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not read the accounts", e);
        }
        return list;
    }

    // UPDATE: saves the current balance, status and employer of the account
    public void update(Account account) {
        String sql = "UPDATE accounts SET balance = ?, status = ?, employer_name = ?, "
                + "employer_address = ? WHERE account_number = ?";
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, account.getBalance());
            ps.setString(2, account.getStatus());
            ps.setString(3, employerNameOf(account));
            ps.setString(4, employerAddressOf(account));
            ps.setString(5, account.getAccountNumber());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not update the account", e);
        }
    }

    // DELETE: removes the account and its transactions
    public void delete(String accountNumber) {
        transactionDAO.deleteByAccount(accountNumber);
        String sql = "DELETE FROM accounts WHERE account_number = ?";
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not delete the account", e);
        }
    }

    // DELETE: removes all accounts (and their transactions) of one customer
    public void deleteByCustomer(String customerId) {
        ArrayList<Account> accounts = findByCustomer(customerId);
        for (int i = 0; i < accounts.size(); i++) {
            delete(accounts.get(i).getAccountNumber());
        }
    }

    // ----- Helpers -----

    // Turns one row of the accounts table back into the right kind of Account object.
    private Account buildAccount(ResultSet rs) throws SQLException {
        String type = rs.getString("account_type");
        String number = rs.getString("account_number");
        String branch = rs.getString("branch");

        Account account;
        if (type.equals("SAVINGS")) {
            account = new SavingsAccount(number, branch);
        } else if (type.equals("INVESTMENT")) {
            account = new InvestmentAccount(number, branch);
        } else {
            ChequeAccount cheque = new ChequeAccount(number, branch);
            String employerName = rs.getString("employer_name");
            if (employerName != null && !employerName.equals("")) {
                cheque.linkEmployer(employerName, rs.getString("employer_address"));
            }
            account = cheque;
        }
        account.restoreState(rs.getDouble("balance"), rs.getString("status"));

        ArrayList<Transaction> transactions = transactionDAO.findByAccount(number);
        for (int i = 0; i < transactions.size(); i++) {
            account.addRestoredTransaction(transactions.get(i));
        }
        return account;
    }

    // Only Cheque accounts have an employer; the other types store nothing.
    private String employerNameOf(Account account) {
        if (account instanceof ChequeAccount) {
            return ((ChequeAccount) account).getEmployerName();
        }
        return null;
    }

    private String employerAddressOf(Account account) {
        if (account instanceof ChequeAccount) {
            return ((ChequeAccount) account).getEmployerAddress();
        }
        return null;
    }
}
