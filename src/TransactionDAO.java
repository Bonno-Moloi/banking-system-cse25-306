import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

// Data access class for the transactions table.
// Transactions are bank records, so they can be created, read and deleted,
// but never changed.
public class TransactionDAO {

    private DatabaseManager db;

    public TransactionDAO(DatabaseManager db) {
        this.db = db;
    }

    // CREATE
    public void insert(Transaction transaction, String accountNumber) {
        String sql = "INSERT INTO transactions "
                + "(account_number, transaction_type, amount, balance_after, transaction_date) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ps.setString(2, transaction.getType());
            ps.setDouble(3, transaction.getAmount());
            ps.setDouble(4, transaction.getBalanceAfter());
            ps.setTimestamp(5, new Timestamp(transaction.getDate().getTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not save the transaction", e);
        }
    }

    // READ: all transactions of one account, oldest first
    public ArrayList<Transaction> findByAccount(String accountNumber) {
        String sql = "SELECT transaction_type, amount, balance_after, transaction_date "
                + "FROM transactions WHERE account_number = ? ORDER BY transaction_id";
        ArrayList<Transaction> list = new ArrayList<Transaction>();
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Transaction(rs.getString("transaction_type"),
                        rs.getDouble("amount"),
                        rs.getDouble("balance_after"),
                        rs.getTimestamp("transaction_date")));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not read the transactions", e);
        }
        return list;
    }

    // DELETE: all transactions of one account
    public void deleteByAccount(String accountNumber) {
        String sql = "DELETE FROM transactions WHERE account_number = ?";
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not delete the transactions", e);
        }
    }
}
