package com.znsio.teswiz.screen.ajio;

import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.android.ajio.HomeScreenAndroid;
import com.znsio.teswiz.screen.ios.ajio.HomeScreenIOS;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public abstract class HomeScreen {
    private static final String SCREEN_NAME = HomeScreen.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);

    public static HomeScreen get() {
        Driver driver = Drivers.getDriverForCurrentUser(Thread.currentThread().getId());
        Platform platform = Runner.fetchPlatform(Thread.currentThread().getId());
        LOGGER.info(SCREEN_NAME + ": Driver type: " + driver.getType() + ": Platform: " + platform);
        Visual visually = Drivers.getVisualDriverForCurrentUser(Thread.currentThread().getId());

        switch (platform) {
            case android:
                return new HomeScreenAndroid(driver, visually);

            case iOS:
                return new HomeScreenIOS(driver, visually);
        }
        throw new NotImplementedException(
                SCREEN_NAME + " is not implemented in " + Runner.getPlatform());
    }

    public abstract SearchScreen searchByImage();

    public abstract HomeScreen attachFileToDevice(Map imageData);

    public abstract HomeScreen goToMenu();

    public abstract SearchScreen selectProductFromCategory(String product, String category, String gender);

    public abstract ProductScreen searchForTheProduct(String productName);

    public abstract HomeScreen clickOnAllowToSendNotifications();

    public abstract HomeScreen clickOnAllowLocation();

    public abstract HomeScreen clickOnAllowLocationWhileUsingApp();

    public abstract HomeScreen relaunchApplication();
}
