package com.znsio.teswiz.steps;

import com.znsio.teswiz.businessLayer.restUser.JsonPlaceHolderBL;
import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import kong.unirest.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonPlaceHolderSteps {
    private static final Logger LOGGER = LogManager.getLogger(WeatherAPISteps.class.getName());
    private final TestExecutionContext context;
    private JSONObject jsonObject;
    private int statusCode;

    public JsonPlaceHolderSteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        LOGGER.info("context: " + context.getTestName());
    }

    @Given("I create a new post")
    public void iCreateAPost() {
        new JsonPlaceHolderBL().createPost();
    }

    @And("I modify title of the created post")
    public void iUpdateTitleOfThePost() {
        jsonObject = new JsonPlaceHolderBL().updatePost();
    }

    @Then("the title of the post should be updated")
    public void verifyTitleUpdated() {
        new JsonPlaceHolderBL().verifyPostUpdatedSuccessfully(jsonObject);

    }

    @When("I delete the modified post")
    public void iDeleteThePost() {
        statusCode = new JsonPlaceHolderBL().deletePost();
    }


    @Then("the post should be deleted successfully")
    public void thePostShouldBeDeletedSuccessfully() {
        new JsonPlaceHolderBL().verifyIfPostDeleted(statusCode);
    }
}
