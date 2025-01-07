package com.znsio.teswiz.steps;

import com.znsio.teswiz.businessLayer.helloWorld.HelloWorldBL;
import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import io.cucumber.java.en.Given;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HelloWorldSteps {
    private static final Logger LOGGER = LogManager.getLogger(HelloWorldSteps.class.getName());
    private final TestExecutionContext context;

    public HelloWorldSteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        LOGGER.info("context: " + context.getTestName());
    }

    @Given("I make the number random {string} times")
    public void randomNumberGenerator(String numberOfTimes) {
        LOGGER.info(System.out.printf("randomNumberGenerator:'%s' - Persona:'%s'", numberOfTimes, SAMPLE_TEST_CONTEXT.ME));
        Drivers.createDriverFor(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform(), context);
        new HelloWorldBL().randomNumberGenerator(numberOfTimes);
    }

}
