import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        String connectionString = "jdbc:mysql://localhost:3306/StudySync";
        String username = "root";
        String password = "root123@";
        
        try (Connection connection = DriverManager.getConnection(connectionString, username, password)) {
            // Connection successful
            System.out.println("Connected to the database.");
            
        } catch (SQLException e) {
            // Connection failed or query execution failed
            System.out.println("Failed to connect to the database or execute query.");
            e.printStackTrace();
        }
    }
}
