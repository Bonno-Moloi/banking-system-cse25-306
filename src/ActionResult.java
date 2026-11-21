// The answer a controller gives to a screen: did it work, and what should the user be told?
public class ActionResult {

    private boolean ok;
    private String message;

    public ActionResult(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    public boolean isOk() {
        return ok;
    }

    public String getMessage() {
        return message;
    }
}
