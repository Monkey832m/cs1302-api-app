package cs1302.api;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Random;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Creates a new instance of Weather Music, a weather app that also gives you song recommendations.
 */
public class ApiApp extends Application {
    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)           // uses HTTP protocol version 2 where possible
        .followRedirects(HttpClient.Redirect.NORMAL)  // always redirects, except from HTTPS to HTTP
        .build();                                     // builds and returns a HttpClient object

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson accuWeatherGSON = new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE) // fix variable names
        .setPrettyPrinting()                          // enable nice output when printing
        .create();                                    // builds and returns a Gson object
        
    public static Gson iTunesGSON = new GsonBuilder()
        .setPrettyPrinting()                          // enable nice output when printing
        .create();                                    // builds and returns a Gson object

    Stage stage;
    Scene scene;
    VBox root;
    
    HBox top;
    Label locationSearchLabel;
    TextField locationQuery;
    Button getLocation;
    
    StackPane weatherStack;
    
    Rectangle backgroundImg;
    
    HBox uiElements;
    
    VBox conditions;
    ImageView icon;
    Label locationName;
    Label currentConditions;
    Label temperature;
    Label feelsLike;
    
    VBox rightContainer;
    
    VBox music;
    Label songListHeader;
    Label[] songs = new Label[5];
    
    VBox xtraWeather;
    Label windSpeed;
    Label uvIndex;
    Label humidity;

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        root = new VBox();
    } // ApiApp
    
    /** {@inheritDoc} */
    @Override
    public void init() {
        // Base settings
        this.root.setPadding(Insets.EMPTY);
        this.root.setSpacing(5);

        // Top bar UI
        this.top = new HBox(3);
        this.top.setAlignment(Pos.CENTER_LEFT);
        this.locationSearchLabel = new Label("Location: ");
        this.locationQuery = new TextField("Athens, GA");
        this.getLocation = new Button("Search");
        this.getLocation.setOnAction(e -> getWeather());
        this.top.getChildren().addAll(locationSearchLabel, locationQuery, getLocation);

        // Image/StackPane UI
        this.weatherStack = new StackPane();
        this.backgroundImg = new Rectangle(0, 0, 600, 300);
        this.backgroundImg.setFill(Color.SKYBLUE);
        this.uiElements = new HBox(2);
        
        this.createWeatherUi();

        // Add all to root when finished
        this.conditions.getChildren().addAll(icon, currentConditions, temperature, locationName,
            feelsLike);
        this.music.getChildren().addAll(songListHeader);
        for (int i = 0; i < 5; i++) {
            this.music.getChildren().addAll(songs[i]);
        }
        this.xtraWeather.getChildren().addAll(this.windSpeed, this.uvIndex, this.humidity);
        this.rightContainer.getChildren().addAll(this.music, this.xtraWeather);
        this.uiElements.getChildren().addAll(this.conditions, this.rightContainer);
        this.weatherStack.getChildren().addAll(this.backgroundImg, this.uiElements);
        this.root.getChildren().addAll(this.top, this.weatherStack);

        System.out.println("init() called");
    } // init
    
    /**
     * Initializes more complex UI components. To be called in init().
     */
    public void createWeatherUi() {
        this.conditions = new VBox(5);
        this.conditions.setPrefWidth(200);
        this.conditions.setAlignment(Pos.TOP_CENTER);
        this.icon = new ImageView(new Image("file:resources/1-s.png"));
        this.icon.setFitHeight(150);
        this.icon.setPreserveRatio(true);
        this.currentConditions = new Label("Sunny");
        this.currentConditions.setTextFill(Color.WHITE);
        this.currentConditions.setFont(new Font(28));
        this.temperature = new Label("");
        this.temperature.setTextFill(Color.WHITE);
        this.temperature.setFont(new Font(32));
        this.locationName = new Label("Location");
        this.locationName.setTextFill(Color.WHITE);  
        this.locationName.setFont(new Font(20));      
        this.feelsLike = new Label("Feels Like: ");
        this.feelsLike.setTextFill(Color.WHITE);
        
        this.rightContainer = new VBox(2);
        
        this.music = new VBox(6);
        this.music.setPrefHeight(180);
        this.songListHeader = new Label("\n\nSongs:");
        this.songListHeader.setTextFill(Color.WHITE);
        for (int i = 0; i < 5; i++) {
            this.songs[i] = new Label("P");
            this.songs[i].setTextFill(Color.WHITE);
        }
        this.xtraWeather = new VBox(4);
        this.windSpeed = new Label(" mph ");
        this.windSpeed.setTextFill(Color.WHITE);
        this.uvIndex = new Label("UV Index: ");
        this.uvIndex.setTextFill(Color.WHITE);
        this.humidity = new Label("Humidity: ");
        this.humidity.setTextFill(Color.WHITE);  
    }

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        this.stage = stage;
        stage.resizableProperty().setValue(Boolean.FALSE);

        // demonstrate how to load local asset using "file:resources/"
        // Image bannerImage = new Image("file:resources/readme-banner.png");
        // ImageView banner = new ImageView(bannerImage);
        // banner.setPreserveRatio(true);
        // banner.setFitWidth(640);

        scene = new Scene(root);

        // setup stage
        stage.setTitle("Weather Music");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();

    } // start
    
    /**
     * Calls other methods to search the AccuWeather APIs required to display the current weather.
     */
    public void getWeather() {
        Platform.runLater(() -> {
            this.getLocation.setDisable(true);
        });
        String location = this.locationQuery.getText().trim();
        // New thread
        new Thread(() -> {
            searchAccuWeatherApi(this.conditionApiUrl(this.getLocationKey(location)));
        }).start();
    }
    
    /**
     * Gets a location key from AccuWeather's Location Text Search API, whi is then passed along to
     * the conditionApiUrl method in order to form a URL for the Current Conditions API.
     * @param location - the location being searched
     * @return String URL
     */
    public String getLocationKey(String location) {
        String locationKey = "";
        // DEBUG
        // System.out.println("Location: " + location + ", Getting location key...");
        // WARNING: ONLY 50 API CALLS CAN BE MADE PER KEY PER DAY. SECOND KEY PROVIDED
        // Monkey832 API Key: "?apikey=k7lOavGLUcon5QNdHIImdpYwAOUCvlCn&q="
        // Secondary API Key: "?apikey=XhZR6WGvI2FZBeT6XwyrYTm3UouswjDC&q="
        try {
            String searchTerm = URLEncoder.encode(location, StandardCharsets.UTF_8);
            String locationUrl = "http://dataservice.accuweather.com/locations/v1/search"
                + "?apikey=k7lOavGLUcon5QNdHIImdpYwAOUCvlCn&q=" + searchTerm + "&details=true";
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI(locationUrl)).GET().build();
            HttpResponse<String> httpResponse = HTTP_CLIENT
                .send(httpRequest, HttpResponse.BodyHandlers.ofString());
            // DEBUG
            // System.out.println(httpResponse.body());
            LocationApiResult[] apiFeed = this.accuWeatherGSON.fromJson(httpResponse.body(), 
                LocationApiResult[].class);
            locationKey = apiFeed[0].key;
            Platform.runLater(() -> {
                this.locationName.setText(apiFeed[0].localizedName);
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                alertUser(e.toString());
                getLocation.setDisable(false);
            });
        }
        return locationKey;
    }
    
    /**
     * Formats a Current Conditions API URL search and returns it.
     * @param locationKey - the locationKey returned by the Text Search API
     * @return String URL
     */
    public String conditionApiUrl(String locationKey) {
        // Monkey832 API Key: "?apikey=k7lOavGLUcon5QNdHIImdpYwAOUCvlCn&details=true";
        // Secondary API Key: "?apikey=XhZR6WGvI2FZBeT6XwyrYTm3UouswjDC&details=true";
        // DEBUG
        // System.out.println("Location key: " + locationKey + ", Creating Conditions API URL...");
        return "http://dataservice.accuweather.com/currentconditions/v1/" + locationKey
            + "?apikey=k7lOavGLUcon5QNdHIImdpYwAOUCvlCn&details=true";
    }
    
    /**
     * Searches the AccuWeather Current Conditions API.
     * @param conditionsUrl - the AccuWeather Current Conditions API URL
     */
    public void searchAccuWeatherApi(String conditionsUrl) {
        // DEBUG
        // System.out.println("Made it to searchAccuWeatherApi successfully.");
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI(conditionsUrl)).GET().build();
            HttpResponse<String> httpResponse = HTTP_CLIENT
                .send(httpRequest, HttpResponse.BodyHandlers.ofString());
            ConditionsApiResult[] apiFeed = this.accuWeatherGSON.fromJson(httpResponse.body(),
                ConditionsApiResult[].class);
            // DEBUG
            // System.out.println("WeatherText: " + apiFeed[0].WeatherText);
            // System.out.println("WeatherIcon: " + apiFeed[0].WeatherIcon);
            // System.out.println("IsDayTime: " + apiFeed[0].IsDayTime);
            // System.out.println("Temperature: " + apiFeed[0].Temperature.Imperial.Value);
            // System.out.println("RealFeel: " + apiFeed[0].RealFeelTemperature.Imperial.Value);
            // System.out.println("Humidity: " + apiFeed[0].RelativeHumidity);
            // System.out.println("UV Index: " + apiFeed[0].UVIndex);
            Platform.runLater(() -> {
                getLocation.setDisable(false);
                this.icon.setImage(new Image("file:resources/" + apiFeed[0].weatherIcon + 
                    "-s.png"));
                this.currentConditions.setText(apiFeed[0].weatherText);
                if (!apiFeed[0].isDayTime) {
                    this.backgroundImg.setFill(Color.BLACK);
                } else if (apiFeed[0].weatherIcon > 6 && apiFeed[0].weatherIcon < 30) {
                    this.backgroundImg.setFill(Color.SLATEGREY);
                } else {
                    this.backgroundImg.setFill(Color.SKYBLUE);
                }
                this.temperature.setText(apiFeed[0].temperature.imperial.value + "\u00B0" + "F");
                this.feelsLike.setText("Feels Like: " + 
                    apiFeed[0].realFeelTemperature.imperial.value + "\u00B0" + "F");
                this.windSpeed.setText("Wind: " + apiFeed[0].wind.speed.imperial.value + " mph " +
                    apiFeed[0].wind.direction.localized);
                this.uvIndex.setText("UV Index: " + apiFeed[0].uVIndex);
                this.humidity.setText("Humidity: " + apiFeed[0].relativeHumidity + "%");
            });
            new Thread(() -> searchItunesApi(this.iTunesApiUrl(apiFeed[0].weatherText))).start();
        } catch (Exception e) {
            Platform.runLater(() -> {
                alertUser(e.toString());
                getLocation.setDisable(false);
            });
        }
    }
    
    /**
     * Formats an iTunes API URL search and returns it.
     * @param term - the unencoded search term (the current weather conditions)
     * @return String URL
     */
    public String iTunesApiUrl(String term) {
        String searchTerm = URLEncoder.encode(term.trim(), StandardCharsets.UTF_8);
        return "https://itunes.apple.com/search?term=" + searchTerm + "&media=music&limit=5";
    }
    
    /**
     * Searches the iTunes API.
     * @param url - the iTunes API URL
     */
    public void searchItunesApi(String url) {
        // DEBUG
        // System.out.println("iTunes API URL: " + url);
        try {
            Platform.runLater(() -> {
                for (int i = 0; i < songs.length; i++) {
                    if (songs[i].getText() != "") {
                        songs[i].setText("");
                    }
                }
            });
            List<String> songSet = new ArrayList<>();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI(url)).GET().build();
            HttpResponse<String> httpResponse = HTTP_CLIENT
                .send(httpRequest, HttpResponse.BodyHandlers.ofString());
            // DEBUG
            // System.out.println(httpResponse.body());
            ItunesResponse apiFeed = this.iTunesGSON.fromJson(httpResponse.body(), 
                ItunesResponse.class);
            // DEBUG
            // System.out.println("API Feed Result Count: " + apiFeed.resultCount);
            // System.out.println("iTunes Results Size: " + apiFeed.results.length);
            for (ItunesResult result : apiFeed.results) {
                if (result.artistName != null && result.trackName != null) {
                    songSet.add(result.artistName + " - " + result.trackName);
                }
                // DEBUG
                // System.out.println("Song: " + result.artistName + " - " + result.trackName);
            }
            // DEBUG
            // System.out.println(songSet);
            Platform.runLater(() -> {
                for (int i = 0; i < 5; i++) {
                    if (songSet.size() > i) {
                        songs[i].setText(songSet.get(i));
                    }
                }
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                alertUser(url + "\n\n" + e.toString());
                getLocation.setDisable(false);
            });
        }
    }
    
    /**
     * Creates and displays an error alert to the user.
     * @param errorMessage - the error message to be displayed in the alert
     */
    public void alertUser(String errorMessage) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(errorMessage);
            alert.showAndWait();
        });
    }

} // ApiApp
