import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CoursesScene {

    private Scene scene;
    private Scene previousScene;
    private int userId; // Added user_id field

    public CoursesScene(Stage primaryStage, Scene previousScene, int userId) { // Accept userId parameter
        this.previousScene = previousScene;
        this.userId = userId; // Set user_id
        initialize(primaryStage);
    }

    public void initialize(Stage primaryStage) {
        primaryStage.setTitle("Course Information Form");

        // Creating form elements
        Label courseNameLabel = new Label("Course Name:");
        TextField courseNameField = new TextField();
        Label scheduleLabel = new Label("Schedule:");
        TextField scheduleField = new TextField();
        Label instructorLabel = new Label("Instructor:");
        TextField instructorField = new TextField();
        Button submitButton = new Button("Submit");
        Button backButton = new Button("Back");

        // Styling form elements
        courseNameLabel.setStyle("-fx-font-weight: bold;");
        scheduleLabel.setStyle("-fx-font-weight: bold;");
        instructorLabel.setStyle("-fx-font-weight: bold;");
        submitButton.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-weight: bold;");

        // Creating layout
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: #f0f0f0;");

        // Adding elements to layout
        grid.add(courseNameLabel, 0, 0);
        grid.add(courseNameField, 1, 0);
        grid.add(scheduleLabel, 0, 1);
        grid.add(scheduleField, 1, 1);
        grid.add(instructorLabel, 0, 2);
        grid.add(instructorField, 1, 2);
        grid.add(backButton, 0, 3);
        grid.add(submitButton, 1, 3);

        // Submit button action
        submitButton.setOnAction(event -> {
            String courseName = courseNameField.getText();
            String schedule = scheduleField.getText();
            String instructor = instructorField.getText();
            // Validate fields
            if (courseName.isEmpty() || schedule.isEmpty() || instructor.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all fields.");
                alert.showAndWait();
            } else {
                // Insert into database
                insertIntoDatabase(courseName, schedule, instructor, userId); // Pass userId to insert method
                // Clear fields after successful submission
                courseNameField.clear();
                scheduleField.clear();
                instructorField.clear();
            }
        });

        // Back button action
        backButton.setOnAction(event -> {
            primaryStage.setScene(previousScene);
        });

        // Setting the scene size
        scene = new Scene(grid, 600, 400);
    }

    private void insertIntoDatabase(String courseName, String schedule, String instructor, int userId) { // Accept userId parameter
        String connectionString = "jdbc:mysql://localhost:3306/StudySync";
        String username = "root";
        String password = "root123@";
        String insertQuery = "INSERT INTO Courses (user_id, course_name, schedule, instructor_name) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(connectionString, username, password);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setInt(1, userId); // Set userId
            stmt.setString(2, courseName);
            stmt.setString(3, schedule);
            stmt.setString(4, instructor);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Data inserted successfully.");
            } else {
                System.out.println("Failed to insert data.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Scene getScene() {
        return scene;
    }
}
