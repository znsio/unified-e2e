package com.znsio.teswiz.businessLayer.weatherAPI;

import com.znsio.teswiz.runner.Runner;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.apache.log4j.Logger;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

public class WeatherAPIBL {
    private static final Logger LOGGER = Logger.getLogger(WeatherAPIBL.class.getName());
    private final Map<String, Object> testData = Runner.getTestDataAsMap("Weather_API");
    private final String base_URL = testData.get("url").toString();

    public JSONObject getCurrentWeatherJSON() {
        LOGGER.info("Getting current weather data for given location coordinates");
        String latitude = testData.get("latitude").toString();
        String longitude = testData.get("longitude").toString();
        HttpResponse<JsonNode> jsonResponse
                = Unirest.get(base_URL)
                .header("accept", "application/json")
                .queryString("latitude", latitude)
                .queryString("longitude",longitude)
                .queryString("current_weather",true)
                .asJson();

        assertThat(jsonResponse.getStatus()).as("API status code incorrect!")
                .isEqualTo(200);
        return jsonResponse.getBody().getObject().getJSONObject("current_weather");
    }

    public void verifyCurrentTemperature(JSONObject jsonResponse, int lowerLimit, int upperLimit) {
        LOGGER.info("Verifying weather is in range "+lowerLimit+" and "+upperLimit+" C");
        assertThat(((int) jsonResponse.getDouble("temperature"))).as("Temperature value incorrect!")
                .isBetween(lowerLimit,upperLimit);
    }

    public JSONObject getForecastForInvalidDays() {
        String days = testData.get("days").toString();
        String latitude = testData.get("latitude").toString();
        String longitude = testData.get("longitude").toString();
        String hourly = testData.get("hourly").toString();
        LOGGER.info("Getting temperature forecast for days: "+days);
        HttpResponse<JsonNode> jsonResponse
                = Unirest.get(base_URL)
                .header("accept", "application/json")
                .queryString("latitude", latitude)
                .queryString("longitude",longitude)
                .queryString("hourly",hourly)
                .queryString("forecast_days",days)
                .asJson();

        assertThat(jsonResponse.getStatus()).as("API status code incorrect!")
                .isEqualTo(400);
        return jsonResponse.getBody().getObject();
    }

    public void verifyErrorForInvalidForecastDays(JSONObject jsonObject, String errorMessage) {
        String jsonError = jsonObject.get("reason").toString();
        LOGGER.info("Verifying error message for invalid forecast days");
        assertThat(jsonError.contains(errorMessage))
                .as("Incorrect error message!").isTrue();
    }

    public JSONObject getCurrentWeatherJSON(String latitude, String longitude) {
        LOGGER.info("Getting current weather data for given location coordinates");
        HttpResponse<JsonNode> jsonResponse
                = Unirest.get(base_URL)
                .header("accept", "application/json")
                .queryString("latitude", latitude)
                .queryString("longitude",longitude)
                .queryString("current_weather",true)
                .asJson();

        assertThat(jsonResponse.getStatus()).as("API status code incorrect!")
                .isEqualTo(200);
        return jsonResponse.getBody().getObject().getJSONObject("current_weather");
    }

    public void verifyCurrentWindSpeed(JSONObject jsonResponse, int lowerLimit, int upperLimit) {
        LOGGER.info("Verifying wind speed is in range "+lowerLimit+" and "+upperLimit);
        assertThat(((int) jsonResponse.getDouble("windspeed"))).as("Wind speed value incorrect!")
                .isBetween(lowerLimit,upperLimit);
    }
}
