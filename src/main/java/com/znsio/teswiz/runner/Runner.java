package com.znsio.teswiz.runner;

import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.TEST_CONTEXT;
import com.znsio.teswiz.exceptions.InvalidTestDataException;
import io.cucumber.core.cli.Main;
import net.masterthought.cucumber.Reportable;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.assertj.core.api.SoftAssertions;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static com.znsio.teswiz.runner.DeviceSetup.getCloudNameFromCapabilities;
import static com.znsio.teswiz.runner.Setup.HOST_NAME;
import static com.znsio.teswiz.runner.Setup.IS_FAILING_TEST_SUITE;
import static com.znsio.teswiz.runner.Setup.SET_HARD_GATE;

public class Runner {
    public static final String OS_NAME = System.getProperty("os.name");
    public static final boolean IS_WINDOWS = OS_NAME.toLowerCase().startsWith("windows");
    public static final boolean IS_MAC = OS_NAME.toLowerCase().startsWith("mac");
    public static final String USER_DIRECTORY = System.getProperty("user.dir");
    public static final String USER_NAME = System.getProperty("user.name");
    public static final String NOT_SET = "not-set";
    public static final String DEFAULT = "default";
    public static final String DEBUG = "DEBUG";
    public static final String INFO = "INFO";
    public static final String WARN = "WARN";

    private static final Logger LOGGER = LogManager.getLogger(Runner.class.getName());
    private static final String INVALID_KEY_MESSAGE = "Invalid key name ('%s') provided";

    public Runner() {
        throw new InvalidTestDataException("Required args not provided to Runner");
    }

    public Runner(String configFilePath, String stepDefDirName, String featuresDirName) {
        Path path = Paths.get(configFilePath);
        if (!Files.exists(path)) {
            throw new InvalidTestDataException(
                    String.format("Invalid path ('%s') provided for config", configFilePath));
        }
        Setup.load(configFilePath);
        List<String> cukeArgs = Setup.getExecutionArguments();
        run(cukeArgs, stepDefDirName, featuresDirName);
    }

    public static Platform getPlatformForUser(String userPersona) {
        return Drivers.getPlatformForUser(userPersona);
    }

    public static String getHostName() {
        return Setup.getFromConfigs(HOST_NAME);
    }

    private void run(List<String> args, String stepDefsDir, String featuresDir) {
        args.add("--glue");
        args.add(stepDefsDir);
        args.add(featuresDir);
        boolean isHardGate = isHardGateSet();
        boolean isRunningFailingTests = isRunningFailingTestSuite();
        LOGGER.info("Begin running tests with args: {}", args);
        String[] array = args.toArray(String[]::new);
        try {
            byte status = Main.run(array);
            LOGGER.info("Execution status: {}", status);
            Setup.cleanUpExecutionEnvironment();
            Reportable overviewReport = CustomReports.generateReport();
            int totalFeatures = overviewReport.getFeatures();
            int totalScenarios = overviewReport.getScenarios();
            int failedScenarios = overviewReport.getFailedScenarios();
            int passedScenarios = overviewReport.getPassedScenarios();
            LOGGER.info("Total features: {}", totalFeatures);
            LOGGER.info("Total scenarios: {}", totalScenarios);
            LOGGER.info("Passed scenarios: {}", passedScenarios);
            LOGGER.info("Failed scenarios: {}", failedScenarios);

            if (isHardGate) {
                status = getStatus(isRunningFailingTests, totalFeatures, totalScenarios, passedScenarios, failedScenarios);
                LOGGER.info("SET_HARD_GATE is '%s', and '%s' is '%s'. Returning status '%s' of hard gate".
                        formatted(isHardGate, IS_FAILING_TEST_SUITE, isRunningFailingTests, status));
            } else {
                LOGGER.info("SET_HARD_GATE is '%s'. Return actual status '%s' of test execution".formatted(isHardGate, status));
            }
            System.exit(status);
        } catch (Exception e) {
            LOGGER.error("EXCEPTION: {}", e.getMessage());
            LOGGER.error(e);
            System.exit(1);
        }
    }

    private boolean isHardGateSet() {
        return Setup.getBooleanValueFromConfigs(SET_HARD_GATE);
    }

    static byte getStatus(boolean runningFailingTestSuite, int totalFeatures, int totalScenarios, int passedScenarios, int failedScenarios) {
        byte status;
        LOGGER.info("Is runningFailingTestSuite: {}", runningFailingTestSuite);

        // running failing test suite - passedScenarios==0, exit 0
        // running passing test suite - failedScenarios==0, exit 0
        status = (runningFailingTestSuite && passedScenarios == 0) ||
                 (!runningFailingTestSuite && failedScenarios == 0) ? (byte) 0 : (byte) 1;
        LOGGER.info("Status: {}", status);
        return status;
    }

    private boolean isRunningFailingTestSuite() {
        return Setup.getBooleanValueFromConfigs(IS_FAILING_TEST_SUITE);
    }

    public static Platform getPlatform() {
        return Setup.getPlatform();
    }

    public static Map getApplitoolsConfiguration() {
        return Setup.initialiseApplitoolsConfiguration();
    }

    public static String getCloudName() {
        return getCloudNameFromCapabilities();
    }

    public static String getCloudUser() {
        return Setup.getFromConfigs(Setup.CLOUD_USERNAME);
    }

    public static String getCloudKey() {
        return Setup.getFromConfigs(Setup.CLOUD_KEY);
    }

    public static String getRemoteDriverGridPort() {
        return Setup.getFromConfigs(Setup.REMOTE_WEBDRIVER_GRID_PORT);
    }

    public static String getRemoteDriverGridHostName() {
        return Setup.getFromConfigs(Setup.REMOTE_WEBDRIVER_GRID_HOST_NAME);
    }

    public static int getMaxNumberOfAppiumDrivers() {
        return Setup.getIntegerValueFromConfigs(Setup.MAX_NUMBER_OF_APPIUM_DRIVERS);
    }

    public static int getMaxNumberOfWebDrivers() {
        return Setup.getIntegerValueFromConfigs(Setup.MAX_NUMBER_OF_WEB_DRIVERS);
    }

    public static boolean isVisualTestingEnabled() {
        return Setup.getBooleanValueFromConfigs(Setup.IS_VISUAL);
    }

    public static boolean shouldFailTestOnVisualDifference() {
        return Setup.getBooleanValueFromConfigs(Setup.FAIL_TEST_ON_VISUAL_DIFFERENCE);
    }

    public static String getFromEnvironmentConfiguration(String key) {
        try {
            return Setup.getFromEnvironmentConfiguration(key);
        } catch (NullPointerException npe) {
            throw new InvalidTestDataException(String.format(INVALID_KEY_MESSAGE, key), npe);
        }
    }

    public static String getTestData(String key) {
        try {
            return Setup.getTestDataValueAsStringForEnvironmentFor(key);
        } catch (NullPointerException npe) {
            throw new InvalidTestDataException(String.format(INVALID_KEY_MESSAGE, key), npe);
        }
    }

    public static Map<String, Object> getTestDataAsMap(String key) {
        try {
            return Setup.getTestDataAsMapForEnvironmentFor(key);
        } catch (NullPointerException npe) {
            throw new InvalidTestDataException(String.format(INVALID_KEY_MESSAGE, key), npe);
        }
    }

    public static void main(String[] args) {
        String logDir = System.getProperty("LOG_DIR");
        System.setProperty("OUTPUT_DIRECTORY", logDir);
        LOGGER.info("teswiz Runner");
        LOGGER.info("Provided parameters:");
        for (String arg : args) {
            LOGGER.info("\t" + arg);
        }
        if (args.length != 3) {
            throw new InvalidTestDataException(
                    "Expected following parameters: 'String configFilePath, String " +
                    "stepDefDirName, String " + "featuresDirName");
        }
        new Runner(args[0], args[1], args[2]);
    }

    public static TestExecutionContext getTestExecutionContext(long threadId) {
        return SessionContext.getTestExecutionContext(threadId);
    }

    public static SoftAssertions getSoftAssertion(long threadId) {
        return (SoftAssertions) getTestExecutionContext(threadId).getTestState(
                TEST_CONTEXT.SOFT_ASSERTIONS);
    }

    public static Driver setCurrentDriverForUser(String userPersona, Platform forPlatform,
                                                 TestExecutionContext context) {
        return Drivers.setDriverFor(userPersona, forPlatform, context);
    }

    public static Platform fetchPlatform(long threadId) {
        String userPersona = getTestExecutionContext(threadId).getTestStateAsString(
                TEST_CONTEXT.CURRENT_USER_PERSONA);
        return Drivers.getPlatformForUser(userPersona);
    }

    public static String getTargetEnvironment() {
        return Setup.getFromConfigs(Setup.TARGET_ENVIRONMENT);
    }

    public static String getBaseURLForWeb() {
        String baseUrlToBeUsed = Setup.getFromConfigs(Setup.BASE_URL_FOR_WEB);
        LOGGER.info(String.format("Using baseUrl KEY in configs for web: '%s'", baseUrlToBeUsed));
        Object updatedBaseUrlForWeb = getTestExecutionContext(Thread.currentThread().getId()).getTestState(TEST_CONTEXT.UPDATED_BASE_URL_FOR_WEB);
        if (null != updatedBaseUrlForWeb) {
            baseUrlToBeUsed = updatedBaseUrlForWeb.toString();
            LOGGER.info(String.format("Using updated baseUrl key for web: '%s'", baseUrlToBeUsed));
        }
        return baseUrlToBeUsed;
    }

    public static String getAppPackageName() {
        return Setup.getFromConfigs(Setup.APP_PACKAGE_NAME);
    }

    public static boolean isRunningInCI() {
        return Setup.getBooleanValueFromConfigs(Setup.RUN_IN_CI);
    }

    public static boolean isCLI() {
        return Runner.getPlatform().equals(Platform.cli);
    }

    public static boolean isPDF() {
        return Runner.getPlatform().equals(Platform.pdf);
    }

    public static boolean isAPI() {
        return Runner.getPlatform().equals(Platform.api);
    }

    public static String getBrowser() {
        return Setup.getFromConfigs(Setup.BROWSER);
    }

    public static String getProxyURL() {
        String proxyURL = Setup.getFromConfigs(Setup.PROXY_URL);
        LOGGER.info(String.format("Using proxyURL: %s", proxyURL));
        return proxyURL;
    }

    public static JSONObject getBrowserConfigFileContents() {
        return getBrowserConfigFileContents(Setup.getFromConfigs(Setup.BROWSER_CONFIG_FILE));
    }

    public static JSONObject getBrowserConfigFileContents(String browserConfigFile) {
        try {
            InputStream inputStream;
            if (browserConfigFile.contains(DEFAULT)) {
                inputStream = Runner.class.getResourceAsStream(Setup.DEFAULT_BROWSER_CONFIG_FILE);
            } else {
                inputStream = Files.newInputStream(Paths.get(browserConfigFile));
            }
            assert inputStream != null;
            return new JSONObject(new JSONTokener(inputStream));
        } catch (Exception e) {
            throw new InvalidTestDataException(
                    String.format("There was a problem while setting browser config file '%s'",
                                  browserConfigFile));
        }
    }

    public static String getBrowserConfigFile() {
        return Setup.getFromConfigs(Setup.BROWSER_CONFIG_FILE);
    }
}
