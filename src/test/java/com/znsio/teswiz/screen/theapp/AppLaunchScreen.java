package com.znsio.teswiz.screen.theapp;

import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.android.theapp.AppLaunchScreenAndroid;
import com.znsio.teswiz.screen.ios.theapp.AppLaunchScreenIOS;
import com.znsio.teswiz.screen.web.theapp.AppLaunchScreenWeb;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AppLaunchScreen {
    private static final String SCREEN_NAME = AppLaunchScreen.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);

    public static AppLaunchScreen get() {
        Driver driver = Drivers.getDriverForCurrentUser(Thread.currentThread().getId());
        Platform platform = Runner.fetchPlatform(Thread.currentThread().getId());
        LOGGER.info(SCREEN_NAME + ": Driver type: " + driver.getType() + ": Platform: " + platform);
        Visual visually = Drivers.getVisualDriverForCurrentUser(Thread.currentThread().getId());

        switch (platform) {
            case web:
                return new AppLaunchScreenWeb(driver, visually);
            case android:
                return new AppLaunchScreenAndroid(driver, visually);
            case iOS:
                return new AppLaunchScreenIOS(driver, visually);
        }
        throw new NotImplementedException(
                SCREEN_NAME + " is not implemented in " + Runner.getPlatform());
    }

    public abstract LoginScreen selectLogin();

    public abstract AppLaunchScreen goBack();

    public abstract EchoScreen selectEcho();

    public abstract ClipboardDemoScreen goToClipboardDemo();
}
