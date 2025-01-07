package com.znsio.teswiz.runner;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.RectangleSize;
import com.znsio.teswiz.entities.APPLITOOLS;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.exceptions.EnvironmentSetupException;
import com.znsio.teswiz.exceptions.InvalidTestDataException;
import com.znsio.teswiz.tools.JsonFile;
import com.znsio.teswiz.tools.cmd.CommandLineExecutor;
import com.znsio.teswiz.tools.cmd.CommandLineResponse;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.znsio.teswiz.runner.Runner.*;
import static com.znsio.teswiz.tools.OverriddenVariable.*;

class Setup {
    static final String RUN_IN_CI = "RUN_IN_CI";
    static final String BUILD_INITIATION_REASON = "BUILD_INITIATION_REASON";
    static final String TARGET_ENVIRONMENT = "TARGET_ENVIRONMENT";
    static final String REPOSITORY_NAME = "REPOSITORY_NAME";
    static final String BRANCH_NAME = "BRANCH_NAME";
    static final String CAPS = "CAPS";
    static final String CLOUD_KEY = "CLOUD_KEY";
    static final String PLATFORM = "PLATFORM";
    static final String APP_NAME = "APP_NAME";
    static final String BASE_URL_FOR_WEB = "BASE_URL_FOR_WEB";
    static final String IS_VISUAL = "IS_VISUAL";
    static final String FAIL_TEST_ON_VISUAL_DIFFERENCE = "FAIL_TEST_ON_VISUAL_DIFFERENCE";
    static final String BROWSER = "BROWSER";
    static final String CONFIG_FILE = "CONFIG_FILE";
    static final String LAUNCH_NAME = "LAUNCH_NAME";
    static final String APP_PACKAGE_NAME = "APP_PACKAGE_NAME";
    static final String MAX_NUMBER_OF_APPIUM_DRIVERS = "MAX_NUMBER_OF_APPIUM_DRIVERS";
    static final String MAX_NUMBER_OF_WEB_DRIVERS = "MAX_NUMBER_OF_WEB_DRIVERS";
    static final String CLOUD_USERNAME = "CLOUD_USERNAME";
    static final String PROXY_URL = "PROXY_URL";
    static final String REMOTE_WEBDRIVER_GRID_PORT = "REMOTE_WEBDRIVER_GRID_PORT";
    static final String REMOTE_WEBDRIVER_GRID_HOST_NAME = "REMOTE_WEBDRIVER_GRID_HOST_NAME";
    static final String BROWSER_CONFIG_FILE = "BROWSER_CONFIG_FILE";
    static final String DEFAULT_BROWSER_CONFIG_FILE = "/default_browser_config.json";
    static final String PLUGIN = "--plugin";
    static final String APP_PATH = "APP_PATH";
    static final String CLOUD_UPLOAD_APP = "CLOUD_UPLOAD_APP";
    static final String EXECUTED_ON = "EXECUTED_ON";
    static final String LOG_DIR = "LOG_DIR";
    static final String PARALLEL = "PARALLEL";
    static final String TAG = "TAG";
    static final String TAG_FOR_REPORTPORTAL = "TAG_FOR_REPORTPORTAL";
    static final String APP_VERSION = "APP_VERSION";
    static final String REPORTS_DIR = "reports";
    static final String CLOUD_USE_PROXY = "CLOUD_USE_PROXY";
    static final String CLOUD_USE_LOCAL_TESTING = "CLOUD_USE_LOCAL_TESTING";
    static final String HOST_NAME = "HOST_NAME";
    static final String IS_FAILING_TEST_SUITE = "IS_FAILING_TEST_SUITE";
    static final String SET_HARD_GATE = "SET_HARD_GATE";
    private static final Map<String, String> configs = new HashMap<>();
    private static final Map<String, Boolean> configsBoolean = new HashMap<>();
    private static final Map<String, Integer> configsInteger = new HashMap<>();
    private static final String RP_DESCRIPTION = "RP_DESCRIPTION";
    private static final String RP_DEFAULT_DESCRIPTION = "End-2-End scenarios";
    private static Map<String, Map> loadedCapabilityFile;
    private static final String CHROME = "chrome";
    private static final String TEMP_DIRECTORY = "temp";
    private static final int DEFAULT_PARALLEL = 1;
    private static final ArrayList<String> CUKE_ARGS = new ArrayList<>();
    private static final String LOG_PROPERTIES_FILE = "LOG_PROPERTIES_FILE";
    private static final String DEFAULT_LOG_DIR = "target";
    private static final String LOCAL = "LOCAL";
    private static final String ENVIRONMENT_CONFIG_FILE = "ENVIRONMENT_CONFIG_FILE";
    private static final String PROXY_KEY = "PROXY_KEY";
    private static final String TEST_DATA_FILE = "TEST_DATA_FILE";
    static final String APPLITOOLS_CONFIGURATION = "APPLITOOLS_CONFIGURATION";
    private static final String LAUNCH_NAME_SUFFIX = "LAUNCH_NAME_SUFFIX";
    private static final String REMOTE_WEBDRIVER_GRID_PORT_KEY = "REMOTE_WEBDRIVER_GRID_PORT_KEY";
    private static final Logger LOGGER = LogManager.getLogger(Setup.class.getName());
    private static final String DEFAULT_LOG_PROPERTIES_FILE = "/defaultLog4j.properties";
    private static final String DEFAULT_WEBDRIVER_GRID_PORT = "4444";
    private static final String DEFAULT_WEBDRIVER_GRID_HOST_NAME = "localhost";
    static final String BUILD_ID = "BUILD_ID";
    private static Map<String, Map> environmentConfiguration;
    private static Map<String, Map> testDataForEnvironment;
    private static Map applitoolsConfiguration = new HashMap<>();
    private static Properties properties;
    private static String configFilePath;
    private static Platform currentPlatform = Platform.android;
    private static final String AND_NOT_WIP = " and not @wip";
    private static final String AND_NOT_FAILING = " and not @failing";
    private static final String AND_FAILING = " and @failing";

    private Setup() {
        LOGGER.debug("Setup - private constructor");
    }

    static void load(String providedConfigFilePath) {
        configFilePath = providedConfigFilePath;
        reset();
        properties = loadProperties(configFilePath);
    }

    private static void reset() {
        properties = null;
        configs.clear();
        configsBoolean.clear();
        configsInteger.clear();
        applitoolsConfiguration.clear();
    }

    @NotNull
    static Properties loadProperties(String configFile) {
        final Properties properties;
        try (InputStream input = new FileInputStream(configFile)) {
            properties = new Properties();
            properties.load(input);
        } catch (IOException ex) {
            throw new InvalidTestDataException("Config file not found, or unable to read it", ex);
        }
        return properties;
    }

    @NotNull
    static String getCurlProxyCommand() {
        String curlProxyCommand = "";
        if (Boolean.TRUE.equals(configsBoolean.get(CLOUD_USE_PROXY))) {
            curlProxyCommand = " --proxy " + configs.get(PROXY_URL);
        }
        return curlProxyCommand;
    }

    static List<String> getExecutionArguments() {
        loadAndUpdateConfigParameters(configFilePath);

        setupDirectories();
        setLogPropertiesFile();
        setBrowserConfigFilePath();

        System.setProperty(LOG_DIR, configs.get(LOG_DIR));
        LOGGER.info(String.format("Runner called from user directory: %s", Runner.USER_DIRECTORY));
        printLoadedConfigProperties(configFilePath);

        environmentConfiguration = loadEnvironmentConfiguration(configs.get(TARGET_ENVIRONMENT));
        testDataForEnvironment = loadTestDataForEnvironment(configs.get(TARGET_ENVIRONMENT));
        if (NOT_SET.equalsIgnoreCase(configs.get(CAPS))) {
            loadedCapabilityFile = new HashMap<>();
        } else {
            loadCapabilitiesFile(configs.get(CAPS));
        }
        setupExecutionEnvironment();

        printStringConfigsMap();
        printBooleanConfigsMap();
        printIntegerConfigsMap();

        return CUKE_ARGS;
    }

    private static void loadCapabilitiesFile(String capabilityFile) {
        loadedCapabilityFile = JsonFile.loadJsonFile(capabilityFile);
    }

    static void loadAndUpdateConfigParameters(String configFilePath) {
        configs.put(CONFIG_FILE, configFilePath);
        buildMapOfRequiredProperties();
    }

    private static void setupDirectories() {
        List<String> files = listOfDirectoriesToCreate();
        LOGGER.info(String.format("Create Directories: %s", files));
        for (String file : files) {
            LOGGER.info(String.format("\tCreating directory: %s", file));
            try {
                FileUtils.forceMkdir(new java.io.File(file));
            } catch (IOException e) {
                throw new EnvironmentSetupException("Unable to cleanup & setup directories", e);
            }
        }
    }

    private static void setLogPropertiesFile() {
        File file;
        try {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            if (properties.containsKey(LOG_PROPERTIES_FILE)) {
                Path logFilePath = Paths.get(properties.get(LOG_PROPERTIES_FILE).toString());
                configs.put(LOG_PROPERTIES_FILE, logFilePath.toString());
                file = new File(LOG_PROPERTIES_FILE);
            } else {
                configs.put(LOG_PROPERTIES_FILE, DEFAULT_LOG_PROPERTIES_FILE);
                file = new File(DEFAULT_LOG_PROPERTIES_FILE);
            }
            context.setConfigLocation(file.toURI());
        } catch (Exception e) {
            throw new InvalidTestDataException("There was a problem while setting log properties file");
        }
    }

    private static void setBrowserConfigFilePath() {
        if (properties.containsKey(BROWSER_CONFIG_FILE)) {
            Path browserConfigFilePath = Paths.get(properties.get(BROWSER_CONFIG_FILE).toString());
            configs.put(BROWSER_CONFIG_FILE, browserConfigFilePath.toString());
            LOGGER.info(String.format("Using the provided BROWSER_CONFIG_FILE: '%s'", browserConfigFilePath));
        } else {
            configs.put(BROWSER_CONFIG_FILE, DEFAULT_BROWSER_CONFIG_FILE);
            LOGGER.info(String.format("Using the default BROWSER_CONFIG_FILE: '%s'", DEFAULT_BROWSER_CONFIG_FILE));
        }
    }

    private static void printLoadedConfigProperties(String configFilePath) {
        LOGGER.info(String.format("Loaded property file: %s", configFilePath));
        final String[] propVars = {""};
        properties.forEach((k, v) -> propVars[0] += ("\t" + k + ":" + v + "\n"));
        LOGGER.info(String.format("Config properties: %s:%n%s", configFilePath, propVars[0]));
    }

    private static Map<String, Map> loadEnvironmentConfiguration(String environment) {
        String envConfigFile = configs.get(ENVIRONMENT_CONFIG_FILE);
        LOGGER.info(String.format("Loading environment configuration from ENVIRONMENT_CONFIG_FILE: %s for " + "environment: %s", envConfigFile, environment));
        return (NOT_SET.equalsIgnoreCase(envConfigFile)) ? new HashMap<>() : JsonFile.getNodeValueAsMapFromJsonFile(environment, envConfigFile);
    }

    private static Map<String, Map> loadTestDataForEnvironment(String environment) {
        String testDataFile = configs.get(TEST_DATA_FILE);
        LOGGER.info(String.format("Loading test data from TEST_DATA_FILE: %s for environment: %s", testDataFile, environment));
        return (NOT_SET.equalsIgnoreCase(testDataFile)) ? new HashMap<>() : JsonFile.getNodeValueAsMapFromJsonFile(environment, testDataFile);
    }

    private static void setupExecutionEnvironment() {
        getPlatformTagsAndLaunchName();
        addCucumberPlugsToArgs();
        CUKE_ARGS.addAll(DeviceSetup.setupAndroidExecution());
        CUKE_ARGS.addAll(DeviceSetup.setupIOSExecution());
        CUKE_ARGS.addAll(DeviceSetup.setupWindowsExecution());
        CUKE_ARGS.addAll(setupCucumberListenersForExecution());
        CUKE_ARGS.addAll(setupParallelExecution());

        initialiseApplitoolsConfiguration();

        String rpAttributes = String.format(
                "AutomationBranch:%s; ExecutedOn:%s; Installer:%s; OS:%s; ParallelCount:%d; " +
                "Platform:%s; RunInCI:%s; Tags:%s; TargetEnvironment:%s; Username:%s; " +
                "VisualEnabled:%s; BuildInitiationReason:%s; ",
                configs.get(BRANCH_NAME), configs.get(EXECUTED_ON), configs.get(APP_PATH), OS_NAME,
                configsInteger.get(PARALLEL), currentPlatform.name(), configsBoolean.get(RUN_IN_CI),
                configs.get(TAG_FOR_REPORTPORTAL), configs.get(TARGET_ENVIRONMENT), USER_NAME,
                configsBoolean.get(IS_VISUAL), configs.get(BUILD_INITIATION_REASON));

        if (!configs.get(APP_VERSION).equals(NOT_SET)) {
            rpAttributes += String.format("AppVersion: %s; ", configs.get(APP_VERSION));
        }

        if (!configs.get(BUILD_ID).equals(NOT_SET)) {
            rpAttributes += String.format("BuildId: %s; ", configs.get(BUILD_ID));
        }

        LOGGER.info(String.format("ReportPortal Test Execution Attributes: %s", rpAttributes));

        // properties needed for atd
        System.setProperty(CLOUD_USERNAME, configs.get(CLOUD_USERNAME));
        System.setProperty(CLOUD_KEY, configs.get(CLOUD_KEY));
        System.setProperty(CONFIG_FILE, configs.get(CONFIG_FILE));
        System.setProperty(CAPS, configs.get(CAPS));
        System.setProperty("Platform", currentPlatform.name());
        System.setProperty("atd_" + currentPlatform.name() + "_app_local", configs.get(APP_PATH));
        if (null != configs.get(PROXY_URL)) {
            System.setProperty(PROXY_URL, configs.get(PROXY_URL));
        }

        // properties needed for ReportPortal.io
        System.setProperty("rp.description", configs.get(APP_NAME) + " " + configs.get(RP_DESCRIPTION) + " on " + currentPlatform.name());
        System.setProperty("rp.launch", configs.get(LAUNCH_NAME));
        System.setProperty("rp.attributes", rpAttributes);
    }

    private static Collection<String> setupParallelExecution() {
        List<String> parallelExecutionArgs = new ArrayList<>();
        parallelExecutionArgs.add("--threads");
        if (Setup.getPlatform().equals(Platform.windows) || Setup.getPlatform().equals(Platform.electron)) {
            parallelExecutionArgs.add("1");
        } else {
            parallelExecutionArgs.add(Setup.getIntegerValueAsStringFromConfigs(PARALLEL));
        }
        return parallelExecutionArgs;
    }

    private static void printStringConfigsMap() {
        StringBuilder printString = new StringBuilder("Using string values" + ": \n");
        for (Map.Entry<String, String> entry : Setup.configs.entrySet()) {
            printString.append("\t").append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        LOGGER.info(printString.toString() + Setup.configs);
    }

    private static void printBooleanConfigsMap() {
        StringBuilder printString = new StringBuilder("Using boolean values" + ": \n");
        for (Map.Entry<String, Boolean> entry : Setup.configsBoolean.entrySet()) {
            printString.append("\t").append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        LOGGER.info(printString.toString() + Setup.configsBoolean);
    }

    private static void printIntegerConfigsMap() {
        StringBuilder printString = new StringBuilder("Using integer values" + ": \n");
        for (Map.Entry<String, Integer> entry : Setup.configsInteger.entrySet()) {
            printString.append("\t").append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        LOGGER.info(printString.toString() + Setup.configsInteger);
    }

    private static void buildMapOfRequiredProperties() {
        configs.put(APP_NAME, getOverriddenStringValue(APP_NAME, getStringValueFromPropertiesIfAvailable(APP_NAME, NOT_SET)));
        configs.put(APP_PACKAGE_NAME, getOverriddenStringValue(APP_PACKAGE_NAME, getStringValueFromPropertiesIfAvailable(APP_PACKAGE_NAME, NOT_SET)));
        configs.put(APP_PATH, getOverriddenStringValue(APP_PATH, getStringValueFromPropertiesIfAvailable(APP_PATH, NOT_SET)));
        configs.put(APPLITOOLS_CONFIGURATION, getStringValueFromPropertiesIfAvailable(APPLITOOLS_CONFIGURATION, NOT_SET));
        configs.put(BASE_URL_FOR_WEB, getOverriddenStringValue(BASE_URL_FOR_WEB, getStringValueFromPropertiesIfAvailable(BASE_URL_FOR_WEB, NOT_SET)));
        configs.put(BRANCH_NAME, getOverriddenStringValue(BRANCH_NAME, getStringValueFromPropertiesIfAvailable(BRANCH_NAME, NOT_SET)));
        configs.put(BRANCH_NAME, getOverriddenStringValue(configs.get(BRANCH_NAME), getBranchNameUsingGitCommand()));
        configs.put(BROWSER, getOverriddenStringValue(BROWSER, getStringValueFromPropertiesIfAvailable(BROWSER, CHROME)));
        configs.put(BUILD_ID, getOverriddenStringValue(BUILD_ID, getStringValueFromPropertiesIfAvailable(BUILD_ID, NOT_SET)));
        configs.put(BUILD_ID, getOverriddenStringValue(configs.get(BUILD_ID), NOT_SET));
        configs.put(BUILD_INITIATION_REASON, getOverriddenStringValue(BUILD_INITIATION_REASON, getStringValueFromPropertiesIfAvailable(BUILD_INITIATION_REASON, NOT_SET)));
        configs.put(BUILD_INITIATION_REASON, getOverriddenStringValue(configs.get(BUILD_INITIATION_REASON), NOT_SET));
        configs.put(CAPS, getOverriddenStringValue(CAPS, getStringValueFromPropertiesIfAvailable(CAPS, NOT_SET)));
        configs.put(CLOUD_KEY, getOverriddenStringValue(CLOUD_KEY, getStringValueFromPropertiesIfAvailable(CLOUD_KEY, NOT_SET)));
        configs.put(CLOUD_USERNAME, getOverriddenStringValue(CLOUD_USERNAME, getStringValueFromPropertiesIfAvailable(CLOUD_USERNAME, NOT_SET)));
        configsBoolean.put(CLOUD_UPLOAD_APP, getOverriddenBooleanValue(CLOUD_UPLOAD_APP, getBooleanValueFromPropertiesIfAvailable(CLOUD_UPLOAD_APP, false)));
        configsBoolean.put(CLOUD_USE_PROXY, getOverriddenBooleanValue(CLOUD_USE_PROXY, getBooleanValueFromPropertiesIfAvailable(CLOUD_USE_PROXY, false)));
        configsBoolean.put(CLOUD_USE_LOCAL_TESTING, getOverriddenBooleanValue(CLOUD_USE_LOCAL_TESTING, getBooleanValueFromPropertiesIfAvailable(CLOUD_USE_LOCAL_TESTING, false)));
        configs.put(ENVIRONMENT_CONFIG_FILE, getOverriddenStringValue(ENVIRONMENT_CONFIG_FILE, getStringValueFromPropertiesIfAvailable(ENVIRONMENT_CONFIG_FILE, NOT_SET)));
        configsBoolean.put(IS_VISUAL, getOverriddenBooleanValue(IS_VISUAL, getBooleanValueFromPropertiesIfAvailable(IS_VISUAL, false)));
        configsBoolean.put(FAIL_TEST_ON_VISUAL_DIFFERENCE, getOverriddenBooleanValue(FAIL_TEST_ON_VISUAL_DIFFERENCE, getBooleanValueFromPropertiesIfAvailable(FAIL_TEST_ON_VISUAL_DIFFERENCE, true)));
        configs.put(LOG_DIR, getOverriddenStringValue(LOG_DIR, getStringValueFromPropertiesIfAvailable(LOG_DIR, DEFAULT_LOG_DIR)));
        configsInteger.put(MAX_NUMBER_OF_APPIUM_DRIVERS, getOverriddenIntValue(MAX_NUMBER_OF_APPIUM_DRIVERS, Integer.parseInt(getStringValueFromPropertiesIfAvailable(MAX_NUMBER_OF_APPIUM_DRIVERS, "5"))));
        configsInteger.put(MAX_NUMBER_OF_WEB_DRIVERS, getOverriddenIntValue(MAX_NUMBER_OF_WEB_DRIVERS, Integer.parseInt(getStringValueFromPropertiesIfAvailable(MAX_NUMBER_OF_WEB_DRIVERS, "5"))));
        currentPlatform = Platform.valueOf(getOverriddenStringValue(PLATFORM, getStringValueFromPropertiesIfAvailable(PLATFORM, Platform.android.name())));
        configsInteger.put(PARALLEL, getOverriddenIntValue(PARALLEL, Integer.parseInt(getStringValueFromPropertiesIfAvailable(PARALLEL, String.valueOf(DEFAULT_PARALLEL)))));
        configs.put(PROXY_KEY, getOverriddenStringValue(PROXY_KEY, getStringValueFromPropertiesIfAvailable(PROXY_KEY, PROXY_KEY)));
        configs.put(PROXY_URL, (null != configs.get(PROXY_KEY)) ? getOverriddenStringValue(configs.get(PROXY_KEY)) : configs.put(PROXY_URL, null));

        configs.put(REMOTE_WEBDRIVER_GRID_PORT_KEY, getStringValueFromPropertiesIfAvailable(REMOTE_WEBDRIVER_GRID_PORT, REMOTE_WEBDRIVER_GRID_PORT));
        configs.put(REMOTE_WEBDRIVER_GRID_HOST_NAME, getOverriddenStringValue(REMOTE_WEBDRIVER_GRID_HOST_NAME, getStringValueFromPropertiesIfAvailable(REMOTE_WEBDRIVER_GRID_HOST_NAME, DEFAULT_WEBDRIVER_GRID_HOST_NAME)));
        configs.put(REMOTE_WEBDRIVER_GRID_PORT, getOverriddenStringValue(configs.get(REMOTE_WEBDRIVER_GRID_PORT_KEY), DEFAULT_WEBDRIVER_GRID_PORT));
        configsBoolean.put(RUN_IN_CI, getOverriddenBooleanValue(RUN_IN_CI, getBooleanValueFromPropertiesIfAvailable(RUN_IN_CI, false)));
        configs.put(TAG, getOverriddenStringValue(TAG, getStringValueFromPropertiesIfAvailable(TAG, NOT_SET)));
        configs.put(TARGET_ENVIRONMENT, getOverriddenStringValue(TARGET_ENVIRONMENT, getStringValueFromPropertiesIfAvailable(TARGET_ENVIRONMENT, NOT_SET)));
        configs.put(TEST_DATA_FILE, getOverriddenStringValue(TEST_DATA_FILE, getStringValueFromPropertiesIfAvailable(TEST_DATA_FILE, NOT_SET)));
        configs.put(LAUNCH_NAME_SUFFIX, getOverriddenStringValue(LAUNCH_NAME_SUFFIX, getStringValueFromPropertiesIfAvailable(LAUNCH_NAME_SUFFIX, "")));
        configs.put(RP_DESCRIPTION, getOverriddenStringValue(RP_DESCRIPTION, getStringValueFromPropertiesIfAvailable(RP_DESCRIPTION, RP_DEFAULT_DESCRIPTION)));
        configs.put(APP_VERSION, NOT_SET);
        configs.put(HOST_NAME, getHostMachineName());
        configsBoolean.put(IS_FAILING_TEST_SUITE, getOverriddenBooleanValue(IS_FAILING_TEST_SUITE, false));
        configsBoolean.put(SET_HARD_GATE, getOverriddenBooleanValue(SET_HARD_GATE, false));
    }

    static String getHostMachineName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            LOGGER.debug("Error fetching machine name: " + e.getMessage());
            return NOT_SET;
        }
    }

    private static List<String> listOfDirectoriesToCreate() {
        List<String> files = new ArrayList<>();
        files.add(TEMP_DIRECTORY);
        files.add(configs.get(LOG_DIR));
        return files;
    }

    private static void getPlatformTagsAndLaunchName() {
        LOGGER.info("Get Platform, Tags and LaunchName");
        String launchName = configs.get(APP_NAME);
        if (Boolean.TRUE.equals(configsBoolean.get(RUN_IN_CI))) {
            launchName += " in CI";
        }
        String inferredTags = getCustomTags();
        String providedTags = configs.get(TAG);
        if (providedTags.isEmpty() || providedTags.equals(NOT_SET)) {
            LOGGER.info("\tTags not specified");
            launchName += " - " + currentPlatform;
        } else {
            if (providedTags.contains("multiuser-android-web")) {
                currentPlatform = Platform.android;
                inferredTags = providedTags + AND_NOT_WIP;
                launchName += " - Real User Simulation on Android & Web";
            } else if (providedTags.contains("multiuser-android")) {
                currentPlatform = Platform.android;
                inferredTags = providedTags + AND_NOT_WIP;
                launchName += " - Real User Simulation on multiple Androids";
            } else if (providedTags.contains("multiuser-web")) {
                currentPlatform = Platform.web;
                inferredTags = providedTags + AND_NOT_WIP;
                launchName += " - Real User Simulation on Web";
            } else if (providedTags.contains("multiuser-electron")) {
                currentPlatform = Platform.electron;
                inferredTags = providedTags + AND_NOT_WIP;
                launchName += " - Real User Simulation on Electron";
            } else if (providedTags.contains("multiuser-windows-web")) {
                currentPlatform = Platform.windows;
                inferredTags = providedTags + AND_NOT_WIP;
                launchName += " - Real User Simulation on Windows & Web";
            } else if (providedTags.contains("multiuser-windows-android")) {
                currentPlatform = Platform.windows;
                inferredTags = providedTags + AND_NOT_WIP;
                launchName += " - Real User Simulation on Windows & Android";
            } else if (providedTags.contains("multiuser-iOS")) {
                currentPlatform = Platform.iOS;
                inferredTags = providedTags + AND_NOT_WIP;
                launchName += " - Real User Simulation on IOS";
            } else {
                launchName += " - " + currentPlatform;
            }
        }

        inferredTags = getInferredTagsForHardGate(inferredTags);

        launchName += " " + configs.get(LAUNCH_NAME_SUFFIX);

        LOGGER.info(String.format("\tRunning tests with platform: %s and the following tag criteria : %s", currentPlatform, inferredTags));
        LOGGER.info(String.format("\tReportPortal Tests Launch name: %s", launchName));

        configs.put(PLATFORM, currentPlatform.name());
        configs.put(LAUNCH_NAME, launchName);
        configs.put(TAG, inferredTags);
        configs.put(TAG_FOR_REPORTPORTAL, inferredTags.replace(AND_NOT_WIP, ""));
        CUKE_ARGS.add("--tags");
        CUKE_ARGS.add(inferredTags);
    }

    private static String getInferredTagsForHardGate(String inferredTags) {
        boolean isSetHardGate = getBooleanValueFromConfigs(SET_HARD_GATE);
        LOGGER.info("SET_HARD_GATE is set to: " + isSetHardGate);
        if (isSetHardGate) {
            if (getBooleanValueFromConfigs(IS_FAILING_TEST_SUITE)) {
                LOGGER.info("Adding '%s' to tags".formatted(AND_FAILING));
                inferredTags += AND_FAILING;
            } else {
                LOGGER.info("Adding '%s' to tags".formatted(AND_NOT_FAILING));
                inferredTags += AND_NOT_FAILING;
            }
        }
        return inferredTags;
    }

    private static void addCucumberPlugsToArgs() {
        CUKE_ARGS.add(PLUGIN);
        CUKE_ARGS.add("pretty");
        CUKE_ARGS.add(PLUGIN);
        String logDir = configs.get(LOG_DIR);
        CUKE_ARGS.add("html:" + logDir + File.separator + REPORTS_DIR + File.separator + "cucumber-html" + "-report.html");
        CUKE_ARGS.add(PLUGIN);
        CUKE_ARGS.add("junit:" + logDir + File.separator + REPORTS_DIR + File.separator + "cucumber" + "-junit-report.xml");
        CUKE_ARGS.add(PLUGIN);
        CUKE_ARGS.add("json:" + logDir + File.separator + REPORTS_DIR + File.separator + "cucumber-json" + "-report.json");
        CUKE_ARGS.add(PLUGIN);
        CUKE_ARGS.add("message:" + logDir + File.separator + REPORTS_DIR + File.separator + "results" + ".ndjson");
        CUKE_ARGS.add(PLUGIN);
        CUKE_ARGS.add("timeline:" + logDir + File.separator + REPORTS_DIR + File.separator + "timeline");
        System.setProperty("cucumber.publish.quiet", "true");
    }

    private static Collection<String> setupCucumberListenersForExecution() {
        ArrayList<String> cukeArgs = new ArrayList<>();
        cukeArgs.add("--threads");
        cukeArgs.add(String.valueOf(configsInteger.get(PARALLEL)));
        cukeArgs.add(PLUGIN);
        cukeArgs.add("com.znsio.teswiz.listener.CucumberPlatformScenarioListener");
        cukeArgs.add(PLUGIN);
        cukeArgs.add("com.znsio.teswiz.listener.CucumberPlatformScenarioReporterListener");
        return cukeArgs;
    }

    static Map<String, Object> initialiseApplitoolsConfiguration() {
        if (applitoolsConfiguration.isEmpty() && !Runner.isAPI() && !Runner.isCLI()) {
            getApplitoolsConfigFromProvidedConfigFile();
            applitoolsConfiguration.put(APPLITOOLS.SERVER_URL, getServerUrl());
            applitoolsConfiguration.put(APPLITOOLS.APP_NAME, configs.get(APP_NAME));
            applitoolsConfiguration.put(APPLITOOLS.API_KEY, getOverriddenStringValue("APPLITOOLS_API_KEY", String.valueOf(applitoolsConfiguration.get(APPLITOOLS.API_KEY))));
            applitoolsConfiguration.put(BRANCH_NAME, configs.get(BRANCH_NAME));
            applitoolsConfiguration.put(PLATFORM, currentPlatform.name());
            applitoolsConfiguration.put(RUN_IN_CI, String.valueOf(configsBoolean.get(RUN_IN_CI)));
            applitoolsConfiguration.put(TARGET_ENVIRONMENT, configs.get(TARGET_ENVIRONMENT));
            applitoolsConfiguration.put(APPLITOOLS.DEFAULT_MATCH_LEVEL, getMatchLevel());
            applitoolsConfiguration.put(APPLITOOLS.RECTANGLE_SIZE, getViewportSize());
            updateApplitoolsProxyUrl();
            applitoolsConfiguration.put(APPLITOOLS.IS_BENCHMARKING_ENABLED, isBenchmarkingEnabled());
            applitoolsConfiguration.put(APPLITOOLS.DISABLE_BROWSER_FETCHING, isDisableBrowserFetching());
            applitoolsConfiguration.put(APPLITOOLS.BATCH_NAME, setupApplitoolsBatchInfo());
        }
        LOGGER.info(String.format("applitoolsConfiguration: %s", applitoolsConfiguration));
        return applitoolsConfiguration;
    }

    @NotNull
    private static BatchInfo setupApplitoolsBatchInfo() {
        BatchInfo batchInfo = new BatchInfo(configs.get(LAUNCH_NAME) + "-" + configs.get(TARGET_ENVIRONMENT));
        batchInfo.addProperty(APP_NAME, configs.get(APP_NAME));
        batchInfo.addProperty(BRANCH_NAME, configs.get(BRANCH_NAME));
        batchInfo.addProperty(PLATFORM, currentPlatform.name());
        batchInfo.addProperty(RUN_IN_CI, String.valueOf(configsBoolean.get(RUN_IN_CI)));
        batchInfo.addProperty(TARGET_ENVIRONMENT, configs.get(TARGET_ENVIRONMENT));
        batchInfo.addProperty(REPOSITORY_NAME, new File(System.getProperty("user.dir")).getName());
        return batchInfo;
    }

    private static void updateApplitoolsProxyUrl() {
        String providedProxyKey = (String) applitoolsConfiguration.getOrDefault(APPLITOOLS.PROXY_KEY, APPLITOOLS.PROXY_KEY);
        if (providedProxyKey.isBlank()) {
            providedProxyKey = APPLITOOLS.PROXY_KEY;
        }
        applitoolsConfiguration.put(APPLITOOLS.PROXY_KEY, getOverriddenStringValue(APPLITOOLS.PROXY_KEY, providedProxyKey));
        applitoolsConfiguration.put(APPLITOOLS.PROXY_URL, getOverriddenStringValue((String) applitoolsConfiguration.get(APPLITOOLS.PROXY_KEY)));
    }

    private static String getStringValueFromPropertiesIfAvailable(String key, String defaultValue) {
        return properties.getProperty(key, String.valueOf(defaultValue));
    }

    private static String getBranchNameUsingGitCommand() {
        String[] getBranchNameCommand = new String[]{"git rev-parse --abbrev-ref HEAD"};
        CommandLineResponse response = CommandLineExecutor.execCommand(getBranchNameCommand);
        String branchName = response.getStdOut();
        LOGGER.info(String.format("\tBranch name from git command: '%s': '%s'", Arrays.toString(getBranchNameCommand), branchName));
        return branchName;
    }

    private static boolean getBooleanValueFromPropertiesIfAvailable(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }

    private static String getCustomTags() {
        String customTags = "@" + currentPlatform + AND_NOT_WIP;
        String providedTags = configs.get(TAG);
        if (!providedTags.equalsIgnoreCase(NOT_SET)) {
            if (!providedTags.startsWith("@")) {
                providedTags = "@" + providedTags;
            }
            customTags = providedTags + " and " + customTags;
        }
        LOGGER.info("\tComputed tags: " + customTags);
        return customTags;
    }

    static void getApplitoolsConfigFromProvidedConfigFile() {
        String applitoolsConfigurationFileName = configs.get(APPLITOOLS_CONFIGURATION);
        if (applitoolsConfigurationFileName.equals(NOT_SET)) {
            LOGGER.warn("-------------------------------------------------------------");
            LOGGER.warn("Applitools configuration not provided. DISABLE Visual Testing");
            LOGGER.warn("-------------------------------------------------------------");
            configsBoolean.put(IS_VISUAL, false);
        } else {
            LOGGER.info(String.format("Loading Applitools configuration from: %s", applitoolsConfigurationFileName));
            applitoolsConfiguration = JsonFile.loadJsonFile(applitoolsConfigurationFileName);
        }
    }

    private static String getServerUrl() {
        return String.valueOf(applitoolsConfiguration.get(APPLITOOLS.SERVER_URL));
    }

    static MatchLevel getMatchLevel() {
        MatchLevel matchLevel;
        try {
            matchLevel = MatchLevel.valueOf(String.valueOf(applitoolsConfiguration.get(APPLITOOLS.DEFAULT_MATCH_LEVEL)));
        } catch (IllegalArgumentException | NullPointerException e) {
            matchLevel = MatchLevel.STRICT;
        }
        return matchLevel;
    }

    @NotNull
    static RectangleSize getViewportSize() {
        RectangleSize viewportSize = new RectangleSize(1280, 960);
        try {
            String[] viewP = ((String) applitoolsConfiguration.get(APPLITOOLS.VIEWPORT_SIZE)).split("x");
            viewportSize = new RectangleSize(Integer.parseInt(viewP[0]), Integer.parseInt(viewP[1]));
        } catch (NullPointerException e) {
            LOGGER.info(String.format("Unable to get viewport size from Applitools configuration. Using default: " + "1280x960"));
        }
        return viewportSize;
    }

    static boolean isBenchmarkingEnabled() {
        return Boolean.parseBoolean(String.valueOf(applitoolsConfiguration.get(APPLITOOLS.ENABLE_BENCHMARK_PER_VALIDATION)));
    }

    static boolean isDisableBrowserFetching() {
        return Boolean.parseBoolean(String.valueOf(applitoolsConfiguration.get(APPLITOOLS.DISABLE_BROWSER_FETCHING)));
    }

    static void cleanUpExecutionEnvironment() {
        LOGGER.info("cleanUpExecutionEnvironment");
        if (currentPlatform.equals(Platform.android) || currentPlatform.equals(Platform.web) || currentPlatform.equals(Platform.iOS)) {
            if (Boolean.TRUE.equals(configsBoolean.get(RUN_IN_CI))) {
                DeviceSetup.cleanupCloudExecution();
            } else {
                LOGGER.info("Not running in CI. Nothing to cleanup in Execution environment");
            }
        } else {
            LOGGER.info("Not running on android/Web. Nothing to cleanup in Execution environment");
        }
    }

    static String getFromEnvironmentConfiguration(String key) {
        return String.valueOf(environmentConfiguration.get(key));
    }

    static String getFromConfigs(String key) {
        return configs.get(key);
    }

    static String getBooleanValueAsStringFromConfigs(String key) {
        return String.valueOf(configsBoolean.get(key));
    }

    static boolean getBooleanValueFromConfigs(String key) {
        return configsBoolean.get(key);
    }

    static void addToConfigs(String key, String value) {
        configs.put(key, value);
    }

    static int getIntegerValueFromConfigs(String key) {
        return configsInteger.get(key);
    }

    static String getIntegerValueAsStringFromConfigs(String key) {
        return String.valueOf(configsInteger.get(key));
    }

    static void addIntegerValueToConfigs(String key, Integer value) {
        configsInteger.put(key, value);
    }

    static String getTestDataValueAsStringForEnvironmentFor(String key) {
        return String.valueOf(testDataForEnvironment.get(key));
    }

    static Map getTestDataAsMapForEnvironmentFor(String key) {
        return testDataForEnvironment.get(key);
    }

    static Platform getPlatform() {
        return currentPlatform;
    }

    public static Map<String, Map> getLoadedCapabilities() {
        return loadedCapabilityFile;
    }
}
