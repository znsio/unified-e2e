package com.znsio.teswiz.runner;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.TEST_CONTEXT;
import com.znsio.teswiz.exceptions.EnvironmentSetupException;
import com.znsio.teswiz.exceptions.InvalidTestDataException;
import com.znsio.teswiz.tools.JsonFile;
import com.znsio.teswiz.tools.JsonSchemaValidator;
import com.znsio.teswiz.tools.ReportPortalLogger;
import com.znsio.teswiz.tools.cmd.CommandLineExecutor;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import static com.znsio.teswiz.runner.Runner.DEFAULT;
import static com.znsio.teswiz.runner.Setup.CAPS;
import static com.znsio.teswiz.tools.OverriddenVariable.getOverriddenStringValue;

class BrowserDriverManager {
    private static final Logger LOGGER = LogManager.getLogger(BrowserDriverManager.class.getName());
    private static final int MAX_NUMBER_OF_WEB_DRIVERS = Runner.getMaxNumberOfWebDrivers();
    private static final String BROWSER_CONFIG_SCHEMA_FILE = "BrowserConfigSchema.json";
    private static final String ACCEPT_INSECURE_CERTS = "acceptInsecureCerts";
    private static final String VERBOSE_LOGGING = "verboseLogging";
    private static final String MAXIMIZE = "maximize";
    private static final String EXCLUDE_SWITCHES = "excludeSwitches";
    private static final String BROWSER_VERSION = "BROWSER_VERSION";
    private static int numberOfWebDriversUsed = 0;
    private static boolean shouldBrowserBeMaximized = false;
    private static boolean isRunInHeadlessMode = false;

    private BrowserDriverManager() {
        LOGGER.debug("BrowserDriverManager - private constructor");
    }

    @NotNull
    static Driver createWebDriverForUser(String userPersona, String browserName,
                                         Platform forPlatform, TestExecutionContext context) {
        LOGGER.info(String.format(
                "createWebDriverForUser: begin: userPersona: '%s', browserName: '%s', Platform: " + "'%s', Number of WebDrivers: '%d'%n",
                userPersona, browserName, forPlatform.name(), numberOfWebDriversUsed));
        LOGGER.debug("Active thread count: " + Thread.activeCount());

        String baseUrl = getBaseUrl(userPersona);
        String appName = Drivers.getAppNamefor(userPersona);

        checkConnectivityToBaseUrl(baseUrl);
        checkNumberOfWebDriversInstantiated(userPersona, forPlatform);
        String updatedTestName = context.getTestName() + "-" + userPersona;
        String runningOn = Runner.isRunningInCI() ? "CI" : "local";
        context.addTestState(TEST_CONTEXT.WEB_BROWSER_ON, runningOn);
        WebDriver newWebDriver = createNewWebDriver(userPersona, browserName, context);
        loadBaseUrl(baseUrl, newWebDriver);
        Driver currentDriver = new Driver(updatedTestName, forPlatform, userPersona, appName, newWebDriver, isRunInHeadlessMode);
        numberOfWebDriversUsed++;

        LOGGER.info(String.format("createWebDriverForUser: done: userPersona: '%s', Platform: '%s', appName: '%s', Number of WebDrivers: '%d'",
                userPersona, forPlatform.name(), appName, numberOfWebDriversUsed));
        return currentDriver;
    }

    private static void loadBaseUrl(String baseUrl, WebDriver newWebDriver) {
        newWebDriver.get(baseUrl);
        LOGGER.info("Navigated to baseUrl: " + baseUrl);
    }

    private static void checkNumberOfWebDriversInstantiated(String userPersona, com.znsio.teswiz.entities.Platform forPlatform) {
        if (numberOfWebDriversUsed >= MAX_NUMBER_OF_WEB_DRIVERS) {
            throw new InvalidTestDataException(String.format(
                    "Current number of WebDriver instances used: '%d'. " + "Unable to create " +
                            "more" + " than '%d' drivers for user persona: '%s' " + "on platform: '%s'",
                    numberOfWebDriversUsed, MAX_NUMBER_OF_WEB_DRIVERS, userPersona,
                    forPlatform.name()));
        }
    }

    @org.jetbrains.annotations.NotNull
    private static JSONObject getBrowserConfig(TestExecutionContext context) {
        String browserConfigFile = Runner.getBrowserConfigFile();
        String updatedBrowserConfigFileForThisTest = context.getTestStateAsString(TEST_CONTEXT.UPDATED_BROWSER_CONFIG_FILE_FOR_THIS_TEST);
        if (null != updatedBrowserConfigFileForThisTest) {
            browserConfigFile = updatedBrowserConfigFileForThisTest;
            LOGGER.debug("Using UPDATED_BROWSER_CONFIG_FILE_FOR_THIS_TEST (instead of default BROWSER_CONFIG_FILE): " + browserConfigFile);
        }
        LOGGER.info("Using BROWSER_CONFIG_FILE: " + browserConfigFile);
        JSONObject browserConfig = Runner.getBrowserConfigFileContents(browserConfigFile);
        return JsonSchemaValidator.validateJsonFileAgainstSchema(browserConfigFile,
                browserConfig.toString(),
                BROWSER_CONFIG_SCHEMA_FILE);
    }

    private static String getBaseUrl(String userPersona) {
        String providedBaseUrlKey = Runner.getBaseURLForWeb();

        String appName = Drivers.getAppNamefor(userPersona);
        if (!appName.equalsIgnoreCase(DEFAULT)) {
            providedBaseUrlKey = appName.toUpperCase() + "_BASE_URL";
        }
        LOGGER.info(String.format("Using BASE_URL key: %s", providedBaseUrlKey));

        if (null == providedBaseUrlKey) {
            throw new InvalidTestDataException("baseUrl not provided");
        }
        String retrievedBaseUrl = String.valueOf(
                Runner.getFromEnvironmentConfiguration(providedBaseUrlKey));
        retrievedBaseUrl = getOverriddenStringValue(providedBaseUrlKey, retrievedBaseUrl);
        LOGGER.info(String.format("baseUrl: %s", retrievedBaseUrl));
        return retrievedBaseUrl;
    }

    private static void checkConnectivityToBaseUrl(String baseUrl) {
        if (numberOfWebDriversUsed == 0) {
            LOGGER.info(String.format("Check connectivity to baseUrl: '%s'", baseUrl));
            String[] curlCommand = new String[]{"curl -m 60 --insecure -I " + baseUrl};
            CommandLineExecutor.execCommand(curlCommand);
        }
    }

    @NotNull
    private static WebDriver createNewWebDriver(String forUserPersona, String browserName,
                                                TestExecutionContext testExecutionContext) {
        JSONObject browserConfig = getBrowserConfig(testExecutionContext);
        LOGGER.info(String.format("Create new webdriver instance for: %s, on: %s, with browserConfig: %s", forUserPersona, browserName, browserConfig));

        JSONObject browserConfigForBrowserType = browserConfig.getJSONObject(browserName.toLowerCase());
        WebDriver driver = createWebDriver(forUserPersona, testExecutionContext, browserName, browserConfigForBrowserType);

        if (null == driver) {
            throw new EnvironmentSetupException(
                    String.format("Unable to create %s browser driver for user: %s", browserName,
                            forUserPersona));
        }
        LOGGER.info("Webdriver instance created");
        return driver;
    }

    private static WebDriver createWebDriver(String forUserPersona, TestExecutionContext testExecutionContext, String browserName, JSONObject browserConfigForBrowserType) {
        WebDriver driver = null;
        switch (browserName.toLowerCase()) {
            case "chrome":
                driver = createChromeDriver(forUserPersona, testExecutionContext, browserConfigForBrowserType);
                break;
            case "firefox":
                driver = createFirefoxDriver(forUserPersona, testExecutionContext, browserConfigForBrowserType);
                break;
            case "safari":
                driver = createSafariDriver(forUserPersona, testExecutionContext, browserConfigForBrowserType);
                break;
            default:
                throw new InvalidTestDataException(
                        String.format("Browser: '%s' is NOT supported", browserName));
        }
        LOGGER.info("Driver created");
        return driver;
    }

    @NotNull
    private static WebDriver createChromeDriver(String forUserPersona,
                                                TestExecutionContext testExecutionContext,
                                                JSONObject chromeConfiguration) {

        ChromeOptions chromeOptions = getChromeOptions(forUserPersona, testExecutionContext, chromeConfiguration);
        shouldBrowserBeMaximized = chromeConfiguration.getBoolean(MAXIMIZE);

        WebDriver driver = Runner.isRunningInCI() ? createRemoteWebDriver(chromeOptions)
                : new ChromeDriver(chromeOptions);
        LOGGER.info("Chrome driver created");
        Capabilities capabilities =
                Runner.isRunningInCI() ? ((RemoteWebDriver) driver).getCapabilities()
                        : ((ChromeDriver) driver).getCapabilities();
        Drivers.addUserPersonaDriverCapabilities(forUserPersona, capabilities);
        LOGGER.debug("Chrome driver capabilities extracted for further use");
        manageWindowSizeAndHeadlessMode(driver);
        return driver;
    }

    @org.jetbrains.annotations.NotNull
    private static ChromeOptions getChromeOptions(String forUserPersona, TestExecutionContext testExecutionContext, JSONObject chromeConfiguration) {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setAcceptInsecureCerts(chromeConfiguration.getBoolean(ACCEPT_INSECURE_CERTS));

        String browserVersion = getOverriddenStringValue(BROWSER_VERSION,
                chromeConfiguration.getString("browserVersion"));
        if ((Runner.getPlatform().equals(Platform.web) || Runner.getPlatform().equals(Platform.electron)) && !browserVersion.equalsIgnoreCase("latest")) {
            chromeOptions.setBrowserVersion(browserVersion);
        }

        setLogFileName(forUserPersona, testExecutionContext, "Chrome");
        setPreferencesInChromeOptions(chromeConfiguration, chromeOptions);
        setLoggingPrefsInChromeOptions(chromeConfiguration.getBoolean(VERBOSE_LOGGING), chromeOptions);
        setProxyInChromeOptions(chromeOptions, chromeConfiguration);
        setHeadlessInChromeOptions(chromeConfiguration, chromeOptions);
        setEmulationModeInChromeOptions(testExecutionContext, chromeOptions);
        LOGGER.info(String.format("ChromeOptions: %s", chromeOptions.asMap()));
        return chromeOptions;
    }

    private static void addWindowSizeToChromeOptions(JSONObject chromeConfiguration, ChromeOptions chromeOptions) {
        chromeOptions.setBinary(chromeConfiguration.getString("binary"));
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int width = toolkit.getScreenSize().width;
        int height = toolkit.getScreenSize().height;
        chromeOptions.addArguments(String.format("window-size=%s,%s", width, height));
    }

    private static WebDriver createFirefoxDriver(String forUserPersona,
                                                 TestExecutionContext testExecutionContext,
                                                 JSONObject firefoxConfiguration) {

        FirefoxOptions firefoxOptions = getFirefoxOptions(forUserPersona, testExecutionContext, firefoxConfiguration);
        shouldBrowserBeMaximized = firefoxConfiguration.getBoolean(MAXIMIZE);

        WebDriver driver = Runner.isRunningInCI() ? createRemoteWebDriver(firefoxOptions)
                : new FirefoxDriver(firefoxOptions);
        LOGGER.info("Firefox driver created");
        Capabilities capabilities =
                Runner.isRunningInCI() ? ((RemoteWebDriver) driver).getCapabilities()
                        : ((FirefoxDriver) driver).getCapabilities();
        Drivers.addUserPersonaDriverCapabilities(forUserPersona, capabilities);
        LOGGER.debug("Firefox driver capabilities extracted for further use");
        manageWindowSizeAndHeadlessMode(driver);
        return driver;
    }

    @NotNull
    private static FirefoxOptions getFirefoxOptions(String forUserPersona, TestExecutionContext testExecutionContext, JSONObject firefoxConfiguration) {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setAcceptInsecureCerts(firefoxConfiguration.getBoolean(ACCEPT_INSECURE_CERTS));
        setLogFileName(forUserPersona, testExecutionContext, "Firefox");
        setProfileInFirefoxOptions(firefoxConfiguration, firefoxOptions);
        setPreferencesInFirefoxOptions(firefoxConfiguration, firefoxOptions);
        setLoggingPrefsInFirefoxOptions(firefoxConfiguration, firefoxOptions);
        setProxyInFirefoxOptions(firefoxOptions, firefoxConfiguration);
        setHeadlessInFirefoxOptions(firefoxConfiguration, firefoxOptions);
        LOGGER.info(String.format("FirefoxOptions: %s", firefoxOptions.asMap()));
        return firefoxOptions;
    }

    private static void setHeadlessInFirefoxOptions(JSONObject firefoxConfiguration, FirefoxOptions firefoxOptions) {
        JSONObject headlessOptions = firefoxConfiguration.getJSONObject("headlessOptions");
        isRunInHeadlessMode = headlessOptions.getBoolean("headless");
        if (isRunInHeadlessMode) {
            firefoxOptions.addArguments("-headless");
        }
    }

    private static void setProxyInFirefoxOptions(FirefoxOptions firefoxOptions, JSONObject firefoxConfiguration) {
        String proxyUrl = Runner.getProxyURL();
        if (null != proxyUrl) {
            String noProxyFor = getNoProxy(firefoxConfiguration);
            Proxy.ProxyType proxyType = getProxyType(firefoxConfiguration);
            LOGGER.info("Setting Proxy for browser: " + "'" + proxyUrl + "' with noProxy for: '" + noProxyFor + "', and proxyType: '" + proxyType + "'");
            Proxy proxy = new Proxy().setHttpProxy(proxyUrl)
                    .setNoProxy(noProxyFor)
                    .setProxyType(proxyType);
            firefoxOptions.setProxy(proxy);
        }
    }

    private static String getNoProxy(JSONObject jsonObject) {
        return jsonObject.keySet().contains("noProxy") ? jsonObject.getString("noProxy") : null;
    }

    private static void setLoggingPrefsInFirefoxOptions(JSONObject firefoxConfiguration, FirefoxOptions firefoxOptions) {
        boolean enableVerboseLogging = firefoxConfiguration.getBoolean(VERBOSE_LOGGING);
        LoggingPreferences logPrefs = new LoggingPreferences();
        if (enableVerboseLogging) {
            firefoxOptions.setLogLevel(FirefoxDriverLogLevel.DEBUG);
            logPrefs.enable(LogType.BROWSER, Level.ALL);
            logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        } else {
            firefoxOptions.setLogLevel(FirefoxDriverLogLevel.INFO);
            logPrefs.enable(LogType.BROWSER, Level.INFO);
            logPrefs.enable(LogType.PERFORMANCE, Level.INFO);
        }
        firefoxOptions.setCapability("moz:firefoxOptions", logPrefs);
    }

    private static void setPreferencesInFirefoxOptions(JSONObject firefoxConfiguration, FirefoxOptions firefoxOptions) {
        JSONObject preferencesObject = firefoxConfiguration.getJSONObject("preferences");
        preferencesObject.keySet().forEach(key -> {
            if (preferencesObject.get(key) instanceof Boolean) {
                firefoxOptions.addPreference(key, preferencesObject.getBoolean(key));
            } else if (preferencesObject.get(key) instanceof String) {
                firefoxOptions.addPreference(key, preferencesObject.getString(key));
            }
        });

        JSONArray arguments = firefoxConfiguration.getJSONArray("arguments");
        arguments.forEach(argument -> firefoxOptions.addArguments(argument.toString()));
    }

    private static void setProfileInFirefoxOptions(JSONObject firefoxConfiguration, FirefoxOptions firefoxOptions) {
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        JSONObject profileObject = firefoxConfiguration.getJSONObject("firefoxProfile");
        profileObject.keySet().forEach(key -> {
            if (profileObject.get(key) instanceof Boolean) {
                firefoxProfile.setPreference(key, profileObject.getBoolean(key));
            } else if (profileObject.get(key) instanceof String) {
                firefoxProfile.setPreference(key, profileObject.getString(key));
            }
        });
        firefoxOptions.setProfile(firefoxProfile);
    }

    private static void setProxyInChromeOptions(ChromeOptions chromeOptions, JSONObject chromeConfiguration) {
        String proxyUrl = Runner.getProxyURL();
        if (null != proxyUrl) {
            String noProxyFor = getNoProxy(chromeConfiguration);
            Proxy.ProxyType proxyType = getProxyType(chromeConfiguration);
            LOGGER.info("Setting Proxy for browser: " + "'" + proxyUrl + "' with noProxy for: '" + noProxyFor + "', and proxyType: '" + proxyType + "'");
            Proxy proxy = new Proxy().setHttpProxy(proxyUrl).setNoProxy(noProxyFor).setProxyType(proxyType);
            chromeOptions.setCapability("proxy", proxy);
        }
    }

    private static Proxy.ProxyType getProxyType(JSONObject jsonObject) {
        return jsonObject.keySet().contains("proxyType") ? Proxy.ProxyType.valueOf(jsonObject.getString("proxyType").toUpperCase()) : Proxy.ProxyType.MANUAL;
    }

    private static void setPreferencesInChromeOptions(JSONObject chromeConfiguration, ChromeOptions chromeOptions) {
        JSONArray excludeSwitches = chromeConfiguration.getJSONArray(EXCLUDE_SWITCHES);
        List<String> excludeSwitchesAsString = new ArrayList<>();
        excludeSwitches.forEach(
                switchToBeExcluded -> excludeSwitchesAsString.add(switchToBeExcluded.toString()));
        chromeOptions.setExperimentalOption(EXCLUDE_SWITCHES, excludeSwitchesAsString);

        JSONObject excludedSchemes = chromeConfiguration.getJSONObject("excludedSchemes");
        JSONObject preferences = chromeConfiguration.getJSONObject("preferences");
        preferences.put("protocol_handler.excluded_schemes", excludedSchemes);
        chromeOptions.setExperimentalOption("prefs", preferences);
    }

    private static void setEmulationModeInChromeOptions(TestExecutionContext testExecutionContext, ChromeOptions chromeOptions) {
        if (null != testExecutionContext.getTestState(TEST_CONTEXT.MOBILE_EMULATION_DEVICE)) {
            Map<String, String> mobileEmulation = new java.util.HashMap<>();
            mobileEmulation.put("deviceName", testExecutionContext.getTestStateAsString(
                    TEST_CONTEXT.MOBILE_EMULATION_DEVICE));
            chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
        }
    }

    private static void setHeadlessInChromeOptions(JSONObject chromeConfiguration, ChromeOptions chromeOptions) {
        JSONObject headlessOptions = chromeConfiguration.getJSONObject("headlessOptions");
        isRunInHeadlessMode = headlessOptions.getBoolean("headless");

        if (isRunInHeadlessMode) {
            chromeOptions.addArguments("--headless=new");
        }

        JSONArray arguments = chromeConfiguration.getJSONArray("arguments");
        arguments.forEach(argument -> chromeOptions.addArguments(argument.toString()));

        if (isRunInHeadlessMode) {
            JSONArray includeArguments = headlessOptions.getJSONArray("include");
            includeArguments.forEach(argument -> chromeOptions.addArguments(argument.toString()));
        }
    }

    private static void setLoggingPrefsInChromeOptions(boolean enableVerboseLogging, ChromeOptions chromeOptions) {
        LOGGER.info("Set Logging preferences");
        LoggingPreferences logPrefs = new LoggingPreferences();
        if (enableVerboseLogging) {
            System.setProperty("webdriver.chrome.verboseLogging", "true");
            logPrefs.enable(LogType.DRIVER, Level.ALL);
            logPrefs.enable(LogType.BROWSER, Level.ALL);
            logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        } else {
            logPrefs.enable(LogType.DRIVER, Level.INFO);
            logPrefs.enable(LogType.BROWSER, Level.INFO);
            logPrefs.enable(LogType.PERFORMANCE, Level.INFO);
        }
        chromeOptions.setCapability(ChromeOptions.LOGGING_PREFS, logPrefs);
    }

    private static void manageWindowSizeAndHeadlessMode(WebDriver driver) {
        if (!Runner.getPlatform().equals(Platform.electron)) {
            LOGGER.info("Reset browser window size");
            if (shouldBrowserBeMaximized && !isRunInHeadlessMode) {
                driver.manage().window().maximize();
            } else if (isRunInHeadlessMode) {
                driver.manage().window().setSize(new Dimension(1920, 1080));
            }
        }
    }

    private static WebDriver createSafariDriver(String forUserPersona,
                                                TestExecutionContext testExecutionContext,
                                                JSONObject safariConfigurations) {
        SafariOptions safariOptions = getSafariOptions(forUserPersona, testExecutionContext, safariConfigurations);
        shouldBrowserBeMaximized = safariConfigurations.getBoolean(MAXIMIZE);

        WebDriver driver = Runner.isRunningInCI() ? createRemoteWebDriver(safariOptions)
                : new SafariDriver(safariOptions);
        LOGGER.info("Safari driver created");
        Capabilities capabilities =
                Runner.isRunningInCI() ? ((RemoteWebDriver) driver).getCapabilities()
                        : ((SafariDriver) driver).getCapabilities();
        Drivers.addUserPersonaDriverCapabilities(forUserPersona, capabilities);
        LOGGER.debug("Safari driver capabilities extracted for further use");
        // webpush notifications are disabled bydefault in safari , headless is not supported by
        // safari browser and user profiles cannot be set in safari
        manageWindowSizeAndHeadlessMode(driver);
        return driver;
    }

    @NotNull
    private static SafariOptions getSafariOptions(String forUserPersona, TestExecutionContext testExecutionContext, JSONObject safariConfigurations) {
        SafariOptions safariOptions = new SafariOptions();
        safariOptions.setCapability(ACCEPT_INSECURE_CERTS, safariConfigurations.getBoolean(ACCEPT_INSECURE_CERTS));
        setLogFileName(forUserPersona, testExecutionContext, "Safari");
        boolean setUseTechnologyPreview = safariConfigurations.getBoolean("setUseTechnologyPreview");
        // setUseTechnologyPreview is false by default
        safariOptions.setUseTechnologyPreview(setUseTechnologyPreview); //
        setProxyInSafariOptions(safariOptions);
        LOGGER.info(String.format("SafariOptions: %s", safariOptions.asMap()));
        return safariOptions;
    }

    private static void setProxyInSafariOptions(SafariOptions safariOptions) {
        String proxyUrl = Runner.getProxyURL();
        if (null != proxyUrl) {
            LOGGER.info(String.format("%s%s", "Setting Proxy for browser: ", proxyUrl));
            safariOptions.setProxy(new Proxy().setHttpProxy(proxyUrl));
        }
    }

    private static void setLogFileName(String forUserPersona,
                                       TestExecutionContext testExecutionContext,
                                       String browserType) {
        String scenarioLogDir = Runner.USER_DIRECTORY + testExecutionContext.getTestStateAsString(
                TEST_CONTEXT.SCENARIO_LOG_DIRECTORY);
        browserType = browserType.toLowerCase();
        String logFile = String.format("%s%sdeviceLogs%s%s-%s.log", scenarioLogDir, File.separator,
                File.separator, browserType, forUserPersona);

        File file = new File(logFile);
        file.getParentFile().mkdirs();

        String logMessage = String.format("Creating %s logs in file: %s", browserType, logFile);
        LOGGER.info(logMessage);
        ReportPortalLogger.logDebugMessage(logMessage);
        System.setProperty("webdriver." + browserType + ".logfile", logFile);
        Platform plaform = Runner.getPlatform();
        addBrowserLogFileNameFor(forUserPersona, plaform.name(), browserType, logFile);
    }

    private static void addBrowserLogFileNameFor(String userPersona, String forPlatform,
                                                 String browserType, String logFileName) {
        UserPersonaDetails userPersonaDetails = Drivers.getUserPersonaDetails(
                Runner.getTestExecutionContext(Thread.currentThread().getId()));
        userPersonaDetails.addBrowserLogFileNameFor(userPersona, forPlatform, browserType,
                logFileName);
    }

    @NotNull
    private static RemoteWebDriver createRemoteWebDriver(MutableCapabilities capabilities) {
        try {
            String cloudName = Runner.getCloudName();
            String webDriverHubSuffix = "/wd/hub";
            String remoteUrl = "http://" + Runner.getRemoteDriverGridHostName() + ":" + Runner.getRemoteDriverGridPort() + webDriverHubSuffix;
            if (cloudName.equalsIgnoreCase("headspin")) {
                String authenticationKey = Runner.getCloudKey();
                String capabilityFile = System.getProperty(CAPS);
                Map<String, Map> loadedCapabilityFile = JsonFile.loadJsonFile(capabilityFile);
                ArrayList hostMachinesList = (ArrayList) loadedCapabilityFile.get("hostMachines");
                Map hostMachines = (Map) hostMachinesList.get(0);
                String remoteServerURL = String.valueOf(hostMachines.get("machineIP"));
                remoteUrl = remoteServerURL.endsWith("/")
                        ? remoteServerURL + authenticationKey + webDriverHubSuffix
                        : remoteServerURL + "/" + authenticationKey + webDriverHubSuffix;
                remoteUrl = remoteUrl.startsWith("https") ? remoteUrl : "https://" + remoteUrl;
            } else if (cloudName.equalsIgnoreCase("browserstack")) {
                String authenticationUser = Runner.getCloudUser();
                String authenticationKey = Runner.getCloudKey();
                remoteUrl = "https://" + authenticationUser + ":" + authenticationKey + "@hub.browserstack.com/wd/hub";
                capabilities = BrowserStackSetup.updateBrowserStackCapabilities(capabilities);
            }

            LOGGER.info(String.format("Starting RemoteWebDriver using url: %s with capabilities: '%s'", remoteUrl, capabilities));
            RemoteWebDriver remoteWebDriver = new RemoteWebDriver(new URL(remoteUrl), capabilities);
            LOGGER.info(String.format("RemoteWebDriver created using url: %s", remoteUrl));
            return remoteWebDriver;
        } catch (MalformedURLException e) {
            throw new EnvironmentSetupException("Unable to create a new RemoteWebDriver", e);
        }
    }

    static void closeWebDriver(String userPersona,
                               @NotNull
                               Driver driver) {
        String browserNameForUser = Drivers.getBrowserNameForUser(userPersona);
        String platformName = Runner.getPlatform().name();
        String logFileName = getBrowserLogFileNameFor(userPersona, platformName, browserNameForUser);
        fetchBrowserLogsFromRemoteBrowser(driver, logFileName);
        --numberOfWebDriversUsed;
        LOGGER.info(String.format("Reduced numberOfWebDriversUsed: %d", numberOfWebDriversUsed));
        String logMessage = String.format("Browser logs for user: %s" + "%nlogFileName: %s", userPersona, logFileName);
        LOGGER.info(logMessage);
        ReportPortalLogger.attachFileInReportPortal(logMessage, new File(logFileName));

        WebDriver webDriver = driver.getInnerDriver();
        if (null == webDriver) {
            logMessage = String.format("Strange. But WebDriver for user '%s' already closed", userPersona);
            LOGGER.info(logMessage);
        } else {
            logMessage = String.format("Closing WebDriver for user: '%s'", userPersona);
            LOGGER.info(logMessage);
            ReportPortalLogger.logDebugMessage(logMessage);
            quitWebDriver(webDriver);
        }
    }

    private static void writeLogEntriesToFile(WebDriver driver, String logFileName, String logType, String logLabel) {
        String fileName = logFileName + "-" + logLabel;
        try (FileWriter writer = new FileWriter(fileName, true)) {
            LogEntries logs = driver.manage().logs().get(logType);
            writer.write("\n=== " + logLabel + " Logs ===\n");
            for (LogEntry entry : logs) {
                writer.write("[" + entry.getLevel() + "] " + entry.getMessage() + "\n");
            }
            writer.flush();
            LOGGER.info("Browser log saved in: " + fileName);
        } catch (NoSuchSessionException | IOException | UnsupportedCommandException e) {
            LOGGER.error("Error writing " + logLabel + " log for file: " + logFileName + ": Error: " + e.getMessage());
        }
    }

    private static void fetchBrowserLogsFromRemoteBrowser(@NotNull Driver driver, String logFileName) {
        if (Runner.isRunningInCI()) {
            LOGGER.info("Fetch browser logs from docker and save it");
            writeLogEntriesToFile(driver.getInnerDriver(), logFileName, LogType.BROWSER, "Browser");
            writeLogEntriesToFile(driver.getInnerDriver(), logFileName, LogType.PERFORMANCE, "Performance");
        }
    }

    private static void quitWebDriver(WebDriver webDriver) {
        try {
            webDriver.quit();
        } catch (NoSuchSessionException e) {
            LOGGER.debug("Exception while quiting WebDriver", e);
        }
    }

    private static String getBrowserLogFileNameFor(String userPersona, String forPlatform,
                                                   String browserType) {
        UserPersonaDetails userPersonaDetails = Drivers.getUserPersonaDetails(
                Runner.getTestExecutionContext(Thread.currentThread().getId()));
        return userPersonaDetails.getBrowserLogFileNameFor(userPersona, forPlatform, browserType);
    }

    @NotNull
    static Driver createElectronDriverForUser(String userPersona, String browserName,
                                              Platform forPlatform, TestExecutionContext context) {
        LOGGER.info(String.format(
                "createElectronDriverForUser: begin: userPersona: '%s', browserName: '%s', Platform: " + "'%s', Number of ElectronDrivers: '%d'%n",
                userPersona, browserName, forPlatform.name(), numberOfWebDriversUsed));
        LOGGER.debug("Active thread count: " + Thread.activeCount());

        String baseUrl = getBaseUrl(userPersona);
        String appName = Drivers.getAppNamefor(userPersona);

        checkConnectivityToBaseUrl(baseUrl);
        checkNumberOfWebDriversInstantiated(userPersona, forPlatform);
        String updatedTestName = context.getTestName() + "-" + userPersona;
        String runningOn = Runner.isRunningInCI() ? "CI" : "local";
        context.addTestState(TEST_CONTEXT.ELECTRON_BROWSER_ON, runningOn);
        JSONObject browserConfig = getBrowserConfig(context);
        LOGGER.info(String.format("Create new electrondriver instance for: %s, on: %s, with browserConfig: %s", userPersona, browserName, browserConfig));
        JSONObject browserConfigForBrowserType = browserConfig.getJSONObject(browserName.toLowerCase());
        ChromeOptions chromeOptions = getChromeOptions(userPersona, context, browserConfigForBrowserType);
        addWindowSizeToChromeOptions(browserConfigForBrowserType, chromeOptions);
        chromeOptions.setExperimentalOption("w3c", true);

        shouldBrowserBeMaximized = browserConfigForBrowserType.getBoolean(MAXIMIZE);

        WebDriverManager webDriverManager = WebDriverManager.chromedriver()
                                                    .clearDriverCache()
                                                    .driverVersion(getOverriddenStringValue(BROWSER_VERSION,browserConfigForBrowserType.getString("browserVersion")));
        String proxyUrl = Runner.getProxyURL();
        if (null != proxyUrl) {
            LOGGER.info(String.format("Adding proxy: %s to WebDriverManager", proxyUrl));
            webDriverManager = webDriverManager.proxy(String.valueOf(new Proxy().setHttpProxy(proxyUrl)));
        }
        webDriverManager.setup();

        WebDriver driver = Runner.isRunningInCI() ? createRemoteWebDriver(chromeOptions)
                : new ChromeDriver(chromeOptions);
        LOGGER.info("Electron driver created");
        Capabilities capabilities =
                Runner.isRunningInCI() ? ((RemoteWebDriver) driver).getCapabilities()
                        : ((ChromeDriver) driver).getCapabilities();
        Drivers.addUserPersonaDriverCapabilities(userPersona, capabilities);
        LOGGER.debug("Electron driver capabilities extracted for further use");

        if (browserConfigForBrowserType.getBoolean("electronAppLoadingPage")) {
            handleWindowForElectronApplication(driver, browserConfigForBrowserType);
        }

        Driver currentDriver = new Driver(updatedTestName, forPlatform, userPersona, appName, driver, isRunInHeadlessMode);
        numberOfWebDriversUsed++;
        LOGGER.info(String.format("createElectronDriverForUser: done: userPersona: '%s', Platform: '%s', appName: '%s', Number of ElectronDrivers: '%d'",
                userPersona, forPlatform.name(), appName, numberOfWebDriversUsed));
        return currentDriver;
    }

    private static void handleWindowForElectronApplication(WebDriver driver, JSONObject browserConfigForBrowserType) {
        LOGGER.info("Handle loading window for electron application");
        Set<String> windowHandles = new java.util.HashSet<>();
        String parentWindowHandle = driver.getWindowHandle();
        LOGGER.info(String.format("Current window handle %s", parentWindowHandle));
        long startTime = System.currentTimeMillis();

        while (windowHandles.size() <= 1) {
            windowHandles = driver.getWindowHandles();
            if (System.currentTimeMillis() - startTime > browserConfigForBrowserType.getInt("electronAppLoadTime") * 1000L) {
                throw new TimeoutException("No Loading page window available, disable the electronAppLoadingPage parameter");
            }
        }

        LOGGER.info(String.format("All the window handles available %s", windowHandles));
        for (String handle : windowHandles) {
            if (!handle.equals(parentWindowHandle)) {
                driver.switchTo().window(handle);
                LOGGER.info(String.format("Current window handle after switching %s", driver.getWindowHandle()));
                break;
            }
        }
    }
}
