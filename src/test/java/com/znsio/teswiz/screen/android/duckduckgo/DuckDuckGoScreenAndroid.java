package com.znsio.teswiz.screen.android.duckduckgo;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.duckduckgo.DuckDuckGoScreen;
import io.appium.java_client.AppiumBy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.znsio.teswiz.tools.Wait.waitFor;

public class DuckDuckGoScreenAndroid
        extends DuckDuckGoScreen {
    private static final String SCREEN_NAME = DuckDuckGoScreenAndroid.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private final Driver driver;
    private final Visual visually;
    private final By byPrimaryCtaId = AppiumBy.id("com.duckduckgo.mobile.android.debug:id/primaryCta");
    private final By byCancelChangingDefaultBrowserId = AppiumBy.id("android:id/button2");
    private By byGetDefaultTextFromWebViewId;

    public DuckDuckGoScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public DuckDuckGoScreen launchBrowser() {
        WebElement ctaElement = driver.waitTillElementIsPresent(byPrimaryCtaId, 30);
        visually.checkWindow(SCREEN_NAME, "DuckDuckGo launched");
        ctaElement.click();
        return this;
    }

    @Override
    public DuckDuckGoScreen cancelChangingDefaultBrowserPopup() {
        driver.waitTillElementIsPresent(byCancelChangingDefaultBrowserId, 30).click();
        return this;
    }

    @Override
    public String getDefaultTextFromWebView() {
        driver.setWebViewContext();
        byGetDefaultTextFromWebViewId = AppiumBy.id("com.duckduckgo.mobile.android.debug:id/dialogTextCta");
        String defaultText = driver.waitTillElementIsPresent(byGetDefaultTextFromWebViewId).getText();
        LOGGER.info(String.format("Default text in webview: '%s'", defaultText));
        return defaultText;
    }

    @Override
    public DuckDuckGoScreen switchToNativeContextAndGoToTeswizGithub() {
        driver.setNativeAppContext();
        driver.getInnerDriver().get("https://github.com/znsio/teswiz");
        waitFor(3);
        visually.checkWindow(SCREEN_NAME, "teswiz github");
        return this;
    }
}
