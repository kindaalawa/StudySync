import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPage extends Application {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/StudySync";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root123@";

    @Override
    public void start(Stage primaryStage) {
        // Create a VBox to hold all the elements
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(20);
        
        // Add styling to the root
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #dcdcdc, #a0a0a0);");

        // Create UI components
        Label loginLabel = new Label("Study Think");
         loginLabel.setStyle("-fx-font-size: 36px; -fx-font-family: 'Arial Black'; -fx-font-weight: bold; -fx-text-fill: black;");
        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-text-fill: navy;");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-text-fill: navy;");
        PasswordField passwordField = new PasswordField();
        Label userIdLabel = new Label("User ID:");
        userIdLabel.setStyle("-fx-text-fill: navy;");
        TextField userIdField = new TextField(); // Adding the user ID field
        Button loginButton = new Button("Login");
        Label signupLabel = new Label("Don't have an account? Sign up!");
        signupLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        Button signupButton = new Button("Sign Up");
        Hyperlink forgotPasswordLink = new Hyperlink("Forgot your password?");
        forgotPasswordLink.setStyle("-fx-font-size: 14px; -fx-text-fill: blue;");

        // Add all components to the root VBox
        root.getChildren().addAll(loginLabel, usernameLabel, usernameField, passwordLabel, passwordField, userIdLabel, userIdField, loginButton, forgotPasswordLink, signupLabel, signupButton);

        // Event handlers
        signupButton.setOnAction(e -> {
            SignupPage signupPage = new SignupPage();
            Stage signupStage = new Stage();
            signupPage.start(signupStage);
            primaryStage.close();
        });

        forgotPasswordLink.setOnAction(e -> {
            ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage();
            Stage forgotPasswordStage = new Stage();
            forgotPasswordPage.start(forgotPasswordStage);
        });

      loginButton.setOnAction(e -> {
    String username = usernameField.getText();
    String password = passwordField.getText();
    String userIdText = userIdField.getText();
    
    // Check if any of the fields are empty
    if (username.isEmpty() || password.isEmpty() || userIdText.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText("Please fill in all fields.");
        alert.showAndWait();
        return; // Exit the event handler
    }

    int userId;
    try {
        userId = Integer.parseInt(userIdText);
    } catch (NumberFormatException ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText("Invalid User ID. Please enter a valid number.");
        alert.showAndWait();
        return; // Exit the event handler
    }
    
    if (validateCredentials(userId, username, password)) {
        openMainApp(userId);
        primaryStage.close();
    } else {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText("Invalid username, password, or user ID");
        alert.showAndWait();
    }
});


        // Set up the scene and display it
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    private boolean validateCredentials(int userId, String username, String password) {
        
        // http request on sprig localhost:8080/api/login?username?password
        // result 
        
        String query = "SELECT encrypted_password FROM Users WHERE user_id = ? AND username = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setString(2, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String hashedPasswordFromDB = resultSet.getString("encrypted_password");
                    return hashedPasswordFromDB.equals(password);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle any SQL exceptions
        }
        return false;
    }

    private void openMainApp(int userId) {
        MainApp mainApp = new MainApp(userId);
        Stage mainStage = new Stage();
        mainApp.start(mainStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
