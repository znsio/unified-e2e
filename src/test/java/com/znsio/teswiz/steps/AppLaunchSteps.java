package com.znsio.teswiz.steps;

import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.TEST_CONTEXT;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.Locale;

public class AppLaunchSteps {
    private static final Logger LOGGER = LogManager.getLogger(AppLaunchSteps.class.getName());
    private final TestExecutionContext context;

    public AppLaunchSteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        LOGGER.info("context: " + context.getTestName());
    }

    @Given("{string} start {string}")
    public void startOn(String userPersona, String appName) {
        String[] appNameParts = appName.split("-");
        appName = appNameParts[0].toLowerCase(Locale.ROOT) + "_" + Runner.getCloudName()
                .toLowerCase();
        String onPlatform = appNameParts[appNameParts.length - 1].toLowerCase(Locale.ROOT);
        LOGGER.info(System.out.printf("startOn - Persona:'%s', AppName: '%s', Platform: '%s'",
                                      userPersona, appName, onPlatform));
        context.addTestState(userPersona, userPersona);
        Drivers.createDriverFor(userPersona, appName, Platform.valueOf(onPlatform), context);
    }

    @And("{string} starts {string} on {string}")
    public void startsOn(String userPersona, String appName, String browserName) {
        String[] appNameParts = appName.split("-");
        appName = appNameParts[0].toLowerCase(Locale.ROOT);
        String onPlatform = appNameParts[appNameParts.length - 1].toLowerCase(Locale.ROOT);
        String evaluatedBrowser = this.evaluateBrowserType(browserName);
        LOGGER.info(System.out.printf(
                "startOn - Persona:'%s', AppName: '%s', Browser: '%s', Platform: '%s'", userPersona,
                appName, evaluatedBrowser, onPlatform));
        context.addTestState(userPersona, userPersona);
        Drivers.createDriverFor(userPersona, appName, evaluatedBrowser,
                                Platform.valueOf(onPlatform), context);
    }

    /**
     * method to evaluate whether the scenario is to be executed in normal web browser view or
     * mobile emulation specific view
     * if mobile emulation is detected, it adds the targeted device name in context that will be
     * utilized while creating WebDriver
     *
     * @param browserName (like 'firefox-mobile2', 'chrome-tab1', etc)
     * @return actual browser name (like 'firefox', 'chrome', etc)
     */
    private String evaluateBrowserType(String browserName) {
        String[] details = browserName.split("-");
        if (details.length == 1) {
            //when user passes values like 'chrome', 'firefox', etc
            return browserName;
        }

        //when user passes values like 'chrome-mobile1', 'safari-tab2', etc
        String browser = details[0];
        String mobileEmulation = details[1];

        //Fetching deviceName from Browser Config json file based on 'mobileEmulation' variable
        String deviceName = new JSONObject(Runner.getBrowserConfigFileContents()).getString(mobileEmulation);
        LOGGER.info("Device name from browser config file :" + deviceName);
        context.addTestState(TEST_CONTEXT.MOBILE_EMULATION_DEVICE, deviceName);

        return browser;
    }
}
