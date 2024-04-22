import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;

public class ScheduleScene {

    private Stage primaryStage;
    private Scene previousScene;
    private Scene scene;

    // JDBC connection variables
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/StudySync";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root123@";

    public ScheduleScene(Stage primaryStage, Scene previousScene) {
        this.primaryStage = primaryStage;
        this.previousScene = previousScene;
        this.scene = createScene();
    }

    public Scene getScene() {
        return scene;
    }

    private Scene createScene() {
        // Table to display schedule
        TableView<Course> scheduleTable = new TableView<>();

        // Columns for the table
        TableColumn<Course, String> courseNameCol = new TableColumn<>("Course Name");
        courseNameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        TableColumn<Course, String> scheduleCol = new TableColumn<>("Schedule");
        scheduleCol.setCellValueFactory(new PropertyValueFactory<>("schedule"));

        TableColumn<Course, String> instructorNameCol = new TableColumn<>("Instructor");
        instructorNameCol.setCellValueFactory(new PropertyValueFactory<>("instructorName"));

        // Delete button column
        TableColumn<Course, Void> deleteCol = new TableColumn<>("Action");
        deleteCol.setCellFactory(param -> {
            return new TableCell<Course, Void>() {
                private final Button deleteButton = new Button("Delete");

                {
                    deleteButton.setOnAction(event -> {
                        Course course = getTableView().getItems().get(getIndex());
                        deleteCourse(course, scheduleTable);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteButton);
                    }
                }
            };
        });

        scheduleTable.getColumns().addAll(courseNameCol, scheduleCol, instructorNameCol, deleteCol);

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(previousScene));

        // User ID input field
        Label userIdLabel = new Label("Enter User ID:");
        TextField userIdField = new TextField();
        userIdField.setPromptText("User ID");

        // Fetch button
        Button fetchButton = new Button("Fetch");
        fetchButton.setOnAction(e -> {
            scheduleTable.getItems().clear(); // Clear previous items
            fetchCourses(scheduleTable, userIdField.getText());
        });

        HBox inputBox = new HBox(10, userIdLabel, userIdField, fetchButton);
        inputBox.setAlignment(Pos.CENTER);

        // Layout
        VBox layout = new VBox(10);
        layout.getChildren().addAll(scheduleTable, inputBox, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        // Create scene with the layout
        Scene scene = new Scene(layout, 600, 400);
        return scene;
    }

    private void fetchCourses(TableView<Course> scheduleTable, String userId) {
        try {
            // Establishing a database connection
            Connection connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD);

            // SQL statement to select courses
            String sql = "SELECT course_id, course_name, schedule, instructor_name FROM Courses WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, userId);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Populate the table with data
            while (resultSet.next()) {
                int courseId = resultSet.getInt("course_id");
                String courseName = resultSet.getString("course_name");
                String schedule = resultSet.getString("schedule");
                String instructorName = resultSet.getString("instructor_name");

                scheduleTable.getItems().add(new Course(courseId, courseName, schedule, instructorName));
            }

            // Close the JDBC resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteCourse(Course course, TableView<Course> scheduleTable) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this course?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Connection connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD);

                // Delete related records from the 'Info' table
                String deleteInfoSQL = "DELETE FROM Info WHERE course_id = ?";
                PreparedStatement deleteInfoStatement = connection.prepareStatement(deleteInfoSQL);
                deleteInfoStatement.setInt(1, course.getCourseId());
                deleteInfoStatement.executeUpdate();
                deleteInfoStatement.close();

                // Now delete the course from the 'Courses' table
                String deleteCourseSQL = "DELETE FROM Courses WHERE course_id = ?";
                PreparedStatement deleteCourseStatement = connection.prepareStatement(deleteCourseSQL);
                deleteCourseStatement.setInt(1, course.getCourseId());
                deleteCourseStatement.executeUpdate();
                deleteCourseStatement.close();

                connection.close();

                // Inform the user
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Course deleted successfully!");
                successAlert.showAndWait();

                // Remove the course from the table
                scheduleTable.getItems().remove(course);
            } catch (SQLException ex) {
                ex.printStackTrace();
                // Handle any exceptions
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Failed to delete course!");
                errorAlert.showAndWait();
            }
        }
    }

    // Define the Course class with appropriate attributes
    public static class Course {
        private int courseId;
        private String courseName;
        private String schedule;
        private String instructorName;

        public Course(int courseId, String courseName, String schedule, String instructorName) {
            this.courseId = courseId;
            this.courseName = courseName;
            this.schedule = schedule;
            this.instructorName = instructorName;
        }

        public int getCourseId() {
            return courseId;
        }

        public String getCourseName() {
            return courseName;
        }

        public String getSchedule() {
            return schedule;
        }

        public String getInstructorName() {
            return instructorName;
        }
    }
}
