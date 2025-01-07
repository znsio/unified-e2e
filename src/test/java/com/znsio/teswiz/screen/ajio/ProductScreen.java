package com.znsio.teswiz.screen.ajio;

import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.android.ajio.ProductScreenAndroid;
import com.znsio.teswiz.screen.ios.ajio.ProductScreenIOS;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ProductScreen {
    private static final String SCREEN_NAME = ProductScreen.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);

    public static ProductScreen get() {
        Driver driver = Drivers.getDriverForCurrentUser(Thread.currentThread().getId());
        Platform platform = Runner.fetchPlatform(Thread.currentThread().getId());
        LOGGER.info(SCREEN_NAME + ": Driver type: " + driver.getType() + ": Platform: " + platform);
        Visual visually = Drivers.getVisualDriverForCurrentUser(Thread.currentThread().getId());

        switch (platform) {
            case android:
                return new ProductScreenAndroid(driver, visually);
            case iOS:
                return new ProductScreenIOS(driver, visually);
        }
        throw new NotImplementedException(
                SCREEN_NAME + " is not implemented in " + Runner.getPlatform());
    }

    public abstract CartScreen addProductToCart();

    public abstract String getProductName();

    public abstract boolean isProductDetailsLoaded();

    public abstract ProductScreen flickImage();

    public abstract String isElementIdChanged();

    public abstract boolean isProductBrandNameVisible();

    public abstract ProductScreen clickOnAddToCart();

    public abstract ProductScreen selectAvailableSize();

    public abstract ProductScreen clickOnAddToBagButton();

    public abstract String getAddedToBagToastMessage();

    public abstract CartScreen clickOnCartIcon();
}
