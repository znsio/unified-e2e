package com.znsio.teswiz.steps;

import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;
import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.APPLITOOLS;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.entities.TEST_CONTEXT;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.tools.HeartBeat;
import com.znsio.teswiz.tools.ReportPortalLogger;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.testng.annotations.DataProvider;

import java.util.HashMap;

public class RunTestCukes
        extends AbstractTestNGCucumberTests {
    private static final Logger LOGGER = LogManager.getLogger(RunTestCukes.class.getName());
    private final TestExecutionContext context;

    public RunTestCukes() {
        long threadId = Thread.currentThread().getId();
        LOGGER.info("RunTestCukes: Constructor: ThreadId: " + threadId);
        context = SessionContext.getTestExecutionContext(threadId);
        System.setProperty(TEST_CONTEXT.TAGS_TO_EXCLUDE_FROM_CUCUMBER_REPORT, "@android,@web,@iOS,@api,@cli,@pdf");
    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        LOGGER.info(String.format("RunTestCukes: ThreadId: %d: in overridden scenarios%n", Thread.currentThread().getId()));
        Object[][] scenarios = super.scenarios();
        LOGGER.info(scenarios);
        return scenarios;
    }

    @Before
    public void beforeTestScenario(Scenario scenario) {
        long threadId = Thread.currentThread().getId();
        LOGGER.info("RunTestCukes: ThreadId : '%d' :: afterScenario: '%s'".formatted(threadId, scenario.getName()));
        new Hooks().beforeScenario(scenario);
        addApplitoolsUFGConfigurationToContext();
        isEnvironmentHealthy(context.getTestName(), Runner.getFromEnvironmentConfiguration(SAMPLE_TEST_CONTEXT.HEALTH_CHECK_URL));
    }

    @After
    public void afterTestScenario(Scenario scenario) {
        long threadId = Thread.currentThread().getId();
        LOGGER.info("RunTestCukes: ThreadId : '%d' :: afterScenario: '%s'".formatted(threadId, scenario.getName()));
        this.closeHeartBeatThreads();
        new Hooks().afterScenario(scenario);
    }

    private void addApplitoolsUFGConfigurationToContext() {
        Configuration ufgConfig = new Configuration();
        ufgConfig.addBrowser(1024, 1024, BrowserType.CHROME);
        ufgConfig.addBrowser(1024, 1024, BrowserType.FIREFOX);
        ufgConfig.addDeviceEmulation(DeviceName.iPhone_X, ScreenOrientation.PORTRAIT);
        ufgConfig.addDeviceEmulation(DeviceName.OnePlus_7T_Pro, ScreenOrientation.LANDSCAPE);
        LOGGER.info("Use the following Browsers and devices in UFG config: " + ufgConfig.getBrowsersInfo());
        context.addTestState(APPLITOOLS.UFG_CONFIG, ufgConfig);
    }

    private void isEnvironmentHealthy(String testName, String healthCheckUrl) {
        if (Runner.getTargetEnvironment().equalsIgnoreCase("prod")) {
            LOGGER.info("Checking health of environment from: %s".formatted(healthCheckUrl));

            // TODO - replace logic as per application's health check and observability capability
            // HttpResponse<JsonNode> healthCheckResponse = UnirestService.getHttpResponse(healthCheckUrl);
            // LOGGER.info("Health check response: %s", healthCheckResponse.getBody());

            boolean isEnvironmentHealthy = true;
            LOGGER.info("Environment healthy? %s".formatted(isEnvironmentHealthy));

            // ReportPortalLogger.logInfoMessage("Health check status: " + healthCheckResponse.getBody());
            ReportPortalLogger.logInfoMessage("Health check status: " + isEnvironmentHealthy);

            Assertions.assertThat(isEnvironmentHealthy).as("Health check failed").isTrue();
        }
    }

    private void closeHeartBeatThreads() {
        if (null != context.getTestState(SAMPLE_TEST_CONTEXT.HEARTBEAT_MAP)) {
            HashMap<String, HeartBeat> heartbeatMap = (HashMap<String, HeartBeat>) context.getTestState(SAMPLE_TEST_CONTEXT.HEARTBEAT_MAP);
            LOGGER.info(String.format("afterScenario: closeHeartBeatThreads: heartbeatMap: %d", heartbeatMap.size()));
            LOGGER.info("Active thread count before closing all heartBeats: " + Thread.activeCount());
            for (HeartBeat heartbeat : heartbeatMap.values()) {
                heartbeat.stopHeartBeat();
            }
            heartbeatMap.clear();
            LOGGER.info("Active thread count after closing all heartBeats " + Thread.activeCount());
        }
    }
}
