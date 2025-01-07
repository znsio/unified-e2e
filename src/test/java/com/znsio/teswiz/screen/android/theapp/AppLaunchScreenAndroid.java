package com.znsio.teswiz.screen.android.theapp;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.theapp.AppLaunchScreen;
import com.znsio.teswiz.screen.theapp.ClipboardDemoScreen;
import com.znsio.teswiz.screen.theapp.EchoScreen;
import com.znsio.teswiz.screen.theapp.LoginScreen;
import io.appium.java_client.AppiumBy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

public class AppLaunchScreenAndroid
        extends AppLaunchScreen {
    private static final String SCREEN_NAME = AppLaunchScreenAndroid.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final By byGoBackToHomeScreenButtonXpath = By.xpath(
            "//android.widget.ImageButton[@content-desc=\"Navigate Up\"]");
    private static final By byEchoMessageXpath = By.xpath(
            "//android.view.ViewGroup[@content-desc=\"Echo Box\"]/android.view.ViewGroup");
    private final Driver driver;
    private final Visual visually;
    private final String byClipboardDemoAccessibilityId = "Clipboard Demo";
    private final By byLoginScreenAccessibilityId = AppiumBy.accessibilityId("Login Screen");

    public AppLaunchScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public LoginScreen selectLogin() {
        driver.findElement(byLoginScreenAccessibilityId).click();
        visually.checkWindow(SCREEN_NAME, "Login Screen");
        return LoginScreen.get();
    }

    @Override
    public AppLaunchScreen goBack() {
        driver.waitForClickabilityOf(byGoBackToHomeScreenButtonXpath).click();
        return this;
    }

    @Override
    public EchoScreen selectEcho() {
        driver.waitForClickabilityOf(byEchoMessageXpath).click();
        return EchoScreen.get();
    }

    @Override
    public ClipboardDemoScreen goToClipboardDemo() {
        LOGGER.debug("Clicking on Clipboard Demo");
        visually.checkWindow(SCREEN_NAME, "On app launch screen");
        driver.findElementByAccessibilityId(byClipboardDemoAccessibilityId).click();
        return ClipboardDemoScreen.get();
    }
}
