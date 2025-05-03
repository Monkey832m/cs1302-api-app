package cs1302.api;

/**
 * Represents a response from AccuWeather's Current Conditions API.
 */
public class ConditionsApiResult {
    String weatherText;
    int weatherIcon;
    boolean isDayTime;
    TemperatureObj temperature;
    TemperatureObj realFeelTemperature;
    int relativeHumidity;
    WindObj wind;
    int uVIndex;
}