package com.znsio.teswiz.runner;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.TEST_CONTEXT;
import com.znsio.teswiz.exceptions.InvalidTestDataException;
import io.cucumber.java.Scenario;
import io.cucumber.java.Status;
import kong.unirest.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.znsio.teswiz.context.SessionContext.getTestExecutionContext;
import static com.znsio.teswiz.runner.Runner.DEFAULT;
import static io.appium.java_client.remote.options.SupportsDeviceNameOption.DEVICE_NAME_OPTION;
import static org.openqa.selenium.remote.CapabilityType.BROWSER_NAME;

public class Drivers {
    private static final Logger LOGGER = LogManager.getLogger(Drivers.class.getName());
    private static final String NO_DRIVER_FOUND_FOR_USER_PERSONA = "No Driver found for user " + "persona: '%s'";

    private Drivers() {
        LOGGER.debug("Drivers - private constructor");
    }

    public static Driver setDriverFor(String userPersona, Platform forPlatform, TestExecutionContext context) {
        LOGGER.info(String.format("setDriverFor: start: userPersona: '%s', Platform: '%s'", userPersona, forPlatform.name()));
        UserPersonaDetails userPersonaDetails = getUserPersonaDetails(context);
        if (!userPersonaDetails.isDriverAssignedForUser(userPersona)) {
            String message = String.format("ERROR: Driver for user persona: '%s' DOES NOT EXIST%nAvailable drivers: '%s'", userPersona, userPersonaDetails.getAllUserPersonasForAssignedDrivers());
            throw new InvalidTestDataException(message);
        }
        Driver currentDriver = userPersonaDetails.getDriverAssignedForUser(userPersona);
        context.addTestState(TEST_CONTEXT.CURRENT_DRIVER, currentDriver);
        context.addTestState(TEST_CONTEXT.CURRENT_USER_PERSONA, userPersona);
        context.addTestState(TEST_CONTEXT.CURRENT_PLATFORM, forPlatform);
        return currentDriver;
    }

    public static boolean isDriverAssignedForUser(String userPersona) {
        TestExecutionContext context = getTestExecutionContext(Thread.currentThread().getId());
        return getUserPersonaDetails(context).isDriverAssignedForUser(userPersona);
    }

    static UserPersonaDetails getUserPersonaDetails(TestExecutionContext context) {
        return (UserPersonaDetails) context.getTestState(TEST_CONTEXT.CURRENT_USER_PERSONA_DETAILS);
    }

    public static Driver createDriverFor(String userPersona, Platform forPlatform, TestExecutionContext context) {
        return createDriverFor(userPersona, DEFAULT, Runner.getBrowser(), forPlatform, context);
    }

    public static Driver createDriverFor(String userPersona, String appName, String browserName, Platform forPlatform, TestExecutionContext context) {
        LOGGER.info(String.format("createDriverFor: start: userPersona: '%s', Platform: '%s'", userPersona, forPlatform.name()));
        context.addTestState(TEST_CONTEXT.CURRENT_USER_PERSONA, userPersona);
        context.addTestState(TEST_CONTEXT.CURRENT_PLATFORM, forPlatform);
        UserPersonaDetails userPersonaDetails = getUserPersonaDetails(context);
        userPersonaDetails.addAppName(userPersona, appName);
        userPersonaDetails.addPlatform(userPersona, forPlatform);

        if (userPersonaDetails.isDriverAssignedForUser(userPersona)) {
            String message = String.format("ERROR: Driver for user persona: '%s' ALREADY EXISTS%nAvailable drivers: '%s'", userPersona, userPersonaDetails.getAllUserPersonasForAssignedDrivers());
            throw new InvalidTestDataException(message);
        }

        Driver currentDriver = createDriverForPlatform(userPersona, browserName, forPlatform, context);
        context.addTestState(TEST_CONTEXT.CURRENT_DRIVER, currentDriver);
        userPersonaDetails.addDriver(userPersona, currentDriver);
        LOGGER.info(String.format("createDriverFor: done: userPersona: '%s', Platform: '%s'%n", userPersona, forPlatform.name()));
        updateTestNameInCloud(currentDriver.getInnerDriver(), context.getTestName(), userPersona);
        return currentDriver;
    }

    private static void updateTestNameInCloud(WebDriver driver, String testName, String userPersona) {
        String updatedTestName = testName + "-" + userPersona;
        if (Runner.getCloudName().equalsIgnoreCase("browserstack")) {
            LOGGER.info(String.format("updateTestNameInCloud for BrowserStack: '%s'", updatedTestName));
            final JavascriptExecutor jse = (JavascriptExecutor) driver;
            JSONObject executorObject = new JSONObject();
            JSONObject argumentsObject = new JSONObject();
            argumentsObject.put("name", updatedTestName);
            executorObject.put("action", "setSessionName");
            executorObject.put("arguments", argumentsObject);
            jse.executeScript(String.format("browserstack_executor: %s", executorObject));
        }
    }

    @NotNull
    private static Driver createDriverForPlatform(String userPersona, String browserName, Platform forPlatform, TestExecutionContext context) {
        Driver currentDriver;
        switch (forPlatform) {
            case android:
                currentDriver = AppiumDriverManager.createAndroidDriverForUser(userPersona, forPlatform, context);
                break;
            case iOS:
                currentDriver = AppiumDriverManager.createIOSDriverForUser(userPersona, forPlatform, context);
                break;
            case windows:
                currentDriver = AppiumDriverManager.createWindowsDriverForUser(userPersona, forPlatform, context);
                break;
            case web:
                currentDriver = BrowserDriverManager.createWebDriverForUser(userPersona, browserName, forPlatform, context);
                break;
            case electron:
                currentDriver = BrowserDriverManager.createElectronDriverForUser(userPersona, browserName, forPlatform, context);
                break;
            default:
                throw new InvalidTestDataException(String.format("Unexpected platform value: '%s' provided to assign Driver for user: '%s': ", forPlatform, userPersona));
        }
        return currentDriver;
    }

    static String getCapabilityFor(org.openqa.selenium.Capabilities capabilities, String name) {
        Object capability = capabilities.getCapability(name);
        return null == capability ? "" : capability.toString();
    }

    public static Driver createDriverFor(String userPersona, String appName, Platform forPlatform, TestExecutionContext context) {
        return createDriverFor(userPersona, appName, Runner.getBrowser(), forPlatform, context);
    }

    public static Driver getDriverForUser(String userPersona) {
        TestExecutionContext context = getTestExecutionContext(Thread.currentThread().getId());
        UserPersonaDetails userPersonaDetails = getUserPersonaDetails(context);

        if (!userPersonaDetails.isDriverAssignedForUser(userPersona)) {
            LOGGER.info("getDriverForUser: Drivers available for userPersonas: " + userPersonaDetails.getAllUserPersonasForAssignedDrivers());
            throw new InvalidTestDataException(String.format(NO_DRIVER_FOUND_FOR_USER_PERSONA, userPersona));
        }

        return userPersonaDetails.getDriverAssignedForUser(userPersona);
    }

    public static Driver getDriverForCurrentUser(long threadId) {
        TestExecutionContext context = getTestExecutionContext(threadId);
        String userPersona = context.getTestStateAsString(TEST_CONTEXT.CURRENT_USER_PERSONA);
        UserPersonaDetails userPersonaDetails = getUserPersonaDetails(context);

        if (!userPersonaDetails.isDriverAssignedForUser(userPersona)) {
            LOGGER.info("getDriverForUser: Drivers available for userPersonas: " + userPersonaDetails.getAllUserPersonasForAssignedDrivers());
            throw new InvalidTestDataException(String.format(NO_DRIVER_FOUND_FOR_USER_PERSONA, userPersona));
        }

        return userPersonaDetails.getDriverAssignedForUser(userPersona);
    }

    public static String getNameOfDeviceUsedByUser(String userPersona) {
        return getDeviceOrBrowserNameFromCapabilitiesForUser(userPersona, DEVICE_NAME_OPTION);
    }

    static String getBrowserNameForUser(String userPersona) {
        return getDeviceOrBrowserNameFromCapabilitiesForUser(userPersona, BROWSER_NAME);
    }

    @NotNull
    private static String getDeviceOrBrowserNameFromCapabilitiesForUser(String userPersona, String capabilityName) {
        UserPersonaDetails userPersonaDetails = getUserPersonaDetails(getTestExecutionContext(Thread.currentThread().getId()));
        Capabilities userPersonaCapabilities = userPersonaDetails.getCapabilitiesAssignedForUser(userPersona);
        String deviceOrBrowserName = (String) userPersonaCapabilities.getCapability(capabilityName);
        if (null == deviceOrBrowserName) {
            LOGGER.info("Capabilities available for userPersona: '" + userPersona + "': " + userPersonaCapabilities.asMap().keySet());
            throw new InvalidTestDataException(String.format("'%s' capability NOT found for user persona: '%s'%n%s", capabilityName, userPersona, userPersonaCapabilities.asMap().keySet()));
        }
        return deviceOrBrowserName;
    }

    static Platform getPlatformForUser(String userPersona) {
        UserPersonaDetails userPersonaDetails = getUserPersonaDetails(getTestExecutionContext(Thread.currentThread().getId()));

        if (!userPersonaDetails.isDriverAssignedForUser(userPersona)) {
            LOGGER.info("getPlatformForUser: Platforms available for userPersonas: ");
            userPersonaDetails.getAllUserPersonasForAssignedPlatforms().forEach(key -> LOGGER.info("\tUser Persona: " + key + ": Platform: " + userPersonaDetails.getPlatformAssignedForUser(key).name()));
            throw new InvalidTestDataException(String.format(NO_DRIVER_FOUND_FOR_USER_PERSONA, userPersona));
        }

        return userPersonaDetails.getPlatformAssignedForUser(userPersona);
    }

    public static void attachLogsAndCloseAllDrivers(Scenario scenario) {
        long currentThreadId = Thread.currentThread().getId();
        LOGGER.info(String.format("Close all drivers for test on ThreadId: - %d", currentThreadId));
        TestExecutionContext context = getTestExecutionContext(currentThreadId);
        UserPersonaDetails userPersonaDetails = getUserPersonaDetails(context);

        Map<String, Driver> allAssignedUserPersonasAndDrivers = userPersonaDetails.getAllAssignedUserPersonasAndDrivers();
        LOGGER.info("Closing driver for the following userPersonas: " + allAssignedUserPersonasAndDrivers.keySet());
        allAssignedUserPersonasAndDrivers.forEach((userPersona, driver) -> {
            LOGGER.info("Closing driver for the userPersonas: " + userPersona);
            driver.getVisual().takeScreenshot("afterHooks", userPersona);
            updateTestStatusInCloud(driver.getInnerDriver(), scenario.getStatus());
            validateVisualTestResults(userPersona, driver);
            attachLogsAndCloseDriver(userPersona, driver);
        });
        AppiumDriverManager.freeDevices();
        userPersonaDetails.clearAllDrivers();
        userPersonaDetails.clearAllAppNames();
        userPersonaDetails.clearAllCapabilities();
        userPersonaDetails.clearAllPlatforms();
        userPersonaDetails.clearLogFileNames();
    }

    private static void updateTestStatusInCloud(WebDriver driver, Status cucumberScenarioStatus) {
        LOGGER.info(String.format("updateTestStatusInCloud for Scenario with status: '%s'", cucumberScenarioStatus.name()));
        long currentThreadId = Thread.currentThread().getId();
        SoftAssertions softly = Runner.getSoftAssertion(currentThreadId);

        String scenarioStatus = "passed";
        String scenarioFailureReasons = "Scenario passed";

        if (!cucumberScenarioStatus.equals(Status.PASSED)) {
            scenarioStatus = "failed";
            scenarioFailureReasons = "Assertion failure";
        }

        if (!softly.errorsCollected().isEmpty()) {
            scenarioStatus = "failed";
            String scenarioSoftFailureReasons = String.format("'%d' Soft Assertion failure(s)", softly.errorsCollected().size());
            scenarioFailureReasons = scenarioFailureReasons.toLowerCase().contains("failure") ? scenarioFailureReasons + ", and " + scenarioSoftFailureReasons : scenarioSoftFailureReasons;
        }

        LOGGER.info(String.format("Scenario status: '%s' :: '%s'", scenarioStatus, scenarioFailureReasons));

        if (Runner.getCloudName().equalsIgnoreCase("browserstack")) {
            updateTestStatusInBrowserStack((JavascriptExecutor) driver, scenarioStatus, scenarioFailureReasons);
        }
    }

    private static void updateTestStatusInBrowserStack(JavascriptExecutor driver, String scenarioStatus, String scenarioFailureReasons) {
        LOGGER.info(String.format("updateTestStatusInCloud for BrowserStack: '%s'", scenarioStatus));
        JSONObject executorObject = new JSONObject();
        JSONObject argumentsObject = new JSONObject();
        argumentsObject.put("status", scenarioStatus);
        argumentsObject.put("reason", scenarioFailureReasons);
        executorObject.put("action", "setSessionStatus");
        executorObject.put("arguments", argumentsObject);
        driver.executeScript(String.format("browserstack_executor: %s", executorObject));
    }

    private static void validateVisualTestResults(String userPersona, Driver driver) {
        LOGGER.info(String.format("\tGetting visual validation results for: User Persona: %s", userPersona));
        driver.getVisual().handleTestResults(userPersona, driver.getType());
    }

    private static void attachLogsAndCloseDriver(String userPersona, Driver driver) {
        String driverName = Driver.PDF_DRIVER;
        if (!driver.getType().equalsIgnoreCase(Driver.PDF_DRIVER)) {
            driverName = driver.getInnerDriver().getClass().getSimpleName();
        }
        LOGGER.info(String.format("attachLogsAndCloseDriver: %s - %s - %s", userPersona, driver.getType(), driverName));
        switch (driver.getType()) {
            case Driver.WEB_DRIVER:
                BrowserDriverManager.closeWebDriver(userPersona, driver);
                break;
            case Driver.APPIUM_DRIVER:
                AppiumDriverManager.closeAppiumDriver(userPersona, driver);
                break;
            case Driver.PDF_DRIVER:
                break;
            default:
                throw new InvalidTestDataException(String.format("Unexpected driver type: '%s'", driver.getType()));
        }
    }

    public static Set<String> getAvailableUserPersonas() {
        UserPersonaDetails userPersonaDetails = getUserPersonaDetails(getTestExecutionContext(Thread.currentThread().getId()));
        String userPersonaPrefix = Thread.currentThread().getId() + "-";
        return userPersonaDetails.getAllUserPersonasForAssignedDrivers().stream().map(personaForCurrentThread -> personaForCurrentThread.replace(userPersonaPrefix, "")).collect(Collectors.toSet());
    }

    public static void assignNewPersonaToExistingDriver(String userPersona, String newUserPersona, TestExecutionContext context) {
        UserPersonaDetails userPersonaDetails = getUserPersonaDetails(context);

        if (!userPersonaDetails.isDriverAssignedForUser(userPersona)) {
            LOGGER.info("assignNewPersonaToExistingDriver: Drivers available for userPersonas: " + userPersonaDetails.getAllUserPersonasForAssignedDrivers());
            throw new InvalidTestDataException(String.format(NO_DRIVER_FOUND_FOR_USER_PERSONA, userPersona));
        }

        Driver currentDriver = userPersonaDetails.getDriverAssignedForUser(userPersona);
        Platform currentPlatform = userPersonaDetails.getPlatformAssignedForUser(userPersona);

        context.addTestState(TEST_CONTEXT.CURRENT_DRIVER, currentDriver);
        context.addTestState(TEST_CONTEXT.CURRENT_USER_PERSONA, newUserPersona);
        context.addTestState(TEST_CONTEXT.CURRENT_PLATFORM, currentPlatform);

        userPersonaDetails.assignNewPersonaForUser(userPersona, newUserPersona);

        LOGGER.info(String.format("assignNewPersonaToExistingDriver: Persona updated from '%s' to '%s'", userPersona, newUserPersona));
    }

    static void addUserPersonaDriverCapabilities(String userPersona, Capabilities capabilities) {
        UserPersonaDetails userPersonaDetails = getUserPersonaDetails(Runner.getTestExecutionContext(Thread.currentThread().getId()));
        userPersonaDetails.addCapabilities(userPersona, capabilities);
    }

    static void addUserPersonaDeviceLogFileName(String userPersona, String deviceLogsFileName, Platform forPlatform) {
        UserPersonaDetails userPersonaDetails = getUserPersonaDetails(Runner.getTestExecutionContext(Thread.currentThread().getId()));
        userPersonaDetails.addDeviceLogFileNameFor(userPersona, forPlatform.name(), deviceLogsFileName);
    }

    static Capabilities getCapabilitiesFor(String userPersona) {
        UserPersonaDetails userPersonaDetails = getUserPersonaDetails(Runner.getTestExecutionContext(Thread.currentThread().getId()));
        return userPersonaDetails.getCapabilitiesAssignedForUser(userPersona);
    }

    static String getAppNamefor(String userPersona) {
        UserPersonaDetails userPersonaDetails = getUserPersonaDetails(Runner.getTestExecutionContext(Thread.currentThread().getId()));
        return userPersonaDetails.getAppName(userPersona);
    }

    public static Visual getVisualDriverForCurrentUser(long threadId) {
        return getDriverForCurrentUser(threadId).getVisual();
    }

    public static void createPDFDriverFor(String userPersona, Platform forPlatform, TestExecutionContext context, String pdfFileName) {
        Driver currentDriver = new Driver(userPersona, forPlatform, context, pdfFileName);
        context.addTestState(TEST_CONTEXT.CURRENT_DRIVER, currentDriver);
        context.addTestState(TEST_CONTEXT.CURRENT_USER_PERSONA, userPersona);
        context.addTestState(TEST_CONTEXT.CURRENT_PLATFORM, forPlatform);
        UserPersonaDetails userPersonaDetails = getUserPersonaDetails(context);
        userPersonaDetails.addAppName(userPersona, pdfFileName);
        userPersonaDetails.addPlatform(userPersona, forPlatform);
        userPersonaDetails.addDriver(userPersona, currentDriver);
        LOGGER.info(String.format("createDriverFor: done: userPersona: '%s', Platform: '%s'%n", userPersona, forPlatform.name()));
    }
}
