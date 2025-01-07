package com.znsio.teswiz.steps;

import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.DataProvider;

public class RunCukes
        extends AbstractTestNGCucumberTests {
    private static final Logger LOGGER = LogManager.getLogger(RunCukes.class.getName());
    private final TestExecutionContext context;

    public RunCukes() {
        long threadId = Thread.currentThread().getId();
        LOGGER.info(String.format("RunCukes: ThreadId: '%s': Constructor", threadId));
        context = SessionContext.getTestExecutionContext(threadId);
    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        LOGGER.info(String.format("RunCukes: ThreadId: %d: in overridden scenarios%n",
                                  Thread.currentThread().getId()));
        Object[][] scenarios = super.scenarios();
        LOGGER.info(scenarios);
        return scenarios;
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        long threadId = Thread.currentThread().getId();
        LOGGER.info("RunCukes: ThreadId : '%d' :: afterScenario: '%s'".formatted(threadId, scenario.getName()));
        new Hooks().beforeScenario(scenario);
    }

    @After
    public void afterScenario(Scenario scenario) {
        long threadId = Thread.currentThread().getId();
        LOGGER.info("RunCukes: ThreadId : '%d' :: afterScenario: '%s'".formatted(threadId, scenario.getName()));
        new Hooks().afterScenario(scenario);
    }
}
