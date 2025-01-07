package com.znsio.teswiz.screen.android.jiomeet;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.jiomeet.InAMeetingScreen;
import com.znsio.teswiz.screen.jiomeet.LandingScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

import static com.znsio.teswiz.tools.Wait.waitFor;

public class LandingScreenAndroid
        extends LandingScreen {
    private static final String SCREEN_NAME = LandingScreenAndroid.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final By byWelcomeMessageId = By.id("com.jio.rilconferences:id/textUserName");
    private static final By byStartInstantMeetingId = By.id(
            "com.jio.rilconferences:id/buttonStartMeeting");
    private final Driver driver;
    private final Visual visually;

    public LandingScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public String getSignedInWelcomeMessage() {
        return driver.waitTillElementIsPresent(byWelcomeMessageId).getText();
    }

    @Override
    public InAMeetingScreen startInstantMeeting() {
        visually.checkWindow(SCREEN_NAME, "Start instant meeting");
        driver.waitTillElementIsPresent(byStartInstantMeetingId).click();
        visually.checkWindow(SCREEN_NAME, "Options before starting meeting");
        driver.waitTillElementIsPresent(byStartInstantMeetingId).click();
        waitFor(8);
        return InAMeetingScreen.get();
    }

    @Override
    public LandingScreen waitTillWelcomeMessageIsSeen() {
        driver.waitTillElementIsPresent(byWelcomeMessageId);
        return this;
    }
}
