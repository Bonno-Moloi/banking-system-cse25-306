import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// Data access class for the customers table.
// Individual and Company customers share one table; the customer_type
// column says which one a row is.
public class CustomerDAO {

    private DatabaseManager db;
    private AccountDAO accountDAO;

    public CustomerDAO(DatabaseManager db, AccountDAO accountDAO) {
        this.db = db;
        this.accountDAO = accountDAO;
    }

    // CREATE: saves the customer row (accounts are saved with AccountDAO)
    public void insert(Customer customer) {
        String sql = "INSERT INTO customers (customer_id, customer_type, first_name, surname, "
                + "company_name, address, username, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customer.getCustomerID());
            if (customer instanceof Individual) {
                Individual individual = (Individual) customer;
                ps.setString(2, "INDIVIDUAL");
                ps.setString(3, individual.getFirstName());
                ps.setString(4, individual.getSurname());
                ps.setString(5, null);
            } else {
                Company company = (Company) customer;
                ps.setString(2, "COMPANY");
                ps.setString(3, null);
                ps.setString(4, null);
                ps.setString(5, company.getCompanyName());
            }
            ps.setString(6, customer.getAddress());
            ps.setString(7, customer.getUsername());
            ps.setString(8, customer.getPassword());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not save the customer", e);
        }
    }

    // READ: one customer with all their accounts, or null if not found
    public Customer findById(String customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return buildCustomer(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Could not read the customer", e);
        }
    }

    // READ: every customer with all their accounts
    public ArrayList<Customer> findAll() {
        String sql = "SELECT * FROM customers "
                + "ORDER BY CAST(REGEXP_REPLACE(customer_id, '[^0-9]', '') AS INT)";
        ArrayList<Customer> list = new ArrayList<Customer>();
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(buildCustomer(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not read the customers", e);
        }
        return list;
    }

    // UPDATE: saves the customer's current address
    public void update(Customer customer) {
        String sql = "UPDATE customers SET address = ? WHERE customer_id = ?";
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customer.getAddress());
            ps.setString(2, customer.getCustomerID());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not update the customer", e);
        }
    }

    // DELETE: removes the customer together with their accounts and transactions
    public void delete(String customerId) {
        accountDAO.deleteByCustomer(customerId);
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, customerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not delete the customer", e);
        }
    }

    // Turns one row of the customers table back into an Individual or a Company,
    // and loads their accounts.
    private Customer buildCustomer(ResultSet rs) throws SQLException {
        String id = rs.getString("customer_id");
        Customer customer;
        if (rs.getString("customer_type").equals("INDIVIDUAL")) {
            customer = new Individual(id, rs.getString("first_name"), rs.getString("surname"),
                    rs.getString("address"), rs.getString("username"), rs.getString("password"));
        } else {
            customer = new Company(id, rs.getString("company_name"),
                    rs.getString("address"), rs.getString("username"), rs.getString("password"));
        }
        ArrayList<Account> accounts = accountDAO.findByCustomer(id);
        for (int i = 0; i < accounts.size(); i++) {
            customer.openAccount(accounts.get(i));
        }
        return customer;
    }
}
