package com.znsio.teswiz.steps;

import com.znsio.teswiz.businessLayer.calculator.CalculatorBL;
import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.znsio.teswiz.tools.Wait.waitFor;

public class CalculatorSteps {
    private static final Logger LOGGER = LogManager.getLogger(CalculatorSteps.class.getName());
    private final TestExecutionContext context;

    public CalculatorSteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        LOGGER.info("context: " + context.getTestName());
    }

    @Given("I select {string}")
    public void iSelect(String number) {
        new CalculatorBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).selectNumber(number);
    }

    @Given("I select {string} in the new calculator")
    public void iSelectInTheNewCalculator(String number) {
        new CalculatorBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).selectNumberInNewCalculator(number);
    }

    @And("{string} select {string}")
    public void select(String userPersona, String action) {
        Platform onPlatform = Runner.getPlatformForUser(userPersona);
        new CalculatorBL(userPersona, onPlatform).startCalculator().selectNumber(action);
    }

    @When("I press {string}")
    public void iPress(String operation) {
        new CalculatorBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).pressOperation(operation);
    }

    @When("I press {string} in the new calculator")
    public void iPressInTheNewCalculator(String operation) {
        new CalculatorBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).pressOperationInNewCalculator(operation);
    }

    @Then("I should see {string}")
    public void iShouldSee(String finalResult) {
    }

    @Given("I start the calculator")
    public void iStartTheCalculator() {
        LOGGER.info(
                System.out.printf("iStartTheCalculator - Persona:'%s'", SAMPLE_TEST_CONTEXT.ME));
        Drivers.createDriverFor(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform(), context);
        new CalculatorBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).startCalculator();
    }

    @Given("I start the new calculator")
    public void iStartTheNewCalculator() {
        LOGGER.info(
                System.out.printf("iStartTheCalculator - Persona:'%s'", SAMPLE_TEST_CONTEXT.ME));
        Drivers.createDriverFor(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform(), context);
        new CalculatorBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).startNewCalculator();
    }

    @And("{string} press {string}")
    public void press(String userPersona, String action) {
        Platform onPlatform = Runner.getPlatformForUser(userPersona);
        new CalculatorBL(userPersona, onPlatform).pressOperation(action);
        if (action.equalsIgnoreCase("equals")) {
            waitFor(10);
        }
    }

    @Then("I switch back to the calculator")
    public void iSwitchBackToTheCalculator() {
        new CalculatorBL().switchBackToCalculator();
    }
}
