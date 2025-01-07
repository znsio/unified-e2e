package com.znsio.teswiz.screen.android.theapp;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.theapp.ClipboardDemoScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;

import static com.znsio.teswiz.tools.Wait.waitFor;

public class ClipboardDemoScreenAndroid
        extends ClipboardDemoScreen {
    private static final String SCREEN_NAME = ClipboardDemoScreenAndroid.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private final Driver driver;
    private final Visual visually;
    private final String bySetClipboardTextByAccessibilityId = "setClipboardText";
    private final String byMessageInputAccessibilityId = "messageInput";
    private final String byRefreshClipboardTextAccessibilityId = "refreshClipboardText";
    private final TestExecutionContext context;

    public ClipboardDemoScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
    }

    @Override
    public ClipboardDemoScreen setInClipboard(String content) {
        return enterTextToAddInClipboard(content).saveEnteredTextToClipBoard();
    }

    @Override
    public boolean doesAddedContentExistInClipboard() {
        String contentExpectedInClipboard = context.getTestStateAsString("contentInClipboard");

        driver.findElementByAccessibilityId(byRefreshClipboardTextAccessibilityId).click();
        visually.checkWindow(SCREEN_NAME, "Clipboard refreshed");
        boolean isElementPresentByAccessibilityId = driver.isElementPresentByAccessibilityId(
                contentExpectedInClipboard);
        LOGGER.info(String.format("Is content present in clipboad: '%s':: '%s'",
                                  contentExpectedInClipboard, isElementPresentByAccessibilityId));
        return isElementPresentByAccessibilityId;
    }

    private ClipboardDemoScreenAndroid saveEnteredTextToClipBoard() {
        driver.findElementByAccessibilityId(bySetClipboardTextByAccessibilityId).click();
        return this;
    }

    private ClipboardDemoScreenAndroid enterTextToAddInClipboard(String content) {
        waitFor(2);
        WebElement contentElement =
                driver.findElementByAccessibilityId(byMessageInputAccessibilityId);
        contentElement.click();
        contentElement.clear();
        contentElement.sendKeys(content);
        return this;
    }
}
