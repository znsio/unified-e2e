package com.znsio.teswiz.steps;

import com.znsio.teswiz.businessLayer.autoscroll.AutoScrollBL;
import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Direction;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AutoScrollSteps {
    private static final Logger LOGGER = LogManager.getLogger(AutoScrollSteps.class.getName());
    private final TestExecutionContext context;

    public AutoScrollSteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        LOGGER.info("context: " + context.getTestName());
    }

    @Given("on landing page, I see the list of available apps in a dropdown list")
    public void onLandingPageISeeTheListOfAvailableAppsInADropdownList() {
        Drivers.createDriverFor(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform(), context);
        new AutoScrollBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).goToDropdownWindow();
    }

    @Then("I should be able to scroll {string} in dynamic layer")
    public void iShouldBeAbleToScrollInDynamicLayer(String direction) {
        new AutoScrollBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform())
                .verifyScrollInDynamicLayerFunctionality(Direction.valueOf(direction.toUpperCase()));
    }
}
