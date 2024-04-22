import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class MainApp extends Application {

    private static final String API_KEY = "78ecd541bee80959e735883829666780";
    private static final String WEATHER_API_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final String IPINFO_API_URL = "https://ipinfo.io/json";
    private static final String QUOTE_API_URL = "https://api.quotable.io/random";
    private int userId; // User ID obtained from login

    public MainApp() {

    }

    // Constructor to receive user_id
    public MainApp(int userId) {
        this.userId = userId;
    }

    @Override
    public void start(Stage primaryStage) {
        // Creating title label with text shadow effect
        Label titleLabel = new Label("WELCOME TO STUDY SYNC !");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 10, 0, 0, 0);");

        // Creating buttons for each action
        Button viewScheduleButton = createSectionButton("View Schedule");
        Button addCoursesButton = createSectionButton("Add Courses");
        Button addNotesButton = createSectionButton("Add Notes");
        Button showNotesButton = createSectionButton("Show Notes");
        Button paymentsButton = createSectionButton("Payments");

        Text weatherText = new Text("Weather: Loading...");
        weatherText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        weatherText.setFill(Color.BLACK);

        // Text for motivational quote
        Text quoteText = new Text("Quote: Loading...");
        quoteText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        quoteText.setFill(Color.BLUE);

        // Log Out button
        Button logOutButton = new Button("Log Out");
        logOutButton.setOnAction(e -> {
            // Navigate back to the login page
            LoginPage loginPage = new LoginPage();
            Stage loginStage = new Stage();
            loginPage.start(loginStage);
            primaryStage.close();
        });

        // Profile Management button
        Button profileButton = new Button("Profile");
        profileButton.setOnAction(e -> {
            // Show profile management dialog
            UserProfile userProfile = getUserProfile(userId);
            showProfileDialog(userProfile);
        });

        // Analytics Dashboard button
        Button analyticsButton = new Button("Analytics");
        analyticsButton.setOnAction(e -> {
            // Show analytics dashboard
            showAnalyticsDashboard();
        });

        // Handling button actions
        viewScheduleButton.setOnAction(e -> primaryStage.setScene(createScheduleScene(primaryStage)));
        addCoursesButton.setOnAction(e -> primaryStage.setScene(createCoursesScene(primaryStage, userId)));
        addNotesButton.setOnAction(e -> primaryStage.setScene(createAddNotesScene(primaryStage, userId)));
        showNotesButton.setOnAction(e -> primaryStage.setScene(createNotesScene(primaryStage, userId)));

        // Creating main layout
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));

        // Gradient background
        Stop[] stops = new Stop[]{
            new Stop(0, Color.web("#E0E0E0")), // Light Gray
            new Stop(1, Color.web("#808080")) // Dark Gray
        };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        mainLayout.setBackground(new Background(new BackgroundFill(gradient, null, null)));

        mainLayout.getChildren().addAll(
                titleLabel,
                viewScheduleButton,
                addCoursesButton,
                addNotesButton,
                showNotesButton,
                paymentsButton,
                profileButton,
                analyticsButton,
                weatherText,
                quoteText,
                logOutButton
        );

        // Creating scene
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("StudySync");
        primaryStage.show();

        // Fetching weather data
        fetchWeatherData(weatherText);

        // Fetching quote data
        fetchQuoteData(quoteText);
    }

    private Button createSectionButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font(18));
        button.setStyle("-fx-background-color: #696969; -fx-text-fill: white; -fx-padding: 10px 20px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-padding: 10px 20px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #696969; -fx-text-fill: white; -fx-padding: 10px 20px;"));
        return button;
    }

    private void fetchWeatherData(Text weatherText) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    // Fetch user's city using IP info
                    URL locationURL = new URL(IPINFO_API_URL);
                    HttpURLConnection locationConnection = (HttpURLConnection) locationURL.openConnection();
                    locationConnection.setRequestMethod("GET");
                    BufferedReader locationReader = new BufferedReader(new InputStreamReader(locationConnection.getInputStream()));
                    StringBuilder locationResponse = new StringBuilder();
                    String locationLine;
                    while ((locationLine = locationReader.readLine()) != null) {
                        locationResponse.append(locationLine);
                    }
                    locationReader.close();

                    JSONObject locationObject = new JSONObject(locationResponse.toString());
                    String city = locationObject.getString("city");

                    // Fetch weather data using city
                    URL weatherURL = new URL(WEATHER_API_URL + "?q=" + city + "&appid=" + API_KEY);
                    HttpURLConnection weatherConnection = (HttpURLConnection) weatherURL.openConnection();
                    weatherConnection.setRequestMethod("GET");
                    BufferedReader weatherReader = new BufferedReader(new InputStreamReader(weatherConnection.getInputStream()));
                    StringBuilder weatherResponse = new StringBuilder();
                    String weatherLine;
                    while ((weatherLine = weatherReader.readLine()) != null) {
                        weatherResponse.append(weatherLine);
                    }
                    weatherReader.close();

                    JSONObject weatherObject = new JSONObject(weatherResponse.toString());
                    String description = weatherObject.getJSONArray("weather").getJSONObject(0).getString("description");
                    double temperature = weatherObject.getJSONObject("main").getDouble("temp") - 273.15;
                    String weatherInfo = "Weather in " + city + ": " + description + ", Temperature: " + String.format("%.1f", temperature) + "°C";

                    Platform.runLater(() -> weatherText.setText(weatherInfo));
                } catch (IOException e) {                     Platform.runLater(() -> weatherText.setText("Failed to fetch weather data"));
                    e.printStackTrace();
                }
                   return null;
            }
        };

        new Thread(task).start();
    }

    private void fetchQuoteData(Text quoteText) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    URL quoteURL = new URL(QUOTE_API_URL);
                    HttpURLConnection connection = (HttpURLConnection) quoteURL.openConnection();
                    connection.setRequestMethod("GET");

                    StringBuilder response = new StringBuilder();
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                    }

                    JSONObject quoteObject = new JSONObject(response.toString());
                    String quote = quoteObject.getString("content");

                    Platform.runLater(() -> quoteText.setText("    " + quote));
                } catch (IOException e) {
                    Platform.runLater(() -> quoteText.setText("Failed to fetch quote data"));
                    e.printStackTrace();
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private Scene createScheduleScene(Stage primaryStage) {
        // Create Schedule scene and return it
        ScheduleScene scheduleScene = new ScheduleScene(primaryStage, primaryStage.getScene());
        return scheduleScene.getScene();
    }

    private Scene createCoursesScene(Stage primaryStage, int userId) {
        // Create Courses scene and return it
        CoursesScene coursesScene = new CoursesScene(primaryStage, primaryStage.getScene(), userId);
        return coursesScene.getScene();
    }

    private Scene createAddNotesScene(Stage primaryStage, int userId) {
        // Assuming previousScene is the Scene object you want to pass to AddNotesScene
        AddNotesScene addNotesScene = new AddNotesScene(primaryStage, primaryStage.getScene(), userId);
        return addNotesScene.getScene();
    }

    private Scene createNotesScene(Stage primaryStage, int userId) {
        NotesScene notesScene = new NotesScene(primaryStage, primaryStage.getScene(), String.valueOf(userId));
        return notesScene.getScene();
    }

    // Method to update user profile information
    private void updateUserProfile(int userId, String newEmail, String newPassword) {
        // Implement database update query to update email and password for the given user ID
    }

    // Method to fetch user profile information
    private UserProfile getUserProfile(int userId) {
        // Implement database query to retrieve user profile details based on the user ID
        // Return the UserProfile object containing user profile data
        return new UserProfile("John Doe", "john@example.com", new Date());
    }

    // Method to display profile management dialog
    private void showProfileDialog(UserProfile userProfile) {
        // Implement dialog to allow users to view and update their profile information
        // You can use JavaFX dialogs or custom dialogs for this purpose
    }

    // Method to collect and aggregate analytics data
    private AnalyticsData generateAnalyticsData() {
        // Implement logic to query the database and aggregate relevant analytics data
        // Return the AnalyticsData object containing aggregated analytics data
        return new AnalyticsData();
    }

    // Method to display the analytics dashboard
    private void showAnalyticsDashboard() {
        // Generate analytics data using generateAnalyticsData() method
        AnalyticsData analyticsData = generateAnalyticsData();
        
        // Implement UI components to display charts and graphs based on the analytics data
        // Show the analytics dashboard in a new scene or dialog
    }

    public static void main(String[] args) {
        launch(args);
    }

}    