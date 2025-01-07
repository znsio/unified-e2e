package com.znsio.teswiz.screen.android.jiocinema;

import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.appium.Target;
import com.znsio.teswiz.entities.Direction;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.jiocinema.JioCinemaScreen;
import io.appium.java_client.AppiumBy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class JioCinemaScreenAndroid extends JioCinemaScreen {
    private static final Logger LOGGER = LogManager.getLogger(JioCinemaScreenAndroid.class.getName());
    private final Driver driver;
    private final Visual visually;
    private final String SCREEN_NAME = JioCinemaScreenAndroid.class.getSimpleName();
    private final String visibleMovieNumberXpath = "//android.widget.TextView[@text='%s']";
    private final String movieXpath = "//android.widget.TextView[@text='%s']//parent::android.view.View";

    public JioCinemaScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public JioCinemaScreen swipeRight() {
        driver.swipeRight();
        return this;
    }

    @Override
    public JioCinemaScreen swipeLeft() {
        driver.swipeLeft();
        return this;
    }

    @Override
    public JioCinemaScreen scrollTillTrendingInIndiaSection() {
        LOGGER.info("scrolling till trending in India section");
        visually.checkWindow(SCREEN_NAME, "Home Page", MatchLevel.LAYOUT);
        driver.scrollVertically(70, 30, 50);
        return this;
    }

    @Override
    public boolean isMovieNumberVisibleOnScreen(int movieNumberOnScreen) {
        LOGGER.info(String.format("validating if movie number %s is visible after swipe in 'trending in india' section", movieNumberOnScreen));
        boolean isMovieVisibleOnScreen = driver.findElement(
                AppiumBy.xpath(String.format(visibleMovieNumberXpath, movieNumberOnScreen))).isDisplayed();
        visually.check(SCREEN_NAME, String.format("movie number %s visible on screen", movieNumberOnScreen),
                       Target.region(AppiumBy.xpath(String.format(visibleMovieNumberXpath, movieNumberOnScreen))));
        return isMovieVisibleOnScreen;
    }

    @Override
    public JioCinemaScreen swipeTrendingItem(Direction direction, int movieNumberOnScreen) {
        LOGGER.info(String.format("Swiping %s movie number : %s ", direction, movieNumberOnScreen));
        WebElement movieTrending = driver.waitTillElementIsVisible(
                By.xpath(String.format(movieXpath, movieNumberOnScreen)));
        driver.horizontalSwipeWithGesture(movieTrending, direction);
        return this;
    }
}
