package com.znsio.teswiz.screen.web.theapp;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.theapp.AppLaunchScreen;
import com.znsio.teswiz.screen.theapp.ClipboardDemoScreen;
import com.znsio.teswiz.screen.theapp.EchoScreen;
import com.znsio.teswiz.screen.theapp.LoginScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

public class AppLaunchScreenWeb
        extends AppLaunchScreen {
    private static final String NOT_YET_IMPLEMENTED = "NOT_YET_IMPLEMENTED";
    private static final Logger LOGGER = LogManager.getLogger(AppLaunchScreenWeb.class.getName());
    private static final By loginFormLinkText = By.linkText("Form Authentication");
    private final Driver driver;
    private final Visual visually;
    private final String SCREEN_NAME = AppLaunchScreenWeb.class.getSimpleName();

    public AppLaunchScreenWeb(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
        visually.checkWindow(SCREEN_NAME, "Home screen");
    }

    @Override
    public LoginScreen selectLogin() {
        driver.findElement(loginFormLinkText).click();
        return LoginScreen.get();
    }

    @Override
    public AppLaunchScreen goBack() {
        LOGGER.info("Skipping this step for Web");
        return this;
    }

    @Override
    public EchoScreen selectEcho() {
        LOGGER.info("Skipping this step for Web");
        return EchoScreen.get();
    }

    @Override
    public ClipboardDemoScreen goToClipboardDemo() {
        return null;
    }
}
