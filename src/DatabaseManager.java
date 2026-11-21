import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Opens connections to the H2 database and creates the tables (the schema).
// The same SQL is also saved in database/schema.sql.
public class DatabaseManager {

    // The database is saved in the file data/bankdb.mv.db
    public static final String DEFAULT_URL = "jdbc:h2:./data/bankdb";

    private String url;

    public DatabaseManager() {
        this.url = DEFAULT_URL;
    }

    // Used by the test program, which uses a temporary in-memory database.
    public DatabaseManager(String url) {
        this.url = url;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, "sa", "");
    }

    public void createTables() {
        String customers = "CREATE TABLE IF NOT EXISTS customers ("
                + "customer_id VARCHAR(20) PRIMARY KEY, "
                + "customer_type VARCHAR(12) NOT NULL, "
                + "first_name VARCHAR(50), "
                + "surname VARCHAR(50), "
                + "company_name VARCHAR(100), "
                + "address VARCHAR(200) NOT NULL, "
                + "username VARCHAR(50) NOT NULL UNIQUE, "
                + "password VARCHAR(100) NOT NULL)";

        String accounts = "CREATE TABLE IF NOT EXISTS accounts ("
                + "account_number VARCHAR(20) PRIMARY KEY, "
                + "account_type VARCHAR(12) NOT NULL, "
                + "customer_id VARCHAR(20) NOT NULL, "
                + "branch VARCHAR(100) NOT NULL, "
                + "balance DECIMAL(15,2) NOT NULL, "
                + "status VARCHAR(12) NOT NULL, "
                + "employer_name VARCHAR(100), "
                + "employer_address VARCHAR(200), "
                + "FOREIGN KEY (customer_id) REFERENCES customers(customer_id))";

        String transactions = "CREATE TABLE IF NOT EXISTS transactions ("
                + "transaction_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "account_number VARCHAR(20) NOT NULL, "
                + "transaction_type VARCHAR(20) NOT NULL, "
                + "amount DECIMAL(15,2) NOT NULL, "
                + "balance_after DECIMAL(15,2) NOT NULL, "
                + "transaction_date TIMESTAMP NOT NULL, "
                + "FOREIGN KEY (account_number) REFERENCES accounts(account_number))";

        try (Connection con = getConnection(); Statement st = con.createStatement()) {
            st.execute(customers);
            st.execute(accounts);
            st.execute(transactions);
        } catch (SQLException e) {
            throw new DatabaseException("Could not create the database tables", e);
        }
    }
}
