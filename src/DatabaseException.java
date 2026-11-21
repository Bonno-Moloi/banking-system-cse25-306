// Thrown by the DAO classes when something goes wrong with the database.
public class DatabaseException extends RuntimeException {

    public DatabaseException(String message, Exception cause) {
        super(message + ": " + cause.getMessage(), cause);
    }
}
