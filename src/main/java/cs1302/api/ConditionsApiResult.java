package cs1302.api;

/**
 * Represents a response from AccuWeather's Current Conditions API.
 */
public class ConditionsApiResult {
    String WeatherText;
    int WeatherIcon;
    boolean IsDayTime;
    TemperatureObj Temperature;
    TemperatureObj RealFeelTemperature;
    int RelativeHumidity;
    WindObj Wind;
    int UVIndex;
}