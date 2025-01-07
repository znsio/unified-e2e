package com.znsio.teswiz.screen.android.confengine;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.confengine.ConfEngineLandingScreen;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

import java.util.Set;

public class ConfEngineLandingScreenAndroid
        extends ConfEngineLandingScreen {
    private static final String SCREEN_NAME = ConfEngineLandingScreenAndroid.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final String NOT_YET_IMPLEMENTED = " not yet implemented";
    private final Driver driver;
    private final Visual visually;

    public ConfEngineLandingScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
        visually.checkWindow(SCREEN_NAME, "Launch screen");
    }

    @Override
    public ConfEngineLandingScreen getListOfConferences() {
        visually.checkWindow(SCREEN_NAME, "Landing screen");
        Set<String> contextNames = ((AppiumDriver) driver.getInnerDriver()).getWindowHandles();
        for (String contextName : contextNames) {
            System.out.println(contextName); //prints out something like NATIVE_APP \n WEBVIEW_1
        }
        ((AppiumDriver) driver.getInnerDriver()).switchTo().window(String.valueOf(contextNames.toArray()[1]));
        driver.waitTillElementIsPresent(By.xpath("//li[@conference-id='selenium-conf-2022']")).click();
        visually.checkWindow(SCREEN_NAME, "Selected Selenium Conf 2022");
        return this;
    }
}
