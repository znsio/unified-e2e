package com.znsio.teswiz.steps;

import com.znsio.teswiz.businessLayer.confengine.ConfEngineBL;
import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import io.cucumber.java.en.Given;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfEngineSteps {
    private static final Logger LOGGER = LogManager.getLogger(ConfEngineSteps.class.getName());
    private final TestExecutionContext context;

    public ConfEngineSteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        LOGGER.info("context: " + context.getTestName());
    }

    @Given("I see the list of conferences")
    public void iSeeTheListOfConferences() {
        LOGGER.info(System.out.printf("iSeeTheListOfConferences - Persona:'%s'",
                                      SAMPLE_TEST_CONTEXT.ME));
        Drivers.createDriverFor(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform(), context);
        new ConfEngineBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).seeListOfConferences();
    }
}
