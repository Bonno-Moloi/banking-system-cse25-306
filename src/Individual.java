// A customer who is a person.
public class Individual extends Customer {

    private String firstName;
    private String surname;

    public Individual(String customerID, String firstName, String surname,
                      String address, String username, String password) {
        super(customerID, address, username, password);
        this.firstName = firstName;
        this.surname = surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getDisplayName() {
        return firstName + " " + surname;
    }
}
