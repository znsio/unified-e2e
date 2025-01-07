package com.znsio.teswiz.screen.android.autoscroll;

import com.applitools.eyes.appium.Target;
import com.znsio.teswiz.entities.Direction;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.autoscroll.AutoScrollScreen;
import io.appium.java_client.AppiumBy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class AutoScrollScreenAndroid extends AutoScrollScreen {
    private static final String SCREEN_NAME = AutoScrollScreenAndroid.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private final Driver driver;
    private final Visual visually;
    private final By byAddNewAppButton = AppiumBy.id("com.tafayor.autoscroll2:id/add");
    private final By byInnerDropdownElement = AppiumBy.xpath("/hierarchy/android.widget.FrameLayout");
    private final By byContactsTextView = AppiumBy.xpath("//android.widget.TextView[@text='Contacts']");


    public AutoScrollScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public AutoScrollScreen goToDropdownWindow() {
        LOGGER.info("clicking on add app button on home page");
        driver.waitTillElementIsPresent(byAddNewAppButton).click();
        return this;
    }

    @Override
    public AutoScrollScreen scrollInDynamicLayer(Direction direction) {
        LOGGER.info("starting: scrollInDynamicLayer()");
        WebElement dropdownElement = driver.waitTillElementIsPresent(byInnerDropdownElement);
        LOGGER.info(String.format("full screen size is: %s", driver.getInnerDriver().manage().window().getSize()));
        LOGGER.info(String.format("inner dropdown size is: %s", dropdownElement.getSize()));
        visually.checkWindow(SCREEN_NAME, "Full screen view including dropdown");
        driver.scrollInDynamicLayer(direction, dropdownElement);
        return this;
    }

    @Override
    public boolean isScrollSuccessful() {
        visually.check(SCREEN_NAME, "contacts dropdown element view", Target.region(byContactsTextView));
        return driver.findElement(byContactsTextView).isDisplayed();
    }
}
