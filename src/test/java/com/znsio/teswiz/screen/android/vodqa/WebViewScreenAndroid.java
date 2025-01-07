package com.znsio.teswiz.screen.android.vodqa;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.vodqa.VodqaScreen;
import com.znsio.teswiz.screen.vodqa.WebViewScreen;
import io.appium.java_client.AppiumBy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

public class WebViewScreenAndroid extends WebViewScreen {
    private static final String SCREEN_NAME = WebViewScreenAndroid.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final By byWebViewScreenHeaderXpath = AppiumBy.xpath("//android.widget.TextView[@text = 'Webview']");
    private static final By byLoginOptionWebviewXpath = By.xpath("//a[text()='login']");
    private static final By byBackButtonXpath = AppiumBy.xpath("//android.widget.TextView[@text = 'Back']");
    private final Driver driver;
    private final Visual visually;

    public WebViewScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public boolean isUserOnNewsWebViewScreen() {
        LOGGER.info("Verify user is on news web view screen");
        visually.checkWindow(SCREEN_NAME, "Web View Screen");
        return driver.findElement(byWebViewScreenHeaderXpath).isDisplayed();
    }

    @Override
    public boolean isLoginOptionVisible() {
        LOGGER.info("Switching context to web view context and verifying login option under webView is visible");
        visually.checkWindow(SCREEN_NAME, "Web view screen");
        return driver.setWebViewContext()
                .findElement(byLoginOptionWebviewXpath)
                .isDisplayed();
    }

    @Override
    public VodqaScreen navigateToSamplesList() {
        LOGGER.info("Switching context to native app and navigate to samples list");
        driver.setNativeAppContext()
                .findElement(byBackButtonXpath)
                .click();
        visually.checkWindow(SCREEN_NAME, "Sample list screen");
        return VodqaScreen.get();
    }
}
