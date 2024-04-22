import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mindrot.jbcrypt.BCrypt;

public class SignupPage extends Application {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/StudySync";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root123@";

    // Email validation regex pattern
    private static final String EMAIL_REGEX = "^(.+)@(.+)$";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    private String encryptPassword(String password) {
        return password;
    }

    @Override
    public void start(Stage primaryStage) {
        // Create labels, text fields, and buttons for sign-up
        Label signupLabel = new Label("Sign Up");
        signupLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: black;");
        Label userIdLabel = new Label("User ID:");
        TextField userIdField = new TextField();
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Label confirmPasswordLabel = new Label("Confirm Password:");
        PasswordField confirmPasswordField = new PasswordField();
        Button signupButton = new Button("Sign Up");

        // Create a hyperlink for login page
        Hyperlink loginLink = new Hyperlink("Already have an account? Go to the login page");
        loginLink.setOnAction(event -> openLoginPage(primaryStage));

        // Create a layout for the sign-up form
        VBox signupForm = new VBox(10);
        signupForm.getChildren().addAll(signupLabel, userIdLabel, userIdField, usernameLabel, usernameField, emailLabel, emailField, passwordLabel, passwordField, confirmPasswordLabel, confirmPasswordField, signupButton, loginLink);
        signupForm.setAlignment(Pos.CENTER);
        VBox.setMargin(signupLabel, new Insets(0, 0, 20, 0));

        // Create a main layout for the scene
        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.rgb(169, 169, 169), null, null))); // Set gray background
        root.setCenter(signupForm);
        BorderPane.setMargin(signupForm, new Insets(50));

        // Event handler for signup button
        signupButton.setOnAction(e -> {
            int userId = Integer.parseInt(userIdField.getText());
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showAlert("Signup Error", "Please fill in all fields.");
            } else if (!isValidEmail(email)) {
                showAlert("Signup Error", "Please enter a valid email address.");
            } else if (!password.equals(confirmPassword)) {
                showAlert("Signup Error", "Passwords do not match.");
            } else {
                String encryptedPassword = encryptPassword(password);
                insertUser(userId, username, email, encryptedPassword, primaryStage);
            }
        });

        // Set up the scene and display it
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sign Up");
        primaryStage.show();
    }

    private void insertUser(int userId, String username, String email, String password, Stage primaryStage) {
        String selectQuery = "SELECT COUNT(*) AS count FROM Users WHERE email = ? OR username = ?";
        String insertQuery = "INSERT INTO Users (user_id, username, email, encrypted_password, date_of_creation) VALUES (?, ?, ?, ?, CURDATE())";

        try ( Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);  PreparedStatement selectStmt = conn.prepareStatement(selectQuery);  PreparedStatement insertStmt = conn.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {

            selectStmt.setString(1, email);
            selectStmt.setString(2, username);
            ResultSet resultSet = selectStmt.executeQuery();
            resultSet.next();
            int count = resultSet.getInt("count");

            if (count > 0) {
                showAlert("Signup Error", "Email or username already exists.");
            } else {
                insertStmt.setInt(1, userId);
                insertStmt.setString(2, username);
                insertStmt.setString(3, email);
                insertStmt.setString(4, password); // Store the password directly
                insertStmt.executeUpdate();

                showAlert("Signup Success", "Account created successfully!");
                openMainApp(primaryStage, userId);
                primaryStage.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to insert user into the database.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void openMainApp(Stage primaryStage, int userId) {
        MainApp mainApp = new MainApp(userId);

        Stage mainStage = new Stage();
        mainApp.start(mainStage);
    }

    private void openLoginPage(Stage primaryStage) {
        LoginPage loginPage = new LoginPage(); // Assuming LoginPage is your login page class
        loginPage.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
