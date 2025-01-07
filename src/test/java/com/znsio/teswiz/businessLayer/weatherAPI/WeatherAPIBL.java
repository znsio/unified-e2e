package com.znsio.teswiz.businessLayer.weatherAPI;

import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.services.UnirestService;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class WeatherAPIBL {
    private static final Logger LOGGER = LogManager.getLogger(WeatherAPIBL.class.getName());
    private final Map<String, Object> testData = Runner.getTestDataAsMap("Weather_API");
    private final String base_URL = testData.get("url").toString();

    public JSONObject getCurrentWeatherJSON() {
        LOGGER.info("Getting current weather data for given location coordinates");
        HashMap<String, Object> queryString = new HashMap<>() {{
            put("latitude", testData.get("latitude").toString());
            put("longitude", testData.get("longitude").toString());
            put("current_weather", true);
        }};
        HttpResponse<JsonNode> jsonResponse = UnirestService.getHttpResponseWithQueryMap(base_URL, queryString);
        assertThat(jsonResponse.getStatus()).as("API status code incorrect!")
                .isEqualTo(200);
        return jsonResponse.getBody().getObject().getJSONObject("current_weather");
    }

    public WeatherAPIBL verifyCurrentTemperature(JSONObject jsonResponse, int lowerLimit, int upperLimit) {
        LOGGER.info("Verifying weather is in range " + lowerLimit + " and " + upperLimit + " C");
        assertThat(((int) jsonResponse.getDouble("temperature"))).as("Temperature value incorrect!")
                .isBetween(lowerLimit, upperLimit);
        return this;
    }

    public JSONObject getForecastForInvalidDays() {
        LOGGER.info("Getting temperature forecast");
        HashMap<String, Object> queryString = new HashMap<>() {{
            put("latitude", testData.get("latitude").toString());
            put("longitude", testData.get("longitude").toString());
            put("hourly", testData.get("hourly").toString());
            put("forecast_days", testData.get("days").toString());
        }};
        HttpResponse<JsonNode> jsonResponse = UnirestService.getHttpResponseWithQueryMap(base_URL, queryString);
        assertThat(jsonResponse.getStatus()).as("API status code incorrect!")
                .isEqualTo(400);
        return jsonResponse.getBody().getObject();
    }

    public WeatherAPIBL verifyErrorForInvalidForecastDays(JSONObject jsonObject, String errorMessage) {
        String jsonError = jsonObject.get("reason").toString();
        LOGGER.info("Verifying error message for invalid forecast days");
        assertThat(jsonError.contains(errorMessage))
                .as("Incorrect error message!").isTrue();
        return this;
    }

    public JSONObject getCurrentWeatherJSON(String latitude, String longitude) {
        LOGGER.info("Getting current weather data for given location coordinates");
        HashMap<String, Object> queryString = new HashMap<>() {{
            put("latitude", latitude);
            put("longitude", longitude);
            put("current_weather", true);
        }};
        HttpResponse<JsonNode> jsonResponse = UnirestService.getHttpResponseWithQueryMap(base_URL, queryString);
        assertThat(jsonResponse.getStatus()).as("API status code incorrect!")
                .isEqualTo(200);
        return jsonResponse.getBody().getObject().getJSONObject("current_weather");
    }

    public WeatherAPIBL verifyCurrentWindSpeed(JSONObject jsonResponse, int lowerLimit, int upperLimit) {
        LOGGER.info("Verifying wind speed is in range " + lowerLimit + " and " + upperLimit);
        assertThat(((int) jsonResponse.getDouble("windspeed"))).as("Wind speed value incorrect!")
                .isBetween(lowerLimit, upperLimit);
        return this;
    }

    public JSONObject getLocationCoordinatesFor(String city) {
        LOGGER.info("Getting coordinates for city " + city);
        String geocode_url = testData.get("geocode_url").toString();
        HttpResponse<JsonNode> jsonResponse = UnirestService.getHttpResponseWithQueryParameter(geocode_url, "q", city);
        assertThat(jsonResponse.getStatus()).as("API status code incorrect!")
                .isEqualTo(200);
        return jsonResponse.getBody().getArray().getJSONObject(0);
    }

    public String getLatitudeFromJSON(JSONObject jsonObject) {
        LOGGER.info("Fetch latitude value from json response");
        assertThat(jsonObject.has("lat"))
                .as("Latitude in not available in JSON response!")
                .isTrue();
        return jsonObject.getString("lat");
    }

    public String getLongitudeFromJSON(JSONObject jsonObject) {
        LOGGER.info("Fetch longitude value from json response");
        assertThat(jsonObject.has("lon"))
                .as("Longitude in not available in JSON response!")
                .isTrue();
        return jsonObject.getString("lon");
    }

    public WeatherAPIBL verifyCurrentWindDirection(JSONObject jsonObject, int maxWindDirection) {
        LOGGER.info("Verifying maximum wind direction is less than: " + maxWindDirection + " from response: " + jsonObject);
        assertThat((Integer) jsonObject.get("winddirection"))
                .as("Wind direction above maximum limit!").isLessThan(maxWindDirection);
        return this;
    }
}
