package com.znsio.teswiz.steps;

import com.context.SessionContext;
import com.context.TestExecutionContext;
import com.znsio.teswiz.businessLayer.weatherAPI.WeatherAPIBL;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONObject;
import org.apache.log4j.Logger;

public class WeatherAPISteps {
    private JSONObject jsonObject;
    private static final Logger LOGGER = Logger.getLogger(WeatherAPISteps.class.getName());
    private final TestExecutionContext context;

    public WeatherAPISteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        LOGGER.info("context: " + context.getTestName());
    }

    @Given("I send GET request with location coordinates")
    public void getRequestIsSentToTheWeatherAPIWithValidLatitudeAndLongitude() {
        jsonObject = new WeatherAPIBL().getCurrentWeatherJSON();
    }

    @Then("temperature of that location should be in range {int} and {int} C")
    public void weVerifyWeatherOfThatLocationInResponse(int lowerLimit, int upperLimit) {
        new WeatherAPIBL().verifyCurrentTemperature(jsonObject, lowerLimit, upperLimit);
    }

    @Given("I send GET request with location coordinates and invalid forecast days")
    public void iSendGETRequestWithValidLocationCoordinatesAndForecastDays() {
        jsonObject = new WeatherAPIBL().getForecastForInvalidDays();
    }

    @Then("error message should be {string}")
    public void iVerifyErrorReason(String errorMessage) {
        new WeatherAPIBL().verifyErrorForInvalidForecastDays(jsonObject, errorMessage);
    }

    @Given("I send GET request with valid location coordinates {string} and {string}")
    public void iSendGETRequestWithValidLocationCoordinatesLatitudeAndLongitude(String latitude, String longitude) {
        jsonObject = new WeatherAPIBL().getCurrentWeatherJSON(latitude, longitude);
    }

    @Then("wind speed of that location should be in range {int} and {int} kmph")
    public void iVerifyWindSpeedOfThatLocationInRangeAndKmph(int lowerLimit, int upperLimit) {
        new WeatherAPIBL().verifyCurrentWindSpeed(jsonObject, lowerLimit, upperLimit);
    }
}
