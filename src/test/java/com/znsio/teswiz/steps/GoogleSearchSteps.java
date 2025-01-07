package com.znsio.teswiz.steps;

import com.znsio.teswiz.businessLayer.googlesearch.GoogleSearchBL;
import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import io.cucumber.java.en.Given;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

public class GoogleSearchSteps {
    private static final Logger LOGGER = LogManager.getLogger(GoogleSearchSteps.class.getName());
    private final TestExecutionContext context;

    public GoogleSearchSteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        LOGGER.info("context: " + context.getTestName());
    }

    /**
     * Launches browser in specified platform
     *
     * @param searchText text for google search
     * @param appName    in format of "browser-platform"
     *                   examples: "chrome-android" or "firefox-web"
     */
    @Given("I search for {string} in {string}")
    public void iSearchFor(String searchText, String appName) {
        String[] appNameParts = appName.split("-");
        String browserName = appNameParts[0].toLowerCase(Locale.ROOT);
        String onPlatform = appNameParts[appNameParts.length - 1].toLowerCase(Locale.ROOT);
        LOGGER.info(System.out.printf("iSearchFor - Persona:'%s'", SAMPLE_TEST_CONTEXT.ME));
        Drivers.createDriverFor(SAMPLE_TEST_CONTEXT.ME, appName, browserName, Platform.valueOf(onPlatform), context);
        new GoogleSearchBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).searchFor(searchText);
    }
}
