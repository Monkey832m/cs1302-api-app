package cs1302.api;

/**
 * Represents a result in a response from AccuWeather's Text Search Location API.
 */
public class LocationApiResult {
    // For some reason, AccuWeather capitalizes their variable names.
    String Key;
    String LocalizedName;
}