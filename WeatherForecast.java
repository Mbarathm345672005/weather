package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WeatherForecast extends Application {
    private static final String API_KEY = "8bb92dec3081493fe9eb79abab6f79db";
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TextField cityInput = new TextField();
        cityInput.setPromptText("Enter City Name");
        Button getWeatherButton = new Button("Get Weather");

        getWeatherButton.setOnAction(e -> {
            try {
                String cityName = cityInput.getText();
                String apiUrl = String.format(API_URL, cityName, API_KEY);
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                connection.disconnect();

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);
                String weatherDescription = jsonObject.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("description").getAsString();
                JsonObject main = jsonObject.getAsJsonObject("main");
                double temperature = main.get("temp").getAsDouble();
                int humidity = main.get("humidity").getAsInt();
                JsonObject wind = jsonObject.getAsJsonObject("wind");
                double windSpeed = wind.get("speed").getAsDouble();

                String htmlContent = "<html><head><title>Weather Forecast</title></head><body>"
                        + "<h1>Weather Forecast for " + cityName + "</h1>"
                        + "<p>Description: " + weatherDescription + "</p>"
                        + "<p>Temperature: " + temperature + " K</p>"
                        + "<p>Humidity: " + humidity + "%</p>"
                        + "<p>Wind Speed: " + windSpeed + " m/s</p>"
                        + "</body></html>";

                try (BufferedWriter writer = new BufferedWriter(new FileWriter("weather.html"))) {
                    writer.write(htmlContent);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                Label label = new Label("Weather data saved to weather.html");
                StackPane root = new StackPane();
                root.getChildren().add(label);
                Scene scene = new Scene(root, 300, 200);
                primaryStage.setTitle("Weather Forecast");
                primaryStage.setScene(scene);
                primaryStage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(cityInput, getWeatherButton);
        Scene inputScene = new Scene(layout, 300, 100);
        primaryStage.setTitle("Weather Forecast");
        primaryStage.setScene(inputScene);
        primaryStage.show();
    }
}
