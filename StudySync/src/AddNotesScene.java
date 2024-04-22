import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea; // Import TextArea
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.sql.ResultSet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddNotesScene {

    private Stage primaryStage;
    private Scene previousScene;
    private Scene scene;
    private int userId; // Store the userId here

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/StudySync";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root123@";

    public AddNotesScene(Stage primaryStage, Scene previousScene, int userId) {
        this.primaryStage = primaryStage;
        this.previousScene = previousScene;
        this.userId = userId; // Assign the userId
        this.scene = createScene();
    }

    public Scene getScene() {
        return scene;
    }

    private Scene createScene() {
        // Text fields to capture necessary information for adding notes
        TextField courseIdField = new TextField();
        courseIdField.setPromptText("Course ID");

        // Add input validation for the Course ID field
        courseIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                courseIdField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextArea infoTypeField = new TextArea(); 
        infoTypeField.setPromptText("Info Type");
        infoTypeField.setPrefRowCount(5); 

        // Button to trigger insertion
        Button insertButton = new Button("Insert");
        insertButton.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-weight: bold;");
        insertButton.setOnAction(e -> {
            // Validate if fields are empty
            if (courseIdField.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in Course ID field.");
                return; // Exit the method if fields are empty
            }

            // Get data from text fields
            int courseId;
            try {
                courseId = Integer.parseInt(courseIdField.getText());
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Course ID must be a valid integer.");
                return; // Exit the method if Course ID is not a valid integer
            }

            String title = titleField.getText();
            String infoType = infoTypeField.getText();

            // Insert note into database
            insertInfo(courseId, userId, title, infoType);
        });

        // Back button to return to the previous scene
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #A9A9A9; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> primaryStage.setScene(previousScene));

        // Layout to organize the text fields and buttons
        VBox layout = new VBox(10);
        layout.getChildren().addAll(courseIdField, titleField, infoTypeField, insertButton, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        // Create scene with the layout
        Scene scene = new Scene(layout, 400, 400); // Increased height to accommodate the larger text field
        scene.setFill(Color.LIGHTGRAY);
        return scene;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void insertInfo(int courseId, int userId, String title, String infoType) {
        try {
            // Establishing a database connection
            Connection connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD);

            // Check if the course ID exists in the database
            String checkSql = "SELECT COUNT(*) FROM Courses WHERE course_id = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setInt(1, courseId);
            ResultSet resultSet = checkStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count == 0) {
                // Course ID does not exist, show alert message
                showAlert(Alert.AlertType.ERROR, "Error", "The provided Course ID does not exist.");
            } else {
                // SQL statement to insert note into the database
                String sql = "INSERT INTO Info (course_id, user_id, title, info_type) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, courseId);
                statement.setInt(2, userId);
                statement.setString(3, title);
                statement.setString(4, infoType);

                // Execute the SQL statement
                statement.executeUpdate();

                // Close the JDBC resources
                statement.close();
            }

            // Close the remaining JDBC resources
            resultSet.close();
            checkStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
