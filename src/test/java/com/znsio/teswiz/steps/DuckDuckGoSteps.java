package com.znsio.teswiz.steps;

import com.znsio.teswiz.businessLayer.duckduckgo.DuckDuckGoBL;
import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DuckDuckGoSteps {
    private static final Logger LOGGER = LogManager.getLogger(DuckDuckGoSteps.class.getName());
    private final TestExecutionContext context;

    public DuckDuckGoSteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        LOGGER.info("context: " + context.getTestName());
    }

    @Given("I launch the duckduckgo browser")
    public void iLaunchTheDuckduckgoBrowser() {
        LOGGER.info(
                System.out.printf("iLaunchTheDuckduckgoBrowser - Persona:'%s'", SAMPLE_TEST_CONTEXT.ME));
        Drivers.createDriverFor(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform(), context);
        new DuckDuckGoBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).launchBrowser();
    }

    @When("I cancel changing the default browser")
    public void iCancelChangingTheDefaultBrowser() {
        new DuckDuckGoBL().cancelChangingDefaultBrowserPopUp();
    }

    @Then("I can see the default text in the webview")
    public void iCanSeeTheDefaultTextInTheWebview() {
        new DuckDuckGoBL().switchToWebViewAndCheckDefaultText();
    }

    @And("I can switch back to the native view and enter the teswiz url")
    public void iCanSwitchBackToTheNativeViewAndEnterTheTeswizUrl() {
        new DuckDuckGoBL().swithToNativeContextAndGoToTeswiz();
    }
}
