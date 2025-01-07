package com.znsio.teswiz.steps;

import com.znsio.teswiz.businessLayer.heartbeat.HeartBeatBL;
import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.znsio.teswiz.tools.Wait.waitFor;

public class HeartBeatSteps {
    private static final Logger LOGGER = LogManager.getLogger(HeartBeatSteps.class.getName());
    private final TestExecutionContext context;

    public HeartBeatSteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        LOGGER.info("context: " + context.getTestName());
    }

    @And("I can see the started heartbeat for {string}")
    public void iCanSeeTheStartedHeartbeatFor(String userPersona) {
        new HeartBeatBL().seeHeartBeatFor(userPersona);
    }

    @When("I start a heartbeat for {string}")
    public void iStartAHeartbeatFor(String userPersona) {
        new HeartBeatBL().startHeatBeat(userPersona);
    }

    @And("I wait for {string} seconds")
    public void iWaitForSeconds(String waitForSeconds) {
        waitFor(Integer.parseInt(waitForSeconds));
    }
}
