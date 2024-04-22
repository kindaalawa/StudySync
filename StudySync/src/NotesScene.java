import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date; // Import Date class
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javafx.scene.control.TableCell;

public class NotesScene {

    private Stage primaryStage;
    private Scene previousScene;
    private Scene scene;
    private String userId; // User ID passed from login page

    // JDBC connection variables
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/StudySync";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root123@";

    public NotesScene(Stage primaryStage, Scene previousScene, String userId) {
        this.primaryStage = primaryStage;
        this.previousScene = previousScene;
        this.userId = userId; // Assign the provided user ID
        this.scene = createScene();
    }

    public Scene getScene() {
        return scene;
    }

    private Scene createScene() {
        // Table to display notes
        TableView<Note> notesTable = new TableView<>();

        // Columns for the table
        TableColumn<Note, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Note, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Note, Date> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Set default cell value for date column
        dateCol.setCellFactory(column -> new TableCell<Note, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(" "); // Show nothing for null values
                } else {
                    setText(item.toString()); // Display date
                }
            }
        });

        // Fetch notes and populate the table
        fetchNotes(notesTable, userId);

        // Add columns to the table
        notesTable.getColumns().addAll(titleCol, typeCol, dateCol);

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(previousScene));

        // Layout
        VBox layout = new VBox(10);
        layout.getChildren().addAll(notesTable, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        // Create scene with the layout
        Scene scene = new Scene(layout, 600, 400);
        return scene;
    }

    private void fetchNotes(TableView<Note> notesTable, String userId) {
        try {
            // Establishing a database connection
            Connection connection = DriverManager.getConnection(JDBC_URL, DB_USERNAME, DB_PASSWORD);

            // SQL statement to select notes
            String sql = "SELECT title, info_type, deadline FROM Info WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, userId);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Create a list to hold notes
            List<Note> notes = new ArrayList<>();

            // Populate the list with data
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String type = resultSet.getString("info_type");
                Date date = resultSet.getDate("deadline");
                notes.add(new Note(title, type, date));
            }

            // Sort notes by date in ascending order
            Collections.sort(notes, Comparator.comparing(Note::getDate));

            // Populate the table with sorted notes
            notesTable.getItems().addAll(notes);

            // Close the JDBC resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Define the Note class with appropriate attributes
    public static class Note {
        private String title;
        private String type;
        private Date date;

        public Note(String title, String type, Date date) {
            this.title = title;
            this.type = type;
            this.date = date;
        }

        public String getTitle() {
            return title;
        }

        public String getType() {
            return type;
        }

        public Date getDate() {
            return date;
        }
    }
}
