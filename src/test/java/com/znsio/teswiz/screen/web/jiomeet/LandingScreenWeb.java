package com.znsio.teswiz.screen.web.jiomeet;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.android.jiomeet.LandingScreenAndroid;
import com.znsio.teswiz.screen.jiomeet.InAMeetingScreen;
import com.znsio.teswiz.screen.jiomeet.LandingScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class LandingScreenWeb
        extends LandingScreen {
    private static final String SCREEN_NAME = LandingScreenAndroid.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final By byHeadingXpath = By.xpath("//h3[contains(@class,'heading')]");
    private static final By byWelcomeTextDescriptionXpath = By.xpath("//p[@class='desc']");
    private static final String NOT_YET_IMPLEMENTED = " not yet implemented";
    private static final By byStartAMeetingOptionXpath = By.xpath(
            "//div[contains(text(), 'Start a Meeting')]");
    private static final By byPMIButtonXpath = By.xpath(
            "//span[contains(text(), 'Personal Meeting ID')]");
    private static final By byStartMeetingButtonXpath = By.xpath(
            "//button[contains(text(), 'Start')]");

    private final Driver driver;
    private final Visual visually;

    public LandingScreenWeb(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public String getSignedInWelcomeMessage() {
        visually.checkWindow(SCREEN_NAME, "get signedin welcome message");
        String welcomeText = driver.waitTillElementIsPresent(byHeadingXpath).getText();
        welcomeText += " " + driver.waitTillElementIsPresent(byWelcomeTextDescriptionXpath)
                .getText();
        return welcomeText;
    }

    @Override
    public InAMeetingScreen startInstantMeeting() {
        driver.waitForClickabilityOf(byStartAMeetingOptionXpath).click();
        return startsTheMeeting();
    }

    private InAMeetingScreen startsTheMeeting() {
        WebElement startMeetingButton = driver.waitForClickabilityOf(byStartMeetingButtonXpath);
        visually.checkWindow(SCREEN_NAME, "Start meeting using 'One time Meeting ID'");
        startMeetingButton.click();
        return this.waitForInAMeetingScreenToLoad();
    }

    private InAMeetingScreen waitForInAMeetingScreenToLoad() {
        InAMeetingScreen inAMeetingScreen = InAMeetingScreen.get();
        inAMeetingScreen.getMicLabelText();
        visually.checkWindow(SCREEN_NAME, "Meeting started");
        return inAMeetingScreen;
    }

    @Override
    public LandingScreen waitTillWelcomeMessageIsSeen() {
        driver.waitTillElementIsPresent(byHeadingXpath);
        visually.checkWindow(SCREEN_NAME, "signedin successfully");
        return this;
    }
}
