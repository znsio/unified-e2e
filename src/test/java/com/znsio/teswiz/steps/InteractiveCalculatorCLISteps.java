package com.znsio.teswiz.steps;

import com.znsio.teswiz.businessLayer.interactiveCalculatorCLI.InteractiveCalculatorCLIBL;
import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InteractiveCalculatorCLISteps {
    private static final Logger LOGGER = LogManager.getLogger(InteractiveCalculatorCLISteps.class.getName());
    private final TestExecutionContext context;

    public InteractiveCalculatorCLISteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        LOGGER.info("context: " + context.getTestName());
    }

    @Given("I launch the interactive CLI for calculator")
    public void iLaunchTheInteractiveCLIForCalculator() {
        LOGGER.info("I launch the interactive CLI for calculator");
        new InteractiveCalculatorCLIBL().launchInteractiveCLIForCalculator();
    }

    @When("I add 2 numbers - {int} and {int}")
    public void iAddNumbersAnd(int number1, int number2) {
        LOGGER.info("I add 2 numbers - " + number1 + " and " + number2);
        new InteractiveCalculatorCLIBL().add(number1, number2);
    }

    @And("I subtract 2 numbers - {int} and {int}")
    public void iSubtractNumbersAnd(int number1, int number2) {
        LOGGER.info("I subtract 2 numbers - " + number1 + " and " + number2);
        new InteractiveCalculatorCLIBL().subtract(number1, number2);
    }

    @And("I see the invalid messages")
    public void iSeeTheInvalidMessages() {
        LOGGER.info("I see the invalid messages");
        new InteractiveCalculatorCLIBL().seeInvalidMessages();
    }

    @Then("I can close the interactive CLI for calculator")
    public void iCanCloseTheInteractiveCLIForCalculator() {
        LOGGER.info("I can close the interactive CLI for calculator");
        new InteractiveCalculatorCLIBL().closeCalculatorCLI();
    }

}
