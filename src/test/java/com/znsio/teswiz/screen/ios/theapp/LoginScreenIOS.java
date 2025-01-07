package com.znsio.teswiz.screen.ios.theapp;

import com.applitools.eyes.appium.Target;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.theapp.LoginScreen;
import io.appium.java_client.AppiumBy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.znsio.teswiz.tools.Wait.waitFor;

public class LoginScreenIOS
        extends LoginScreen {
    private static final String SCREEN_NAME = LoginScreenIOS.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private final Driver driver;
    private final Visual visually;
    private final By byUserNameXpath = AppiumBy.xpath("(//XCUIElementTypeOther[@name=\"username\"])[2]");
    private final By byPasswordXpath = AppiumBy.xpath("(//XCUIElementTypeOther[@name=\"password\"])[2]");
    private final By byOKButtonXpath = AppiumBy.xpath("(//*[@name=\"OK\"])");
    private final By byAlertBoxXpath = AppiumBy.xpath("(//*[contains(@label,\"Invalid login credentials\")])");
    private final String loginButtonId = "loginBtn";

    public LoginScreenIOS(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public LoginScreen enterLoginDetails(String username, String password) {
        waitFor(2);
        WebElement userNameElement = driver.findElement(byUserNameXpath);
        userNameElement.clear();
        userNameElement.sendKeys(username);
        WebElement passwordElement = driver.findElement(byPasswordXpath);
        passwordElement.clear();
        passwordElement.sendKeys(password);
        //        driver.waitForVisibilityOf(passwordId).sendKeys(username);
        visually.check(SCREEN_NAME, "Entered login details",
                       Target.window().fully().layout(userNameElement, passwordElement));
        return this;
    }

    @Override
    public LoginScreen login() {
        waitFor(1);
        driver.findElementByAccessibilityId(loginButtonId).click();
        waitFor(2);
        visually.checkWindow(SCREEN_NAME, "Clicked on Login");
        return this;
    }

    @Override
    public String getInvalidLoginError() {
        String actualAlterText = driver.findElement(byAlertBoxXpath).getText();
        LOGGER.info("actualAlterText: " + actualAlterText);
        return actualAlterText;
    }

    @Override
    public LoginScreen dismissAlert() {
        waitFor(2);
        driver.findElement(byOKButtonXpath).click();
        return this;
    }
}
