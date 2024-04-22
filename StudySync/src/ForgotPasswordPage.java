import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ForgotPasswordPage extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Create labels, text fields, and buttons for forgot password page
        Label forgotPasswordLabel = new Label("Forgot Password");
        forgotPasswordLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label emailLabel = new Label("Enter your email:");
        TextField emailField = new TextField();
        Button submitButton = new Button("Submit");
        Button backButton = new Button("Back");

        // Create a layout for the forgot password form
        VBox forgotPasswordForm = new VBox(10);
        forgotPasswordForm.getChildren().addAll(forgotPasswordLabel, emailLabel, emailField, submitButton, backButton);
        forgotPasswordForm.setAlignment(Pos.CENTER);
        VBox.setMargin(forgotPasswordLabel, new Insets(0, 0, 20, 0));

        // Create a main layout for the scene
        BorderPane root = new BorderPane();
        root.setCenter(forgotPasswordForm);
        BorderPane.setMargin(forgotPasswordForm, new Insets(50));

        // Event handler for submit button
        submitButton.setOnAction(e -> {
            // Send email
            String recipient = emailField.getText();
            if (isValidEmail(recipient)) {
                displayConfirmationAlert();
            } else {
                displayErrorAlert("Invalid Email", "Please enter a valid email address.");
            }
        });

        // Event handler for back button
        backButton.setOnAction(e -> {
            // Go back to the previous scene
            primaryStage.close(); // Close the current stage
            // Call the previous scene's start method
            new LoginPage().start(new Stage());
        });

        // Set up the scene and display it
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Forgot Password");
        primaryStage.show();
    }

    private boolean isValidEmail(String email) {
        // Basic email validation
        return email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }

    private void displayConfirmationAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Email Sent");
        alert.setHeaderText(null);
        alert.setContentText("Please check your email for password recovery instructions.");
        alert.showAndWait();
    }

    private void displayErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
