package com.znsio.teswiz.screen.googlesearch;

import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.android.googlesearch.GoogleSearchResultsScreenAndroid;
import com.znsio.teswiz.screen.web.googlesearch.GoogleSearchResultsScreenWeb;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public abstract class GoogleSearchResultsScreen {
    private static final String SCREEN_NAME = GoogleSearchResultsScreen.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);

    public static GoogleSearchResultsScreen get() {
        Driver driver = Drivers.getDriverForCurrentUser(Thread.currentThread().getId());
        Platform platform = Runner.fetchPlatform(Thread.currentThread().getId());
        LOGGER.info(SCREEN_NAME + ": Driver type: " + driver.getType() + ": Platform: " + platform);
        Visual visually = Drivers.getVisualDriverForCurrentUser(Thread.currentThread().getId());

        switch (platform) {
            case android:
                return new GoogleSearchResultsScreenAndroid(driver, visually);
            case web:
                return new GoogleSearchResultsScreenWeb(driver, visually);
            default:
                throw new NotImplementedException(
                        SCREEN_NAME + " is not implemented in " + Runner.getPlatform());
        }
    }

    public abstract List<String> getSearchResults();
}
