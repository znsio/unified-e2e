package com.znsio.teswiz.runner;

import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.FileLocations;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.TEST_CONTEXT;
import com.znsio.teswiz.exceptions.EnvironmentSetupException;
import com.znsio.teswiz.exceptions.InvalidTestDataException;
import com.znsio.teswiz.tools.ReportPortalLogger;
import com.znsio.teswiz.tools.cmd.CommandLineExecutor;
import com.znsio.teswiz.tools.cmd.CommandLineResponse;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.appmanagement.ApplicationState;
import io.appium.java_client.ios.IOSDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.logging.LogEntries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static com.znsio.teswiz.listener.CucumberScenarioListener.createFile;
import static com.znsio.teswiz.runner.Runner.DEFAULT;
import static com.znsio.teswiz.runner.Runner.getCloudName;
import static com.znsio.teswiz.runner.Setup.CAPS;

public class AppiumDriverManager {
    private static final int MAX_NUMBER_OF_APPIUM_DRIVERS = Runner.getMaxNumberOfAppiumDrivers();
    private static final List additionalDevices = new ArrayList<>();
    private static final Logger LOGGER = LogManager.getLogger(AppiumDriverManager.class.getName());
    private static final String CAPABILITIES = "CAPABILITIES: ";
    private static int numberOfAppiumDriversUsed = 0;

    public AppiumDriverManager() {
        LOGGER.debug("AppiumDriverManager - private constructor");
    }

    @NotNull
    static Driver createAndroidDriverForUser(String userPersona, Platform forPlatform, TestExecutionContext context) {
        LOGGER.info(String.format("createAndroidDriverForUser: begin: userPersona: '%s', Platform: '%s', Number of " + "appiumDrivers: '%d'%n", userPersona, forPlatform.name(), numberOfAppiumDriversUsed));
        Driver currentDriver;
        if (numberOfAppiumDriversUsed == MAX_NUMBER_OF_APPIUM_DRIVERS) {
            throw new InvalidTestDataException(String.format("Unable to create more than '%d' drivers for user persona: '%s' on platform: " + "'%s'", numberOfAppiumDriversUsed, userPersona, forPlatform.name()));
        }

        currentDriver = setupOrCreateAndroidDriver(userPersona, forPlatform, context);
        LOGGER.info(String.format("createAndroidDriverForUser: done: userPersona: '%s', Platform: '%s', Number of " + "appiumDrivers: '%d'%n", userPersona, forPlatform.name(), numberOfAppiumDriversUsed));
        disableNotificationsAndToastsOnDevice(currentDriver, context.getTestStateAsString(TEST_CONTEXT.DEVICE_ON), (String) Drivers.getCapabilitiesFor(userPersona).getCapability("udid"));
        return currentDriver;
    }

    @NotNull
    private static Driver setupOrCreateAndroidDriver(String userPersona, Platform forPlatform, TestExecutionContext context) {
        String appName = Drivers.getAppNamefor(userPersona);
        File capabilityFileToUseForDriverCreation = getCapabilityFileToUseForDriverCreation(appName);
        Driver currentDriver;
        if (numberOfAppiumDriversUsed == 0) {
            currentDriver = setupFirstAppiumDriver(userPersona, forPlatform, context, appName);
        } else {
            currentDriver = createNewAppiumDriver(userPersona, forPlatform, context, appName, capabilityFileToUseForDriverCreation);
        }
        numberOfAppiumDriversUsed++;
        return currentDriver;
    }

    @NotNull
    private static File getCapabilityFileToUseForDriverCreation(String appName) {
        String capabilityFileNameToUseForDriverCreation = System.getProperty(CAPS);

        if (!appName.equalsIgnoreCase(DEFAULT)) {
            String capabilityFileDirectory = new File(capabilityFileNameToUseForDriverCreation).getParent();
            capabilityFileNameToUseForDriverCreation = capabilityFileDirectory + File.separator + (appName + "_capabilities.json");
        }
        File capabilityFileToUseForDriverCreation = new File(capabilityFileNameToUseForDriverCreation);
        LOGGER.info(String.format("capabilityFileToUseForDriverCreation: %s", capabilityFileToUseForDriverCreation.getAbsolutePath()));
        return capabilityFileToUseForDriverCreation;
    }

    @NotNull
    private static Driver createNewAppiumDriver(String userPersona, Platform forPlatform, TestExecutionContext context, String appName, File capabilityFileToUseForDriverCreation) {
        Driver currentDriver;
        try {
            // TODO
            AppiumDriver appiumDriver = TODO_createAppiumDriver(userPersona, capabilityFileToUseForDriverCreation);


            String scenarioDirectory = context.getTestStateAsString("scenarioDirectory");
            Integer scenarioRunCount = (Integer) context.getTestState("scenarioRunCount");
            String deviceLogFileName = startDataCapture(scenarioRunCount, scenarioDirectory);
            addDeviceLogFileNameFor(userPersona, forPlatform.name(), deviceLogFileName);

            currentDriver = new Driver(context.getTestName() + "-" + userPersona, forPlatform, userPersona, appName, appiumDriver);
            Capabilities appiumDriverCapabilities = appiumDriver.getCapabilities();
            LOGGER.info(CAPABILITIES + appiumDriverCapabilities);
            appiumDriverCapabilities.getCapabilityNames().forEach(key -> LOGGER.info(String.format("\t%s:: %s", key, appiumDriverCapabilities.getCapability(key))));

            Drivers.addUserPersonaDriverCapabilities(userPersona, appiumDriverCapabilities);
        } catch (Exception e) {
            throw new EnvironmentSetupException(String.format("Unable to create Android driver '#%d' for user persona: '%s'", numberOfAppiumDriversUsed, userPersona));
        }
        return currentDriver;
    }

    private static AppiumDriver TODO_createAppiumDriver(String userPersona, File capabilityFileToUseForDriverCreation) {
//        return new com.appium.manager.AppiumDriverManager().startAppiumDriverInstance(userPersona, capabilityFileToUseForDriverCreation.getAbsolutePath());
        return null;
    }

    private static String startDataCapture(Integer scenarioRunCount, String deviceLogFileDirectory) {
        String fileName = String.format("/run-%s", scenarioRunCount);
//        if (AppiumDeviceManager.getAppiumDevice().getPlatformName().equalsIgnoreCase("android")) {
//            try {
//                fileName = String.format("/%s-run-%s", AppiumDeviceManager.getAppiumDevice().getUdid(), scenarioRunCount);
//                File logFile = createFile(deviceLogFileDirectory + FileLocations.DEVICE_LOGS_DIRECTORY, fileName);
//                fileName = logFile.getAbsolutePath();
//                LOGGER.debug("Capturing device logs here: " + fileName);
//                PrintStream logFileStream = null;
//                logFileStream = new PrintStream(logFile);
//                LogEntries logcatOutput = com.appium.manager.AppiumDriverManager.getDriver().manage().logs().get("logcat");
//                StreamSupport.stream(logcatOutput.spliterator(), false).forEach(logFileStream::println);
//            } catch (FileNotFoundException e) {
//                LOGGER.warn("ERROR in getting logcat. Skipping logcat capture");
//            }
//        }
        return fileName;
    }

    @NotNull
    private static Driver setupFirstAppiumDriver(String userPersona, Platform forPlatform, TestExecutionContext context, String appName) {
        Driver currentDriver;
        AppiumDriver appiumDriver = (AppiumDriver) context.getTestState(TEST_CONTEXT.APPIUM_DRIVER);
        // DriverSession deviceInfo = (DriverSession) context.getTestState(TEST_CONTEXT.DEVICE_INFO);
        // Do not add the device info to additionalDevices for the driver created by ATD
        // additionalDevices.add(deviceInfo);
        Capabilities appiumDriverCapabilities = appiumDriver.getCapabilities();
        if (PluginClI.getInstance().isCloudExecution()) {
            context.addTestState(TEST_CONTEXT.DEVICE_ON, getCloudName());
        } else {
            context.addTestState(TEST_CONTEXT.DEVICE_ON, "localDevice");
        }
        LOGGER.info(CAPABILITIES + appiumDriverCapabilities);
        Drivers.addUserPersonaDriverCapabilities(userPersona, appiumDriverCapabilities);
        Drivers.addUserPersonaDeviceLogFileName(userPersona, context.getTestStateAsString("deviceLog"), forPlatform);
        currentDriver = new Driver(context.getTestName() + "-" + userPersona, forPlatform, userPersona, appName, appiumDriver);
        return currentDriver;
    }

    private static void disableNotificationsAndToastsOnDevice(Driver currentDriver, String deviceOn, String udid) {
        if (Runner.isRunningInCI()) {
            //            disableNotificationsForDeviceInDeviceFarm(currentDriver, deviceOn);
        } else {
            disableNotificationsForLocalDevice(udid);
        }
    }

    private static void disableNotificationsForLocalDevice(String udid) {
        String[] disableToastsCommand = new String[]{"adb", "-s", udid, "shell", "appops", "set", Runner.getAppPackageName(), "TOAST_WINDOW", "deny"};
        String[] disableNotificationsCommand = new String[]{"adb", "-s", udid, "shell", "settings", "put", "global", "heads_up_notifications_enabled", "0"};

        CommandLineResponse disableToastsCommandResponse = CommandLineExecutor.execCommand(disableToastsCommand);
        LOGGER.info(String.format("disableToastsCommandResponse: %s", disableToastsCommandResponse));
        CommandLineResponse disableNotificationsCommandResponse = CommandLineExecutor.execCommand(disableNotificationsCommand);
        LOGGER.info(String.format("disableNotificationsCommandResponse: %s", disableNotificationsCommandResponse));
    }

    private static void disableNotificationsForDeviceInDeviceFarm(Driver currentDriver, String deviceOn) {
        if (deviceOn.equalsIgnoreCase("pCloudy")) {
            Object disableToasts = ((AppiumDriver) currentDriver.getInnerDriver()).executeScript("pCloudy_executeAdbCommand", "adb shell appops set " + Runner.getAppPackageName() + " TOAST_WINDOW " + "deny");
            LOGGER.info(String.format("@disableToastsCommandResponse: %s", disableToasts));
            Object disableNotifications = ((AppiumDriver) currentDriver.getInnerDriver()).executeScript("pCloudy_executeAdbCommand", "adb shell settings put global heads_up_notifications_enabled 0");
            LOGGER.info("@disableNotificationsCommandResponse: " + disableNotifications);
        }
    }

    static void closeAppiumDriver(String userPersona, Driver driver) {
        if (Runner.getPlatform().equals(Platform.windows)) {
            closeWindowsAppOnMachine(userPersona, driver);
        } else if (Runner.getPlatform().equals(Platform.android)) {
            closeAndroidAppOnDevice(userPersona, driver);
        } else if (Runner.getPlatform().equals(Platform.iOS)) {
            closeIOSAppOnDevice(userPersona, driver);
        } else {
            throw new InvalidTestDataException(String.format("No implementation for platform: %s", Runner.getPlatform()));
        }
    }

    static void closeWindowsAppOnMachine(String userPersona, @NotNull Driver driver) {
        String logMessage;
        String appPackageName = Runner.getAppPackageName();
        AppiumDriver appiumDriver = (AppiumDriver) driver.getInnerDriver();
        --numberOfAppiumDriversUsed;
        LOGGER.info(String.format("numberOfAppiumDriversUsed: %d", numberOfAppiumDriversUsed));
        if (null == appiumDriver) {
            logMessage = String.format("Strange. But WindowsDriver for user '%s' already closed", userPersona);
            LOGGER.info(logMessage);
            ReportPortalLogger.logDebugMessage(logMessage);
        } else {
            logMessage = String.format("Closing WindowsDriver for App '%s' for user '%s'", appPackageName, userPersona);
            LOGGER.info(logMessage);
            // todo - fix for appium 2.0, test terminateApp() on windows app in windows OS
            AndroidDriver androidDriver = (AndroidDriver) appiumDriver;
            androidDriver.terminateApp(appPackageName);
            TestExecutionContext context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
            AppiumDriver atdAppiumDriver = (AppiumDriver) context.getTestState(TEST_CONTEXT.APPIUM_DRIVER);
            if (appiumDriver.equals(atdAppiumDriver)) {
                LOGGER.info(String.format("ATD will quit the driver for persona: '%s'", userPersona));
            } else {
                LOGGER.info(String.format("Quit driver for persona: '%s'", userPersona));
                appiumDriver.quit();
            }
            logMessage = String.format("App: '%s' terminated", appPackageName);
            LOGGER.info(logMessage);
            ReportPortalLogger.logDebugMessage(logMessage);
        }
    }

    static void closeAndroidAppOnDevice(String userPersona, @NotNull Driver driver) {
        String appPackageName = Runner.getAppPackageName();
        String logMessage;
        --numberOfAppiumDriversUsed;
        LOGGER.info(String.format("numberOfAppiumDriversUsed: %d", numberOfAppiumDriversUsed));
        AppiumDriver appiumDriver = (AppiumDriver) driver.getInnerDriver();
        if (null == appiumDriver) {
            logMessage = String.format("Strange. But AppiumDriver for user '%s' already closed", userPersona);
            LOGGER.info(logMessage);
            ReportPortalLogger.logDebugMessage(logMessage);
        } else {
            TestExecutionContext context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
            AppiumDriver atdAppiumDriver = (AppiumDriver) context.getTestState(TEST_CONTEXT.APPIUM_DRIVER);
            if (appiumDriver.equals(atdAppiumDriver)) {
                LOGGER.info(String.format("ATD will quit the driver for persona: '%s'", userPersona));
                LOGGER.info("Close the app");
                if (!Runner.isRunningInCI()) {
                    terminateApp((AndroidDriver) appiumDriver, appPackageName);
                }
            } else {
                LOGGER.info(String.format("Quit driver for persona: '%s'", userPersona));
                attachDeviceLogsToReportPortal(userPersona);
                terminateAndroidAppOnDevice(appPackageName, appiumDriver);
            }
        }
    }

    private static void terminateApp(AndroidDriver androidDriver, String appPackageName) {
        if (null == androidDriver) {
            LOGGER.debug("Driver already terminated. Returning.");
            return;
        }
        androidDriver.quit();
    }

    private static void terminateAndroidAppOnDevice(String appPackageName, AppiumDriver appiumDriver) {
        String logMessage;
        ApplicationState applicationState = null;
        try {
            LOGGER.info(String.format("Terminate app: %s", appPackageName));
            AndroidDriver androidDriver = (AndroidDriver) appiumDriver;
            applicationState = androidDriver.queryAppState(appPackageName);

            logMessage = String.format("App: '%s' Application state before closing app: '%s'%n", appPackageName, applicationState);
            LOGGER.info(logMessage);
            ReportPortalLogger.logDebugMessage(logMessage);

            androidDriver.terminateApp(appPackageName);
            applicationState = androidDriver.queryAppState(appPackageName);
            logMessage = String.format("App: '%s' Application state after closing app: '%s'%n", appPackageName, applicationState);
            LOGGER.info(logMessage);
        } catch (NoSuchSessionException e) {
            logMessage = e.getMessage();
            LOGGER.info(logMessage);
        }
        ReportPortalLogger.logDebugMessage(logMessage);
    }

    private static void attachDeviceLogsToReportPortal(String userPersona) {
        String deviceLogFileName = getDeviceLogFileNameFor(userPersona, Runner.getPlatform().toString());
        File destinationFile = new File(deviceLogFileName);

        String adbLogMessage = String.format("ADB Logs for %s, file name: %s", Drivers.getNameOfDeviceUsedByUser(userPersona), destinationFile.getName());
        LOGGER.info(adbLogMessage);
        ReportPortalLogger.attachFileInReportPortal(adbLogMessage, destinationFile);
    }

    private static String getDeviceLogFileNameFor(String userPersona, String forPlatform) {
        UserPersonaDetails userPersonaDetails = Drivers.getUserPersonaDetails(Runner.getTestExecutionContext(Thread.currentThread().getId()));
        return userPersonaDetails.getDeviceLogFileNameFor(userPersona, forPlatform);
    }

    private static void addDeviceLogFileNameFor(String userPersona, String forPlatform, String deviceLogFileName) {
        UserPersonaDetails userPersonaDetails = Drivers.getUserPersonaDetails(Runner.getTestExecutionContext(Thread.currentThread().getId()));
        userPersonaDetails.addDeviceLogFileNameFor(userPersona, forPlatform, deviceLogFileName);
    }

    static void freeDevices() {
//        for (DriverSession additionalDevice : additionalDevices) {
//            LOGGER.info(String.format("Freeing device: %s", additionalDevice.getDeviceName()));
//        }
        additionalDevices.clear();
    }

    @NotNull
    static Driver createWindowsDriverForUser(String userPersona, Platform forPlatform, TestExecutionContext context) {
        LOGGER.info(String.format("createWindowsDriverForUser: begin: userPersona: '%s', Platform: '%s', Number of " + "webdrivers: '%d'%n", userPersona, forPlatform.name(), numberOfAppiumDriversUsed));

        if (numberOfAppiumDriversUsed == MAX_NUMBER_OF_APPIUM_DRIVERS) {
            throw new InvalidTestDataException(String.format("Unable to create more than '%d' drivers for user persona: '%s' on platform: " + "'%s'", numberOfAppiumDriversUsed, userPersona, forPlatform.name()));
        }
        return createWindowsDriver(userPersona, forPlatform, context);
    }

    @NotNull
    private static Driver createWindowsDriver(String userPersona, Platform forPlatform, TestExecutionContext context) {
        Driver currentDriver;
        if (numberOfAppiumDriversUsed < MAX_NUMBER_OF_APPIUM_DRIVERS) {
            AppiumDriver windowsDriver = (AppiumDriver) context.getTestState(TEST_CONTEXT.APPIUM_DRIVER);
            String appName = Drivers.getAppNamefor(userPersona);

            String runningOn = Runner.isRunningInCI() ? "CI" : "local";
            context.addTestState(TEST_CONTEXT.WINDOWS_DEVICE_ON, runningOn);
            currentDriver = new Driver(context.getTestName() + "-" + userPersona, forPlatform, userPersona, appName, windowsDriver);
            Capabilities windowsDriverCapabilities = windowsDriver.getCapabilities();
            LOGGER.info(CAPABILITIES + windowsDriverCapabilities);
            Drivers.addUserPersonaDriverCapabilities(userPersona, windowsDriverCapabilities);
            LOGGER.info("deviceLog for windows driver: " + context.getTestStateAsString("deviceLog"));
            Drivers.addUserPersonaDeviceLogFileName(userPersona, context.getTestStateAsString("deviceLog"), forPlatform);
        } else {
            throw new InvalidTestDataException(String.format("Current number of WindowsDriver instances used: '%d'. " + "Unable to create " + "more than '%d' drivers for user persona: '%s' " + "on platform: '%s'", numberOfAppiumDriversUsed, MAX_NUMBER_OF_APPIUM_DRIVERS, userPersona, forPlatform.name()));
        }
        numberOfAppiumDriversUsed++;
        LOGGER.info(String.format("createWindowsDriverForUser: done: userPersona: '%s', Platform: '%s', Number of " + "windowsDrivers: '%d'", userPersona, forPlatform.name(), numberOfAppiumDriversUsed));
        return currentDriver;
    }

    public static Driver createIOSDriverForUser(String userPersona, Platform forPlatform, TestExecutionContext context) {
        LOGGER.info(String.format("createIOSDriverForUser: begin: userPersona: '%s', Platform: '%s', Number of " + "appiumDrivers: '%d'%n", userPersona, forPlatform.name(), numberOfAppiumDriversUsed));
        Driver currentDriver;
        if (numberOfAppiumDriversUsed == MAX_NUMBER_OF_APPIUM_DRIVERS) {
            throw new InvalidTestDataException(String.format("Unable to create more than '%d' drivers for user persona: '%s' on platform: " + "'%s'", numberOfAppiumDriversUsed, userPersona, forPlatform.name()));
        }

        currentDriver = setupOrCreateIOSDriver(userPersona, forPlatform, context);
        LOGGER.info(String.format("createIOSDriverForUser: done: userPersona: '%s', Platform: '%s', Number of " + "appiumDrivers: '%d'%n", userPersona, forPlatform.name(), numberOfAppiumDriversUsed));
        disableNotificationsAndToastsOnDevice(currentDriver, context.getTestStateAsString(TEST_CONTEXT.DEVICE_ON), (String) Drivers.getCapabilitiesFor(userPersona).getCapability("udid"));
        return currentDriver;
    }

    private static Driver setupOrCreateIOSDriver(String userPersona, Platform forPlatform, TestExecutionContext context) {
        String appName = Drivers.getAppNamefor(userPersona);
        File capabilityFileToUseForDriverCreation = getCapabilityFileToUseForDriverCreation(appName);
        Driver currentDriver;
        if (numberOfAppiumDriversUsed == 0) {
            currentDriver = setupFirstAppiumDriver(userPersona, forPlatform, context, appName);
        } else {
            currentDriver = createNewAppiumDriver(userPersona, forPlatform, context, appName, capabilityFileToUseForDriverCreation);
        }
        numberOfAppiumDriversUsed++;
        return currentDriver;
    }

    static void closeIOSAppOnDevice(String userPersona, @NotNull Driver driver) {
        String appBundleId = Runner.getAppPackageName();
        String logMessage;
        --numberOfAppiumDriversUsed;
        LOGGER.info(String.format("numberOfAppiumDriversUsed: %d", numberOfAppiumDriversUsed));
        AppiumDriver appiumDriver = (AppiumDriver) driver.getInnerDriver();

        if (null == appiumDriver) {
            logMessage = String.format("Strange. But AppiumDriver for user '%s' already closed", userPersona);
            LOGGER.info(logMessage);
            ReportPortalLogger.logDebugMessage(logMessage);
        } else {
            TestExecutionContext context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
            AppiumDriver iOSAppiumDriver = (AppiumDriver) context.getTestState(TEST_CONTEXT.APPIUM_DRIVER);

            if (appiumDriver.equals(iOSAppiumDriver)) {
                LOGGER.info(String.format("Appium will quit the driver for persona: '%s'", userPersona));
                LOGGER.info("Close the app");
                IOSDriver iosDriver = (IOSDriver) appiumDriver;
                iosDriver.terminateApp(appBundleId);
            } else {
                quitDriver(appiumDriver, userPersona);
                attachDeviceLogsToReportPortal(userPersona);
                terminateIOSAppOnDevice(appBundleId, appiumDriver);
            }
        }
    }

    static void quitDriver(AppiumDriver appiumDriver, String userPersona) {
        LOGGER.info(String.format("Quit driver for persona: '%s'", userPersona));
    }

    private static void terminateIOSAppOnDevice(String appPackageName, AppiumDriver appiumDriver) {
        String logMessage;
        ApplicationState applicationState = null;
        try {
            LOGGER.info(String.format("Terminate app: %s", appPackageName));
            IOSDriver iOSDriver = (IOSDriver) appiumDriver;
            applicationState = iOSDriver.queryAppState(appPackageName);

            logMessage = String.format("App: '%s' Application state before closing app: '%s'%n", appPackageName, applicationState);
            LOGGER.info(logMessage);
            ReportPortalLogger.logDebugMessage(logMessage);

            iOSDriver.terminateApp(appPackageName);
            applicationState = iOSDriver.queryAppState(appPackageName);
            logMessage = String.format("App: '%s' Application state after closing app: '%s'%n", appPackageName, applicationState);
            LOGGER.info(logMessage);
        } catch (NoSuchSessionException e) {
            logMessage = e.getMessage();
            LOGGER.info(logMessage);
        }
        ReportPortalLogger.logDebugMessage(logMessage);
    }
}
