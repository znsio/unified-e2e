package com.znsio.teswiz.screen.web.googlesearch;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.googlesearch.GoogleSearchResultsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Collectors;

public class GoogleSearchResultsScreenWeb extends GoogleSearchResultsScreen {
    private static final String SCREEN_NAME = GoogleSearchResultsScreenWeb.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private final Driver driver;
    private final Visual visually;
    private final TestExecutionContext context;

    private final By searchResultsHeadings = By.cssSelector("a div[role='heading']");

    public GoogleSearchResultsScreenWeb(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
        long threadId = Thread.currentThread().getId();
        context = Runner.getTestExecutionContext(threadId);
    }

    @Override
    public List<String> getSearchResults() {
        visually.checkWindow(SCREEN_NAME, "india - Google Search");
        return driver.findElements(searchResultsHeadings).stream().map(webElement -> webElement.getText()).collect(Collectors.toList());
    }
}
