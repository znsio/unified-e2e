package com.znsio.teswiz.steps;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.TEST_CONTEXT;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.UserPersonaDetails;
import com.znsio.teswiz.tools.ReportPortalLogger;
import com.znsio.teswiz.tools.ScreenShotManager;
import com.znsio.teswiz.tools.cmd.AsyncCommandLineExecutor;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Hooks {
    private static final Logger LOGGER = LogManager.getLogger(Hooks.class.getName());
    private static final List<String> excludeLoggingSystemProperties = Arrays.asList("java.class.path", "java.library.path");
    private static final List<String> excludeLoggingEnvVariables = Arrays.asList("KEY", "PASSWORD");
    private final TestExecutionContext testExecutionContext;
    private final long threadId;

    public Hooks() {
        threadId = Thread.currentThread().getId();
        testExecutionContext = Runner.getTestExecutionContext(threadId);
    }

    public void beforeScenario(Scenario scenario) {
        Object isHooksInitialized = testExecutionContext.getTestState(TEST_CONTEXT.HOOKS_INITIALIZED);
        LOGGER.info("Hooks: beforeScenario: isHooksInitialized: " + isHooksInitialized);
        if (null == isHooksInitialized) {
            LOGGER.info("Hooks: ThreadId : '%d' :: beforeScenario: '%s'".formatted(threadId, scenario.getName()));
            if (!Runner.isAPI() || !Runner.isCLI() || !Runner.isPDF()) {
                testExecutionContext.addTestState(TEST_CONTEXT.SCREENSHOT_MANAGER, new ScreenShotManager());
            }
            testExecutionContext.addTestState(TEST_CONTEXT.CURRENT_USER_PERSONA_DETAILS,
                                              new UserPersonaDetails());
            SoftAssertions softly = new SoftAssertions();
            testExecutionContext.addTestState(TEST_CONTEXT.SOFT_ASSERTIONS, softly);
            addEnvironmentVariablesToReportPortal();
            addSystemPropertiesToReportPortal();
            startTheAsyncCommandLineExecutor();
            testExecutionContext.addTestState(TEST_CONTEXT.HOOKS_INITIALIZED, true);
        }
    }

    public void afterScenario(Scenario scenario) {
        Object isHooksInitialized = testExecutionContext.getTestState(TEST_CONTEXT.HOOKS_INITIALIZED);
        LOGGER.info("Hooks: afterScenario: isHooksInitialized: " + isHooksInitialized);
        if (null != isHooksInitialized) {
            LOGGER.info("Hooks: ThreadId : '%d' :: afterScenario: '%s'".formatted(threadId, scenario.getName()));
            testExecutionContext.addTestState(TEST_CONTEXT.HOOKS_INITIALIZED, null);
            Drivers.attachLogsAndCloseAllDrivers(scenario);
            closeTheAsyncCommandLineExecutor();
            SoftAssertions softly = Runner.getSoftAssertion(threadId);
            LOGGER.info("Hooks: Assert all soft assertions");
            softly.assertAll();
        }
    }

    private void startTheAsyncCommandLineExecutor() {
        if (Runner.isCLI()) {
            LOGGER.info("Start the AsyncCommandLineExecutor");
            AsyncCommandLineExecutor asyncCommandLineExecutor = new AsyncCommandLineExecutor();
            testExecutionContext.addTestState(TEST_CONTEXT.ASYNC_COMMAND_LINE_EXECUTOR, asyncCommandLineExecutor);
        }
    }

    private void addEnvironmentVariablesToReportPortal() {
        Map<String, String> envVars = System.getenv();
        StringBuilder envVarInfo = new StringBuilder();

        envVars.entrySet().stream()
                .filter(entry -> excludeLoggingEnvVariables.stream().noneMatch(excludedKey ->
                        entry.getKey().toLowerCase().contains(excludedKey.toLowerCase())))
                .forEach(entry -> envVarInfo.append("\t").append(entry.getKey()).append(":").append(entry.getValue()).append("\n"));

        envVars.entrySet().stream()
                .filter(entry -> excludeLoggingEnvVariables.stream().anyMatch(excludedKey ->
                        entry.getKey().toLowerCase().contains(excludedKey.toLowerCase())))
                .forEach(entry -> envVarInfo.append("\t").append(entry.getKey()).append(":").append("*****").append("\n"));

        ReportPortalLogger.logDebugMessage(
                String.format("Hooks: Environment Variables:%n%s", envVarInfo));
    }

    private void addSystemPropertiesToReportPortal() {
        Properties props = System.getProperties();
        StringBuilder propVars = new StringBuilder();

        props.entrySet().stream()
                .filter(entry -> excludeLoggingSystemProperties.stream().noneMatch(excludedKey ->
                        entry.getKey().toString().toLowerCase().contains(excludedKey.toLowerCase())))
                .forEach(entry -> propVars.append("\t").append(entry.getKey()).append(":").append(entry.getValue()).append("\n"));

        props.entrySet().stream()
                .filter(entry -> excludeLoggingSystemProperties.stream().anyMatch(excludedKey ->
                        entry.getKey().toString().toLowerCase().contains(excludedKey.toLowerCase())))
                .forEach(entry -> propVars.append("\t").append(entry.getKey()).append(":").append("*****").append("\n"));

        ReportPortalLogger.logDebugMessage(
                String.format("Hooks: System Properties:%n%s", propVars));
    }

    private void closeTheAsyncCommandLineExecutor() {
        if (Runner.isCLI()) {
            LOGGER.info("Close the AsyncCommandLineExecutor");
            AsyncCommandLineExecutor asyncCommandLineExecutor = (AsyncCommandLineExecutor) testExecutionContext.getTestState(TEST_CONTEXT.ASYNC_COMMAND_LINE_EXECUTOR);
            asyncCommandLineExecutor.close();
        }
    }

}
