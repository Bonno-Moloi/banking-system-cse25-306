import javafx.scene.control.Button;
import javafx.scene.control.Label;

// Small helper used by all the screens so they look the same.
public class ViewHelper {

    public static Label titleLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #1F3864;");
        return label;
    }

    public static Label headingLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2E5597;");
        return label;
    }

    public static Button primaryButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #2E5597; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-padding: 8 18 8 18; -fx-cursor: hand;");
        return button;
    }

    public static Button secondaryButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #DDE4F0; -fx-text-fill: #1F3864; "
                + "-fx-padding: 8 18 8 18; -fx-cursor: hand;");
        return button;
    }

    // Shows a message in green (success) or red (error).
    public static void showMessage(Label label, String text, boolean ok) {
        label.setText(text);
        if (ok) {
            label.setStyle("-fx-text-fill: #1E7B34; -fx-font-weight: bold;");
        } else {
            label.setStyle("-fx-text-fill: #C0392B; -fx-font-weight: bold;");
        }
        label.setWrapText(true);
    }
}
