package com.znsio.teswiz.screen.web.indigo;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.indigo.IndigoFlightSearchResultsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class IndigoFlightSearchResultsScreenWeb
        extends IndigoFlightSearchResultsScreen {
    private static final String SCREEN_NAME =
            IndigoFlightSearchResultsScreenWeb.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final String NOT_YET_IMPLEMENTED = " not yet implemented";
    private static final By byBackToSearchResultsLinkXpath = By.xpath(
            "//div[@class='bck-to-search']");
    private final Driver driver;
    private final Visual visually;
    private final WebDriver innerDriver;
    private final TestExecutionContext context;

    public IndigoFlightSearchResultsScreenWeb(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
        this.innerDriver = this.driver.getInnerDriver();
        long threadId = Thread.currentThread().getId();
        context = Runner.getTestExecutionContext(threadId);
        driver.waitTillElementIsVisible(byBackToSearchResultsLinkXpath, 30);
        visually.checkWindow(SCREEN_NAME, "On Search Results page");
    }
}
