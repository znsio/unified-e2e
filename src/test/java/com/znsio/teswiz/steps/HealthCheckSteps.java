package com.znsio.teswiz.steps;

import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.runner.Runner;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HealthCheckSteps {
    private static final Logger LOGGER = LogManager.getLogger(AutoScrollSteps.class.getName());
    private final TestExecutionContext context;

    public HealthCheckSteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        LOGGER.info("context: " + context.getTestName());
    }

    @Given("I print my username")
    public void iPrintMyUsername() {
        LOGGER.info("My username: " + Runner.USER_NAME);
    }

    @Then("I see my current directory")
    public void iSeeMyCurrentDirectory() {
        LOGGER.info("My directory: " + Runner.USER_DIRECTORY);
    }

    @Given("I print the current platform")
    public void iPrintTheCurrentPlatform() {
        LOGGER.info("My platform: " + Runner.getPlatform().name());
    }
}
