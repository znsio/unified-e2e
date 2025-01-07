package com.znsio.teswiz.screen.android.dineout;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.dineout.DineoutLandingScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class DineoutLandingScreenAndroid
        extends DineoutLandingScreen {
    private static final String SCREEN_NAME = DineoutLandingScreenAndroid.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final String NOT_YET_IMPLEMENTED = " not yet implemented";
    private final Driver driver;
    private final Visual visually;

    public DineoutLandingScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
        visually.checkWindow(SCREEN_NAME, "Launch screen");
    }

    @Override
    public DineoutLandingScreen selectDefaultCity() {
        driver.waitTillElementIsPresent(By.id("com.dineout.book:id/skip_tv")).click();
        driver.waitTillElementIsPresent(By.id("com.dineout.book:id/manual_location_txt")).click();
        driver.waitTillElementIsPresent(By.id("com.dineout.book:id/et_search_location"))
                .sendKeys("Mumbai");
        driver.waitTillElementIsPresent(
                By.id("com.dineout.book:id/textView_search_location_header")).click();


        try {
            // waitFor offer then click, if exists
            driver.waitTillElementIsPresent(By.xpath(
                                                    "//android.widget" +
                                                    ".FrameLayout[@resource-id='com.dineout" +
                                                    ".book:id" +
                                                    "/inapp_half_interstitial_image_frame_layout" +
                                                    "']//android.widget" + ".ImageView[not" +
                                                    "(@resource-id='com.dineout" +
                                                    ".book:id/half_interstitial_image')]"),
                                            10).click();
        } catch (NoSuchElementException e) {
            LOGGER.info("Did not get any popup banner");
        }

        try {
            // update location, if exists
            driver.waitTillElementIsPresent(By.id("com.dineout.book:id/txt_update_now_cta"), 5)
                    .click();
        } catch (NoSuchElementException e) {
            LOGGER.info("Did not get update location CTA");
        }
        return this;
    }

    @Override
    public DineoutLandingScreen selectCity(String city) {
        driver.waitTillElementIsPresent(By.id("com.dineout.book:id/search_text")).click();
        driver.waitTillElementIsPresent(By.id("com.dineout.book:id/tv_rest_suggestions"))
                .sendKeys("Mumbai");
        // select the region
        driver.waitTillElementIsPresent(By.xpath("//android.widget.TextView[@text='Location']"))
                .click();

        // select first from the list
        driver.waitTillElementIsPresent(By.id("com.dineout.book:id/suggestion_layout")).click();
        return this;
    }

    @Override
    public DineoutLandingScreen searchCuisine(String cuisine) {
        driver.waitTillElementIsPresent(By.id("com.dineout.book:id/image_view_search")).click();
        WebElement suggestionsElement = driver.waitTillElementIsPresent(
                By.id("com.dineout.book:id/tv_rest_suggestions"));
        suggestionsElement.click();
        suggestionsElement.sendKeys(cuisine);
        suggestionsElement.click();
        driver.waitTillElementIsPresent(By.xpath("//android.widget.TextView[@text='Cuisine']"))
                .click();
        driver.waitTillElementIsPresent(By.id("com.dineout.book:id/selected_state_container"))
                .click();
        return this;
    }
}
