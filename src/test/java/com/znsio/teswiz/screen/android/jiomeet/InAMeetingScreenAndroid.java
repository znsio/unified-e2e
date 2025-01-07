package com.znsio.teswiz.screen.android.jiomeet;

import com.znsio.teswiz.exceptions.jiomeet.InAMeetingException;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.jiomeet.InAMeetingScreen;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.znsio.teswiz.tools.Wait.waitFor;

public class InAMeetingScreenAndroid
        extends InAMeetingScreen {
    private static final String SCREEN_NAME = InAMeetingScreenAndroid.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final By byMicStatusId = By.id("com.jio.rilconferences:id/mic_status_label");
    private static final By byMeetingId = By.id("com.jio.rilconferences:id/caller_number");
    private static final By byMeetingPasswordId = By.id(
            "com.jio.rilconferences:id/caller_password");

    private static final By byMeetingNotificationXpath = By.xpath("//android.widget.TextView[@text='JioMeet Video call']");
    private static final By byTopHeaderControlsPanelId = By.id("videoTopLayout1");
    private static final String NOT_YET_IMPLEMENTED = " not yet implemented";
    private final Driver driver;
    private final Visual visually;

    public InAMeetingScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public boolean isMeetingStarted() {
        try {
            enableInMeetingControls("isMeetingStarted");
            visually.checkWindow(SCREEN_NAME, "isMeetingStarted");
            // the on-screen controls may get hidden again by the time the visual validation is done
            enableInMeetingControls("isMeetingStarted");
            driver.waitTillElementIsPresent(byMicStatusId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getMeetingId() {
        enableInMeetingControls("getMeetingId");
        return driver.waitTillElementIsPresent(byMeetingId).getText().replace("-", "");
    }

    @Override
    public String getMeetingPassword() {
        enableInMeetingControls("getMeetingPassword");
        return driver.waitTillElementIsPresent(byMeetingPasswordId).getText()
                .replace("Password: ", "");
    }

    @Override
    public InAMeetingScreen openJioMeetNotification() {
        LOGGER.info("Opening Jio Meeting notification from notification bar ");
        driver.selectNotificationFromNotificationDrawer(byMeetingNotificationXpath);
        return this;
    }

    @Override
    public InAMeetingScreen unmute() {
        enableInMeetingControls("unmute");
        visually.checkWindow(SCREEN_NAME, "mic should be muted");
        // the on-screen controls may get hidden again by the time the visual validation is done
        enableInMeetingControls("unmute");
        WebElement micStatus = driver.waitTillElementIsPresent(byMicStatusId);
        LOGGER.info("unmute- current mic status: " + micStatus.getText());
        if (micStatus.getText().equals("Mute")) {
            throw new InAMeetingException("Mic is already unmuted");
        } else {
            micStatus.click();
        }
        return this;
    }

    @Override
    public InAMeetingScreen mute() {
        enableInMeetingControls("mute");
        visually.checkWindow(SCREEN_NAME, "mic should be unmuted");
        // the on-screen controls may get hidden again by the time the visual validation is done
        enableInMeetingControls("mute");
        WebElement micStatus = driver.waitTillElementIsPresent(byMicStatusId);
        LOGGER.info("mute- current mic status: " + micStatus.getText());
        if (micStatus.getText().equals("Unmute")) {
            throw new InAMeetingException("Mic is already muted");
        } else {
            micStatus.click();
        }
        return this;
    }

    @Override
    public String getMicLabelText() {
        throw new NotImplementedException(
                SCREEN_NAME + ":" + new Throwable().getStackTrace()[0].getMethodName() + NOT_YET_IMPLEMENTED);
    }

    private void enableInMeetingControls(String calledFrom) {
        LOGGER.info(String.format("enableInMeetingControls: Called from: '%s'%n", calledFrom));
        boolean isTopHeaderDisplayed = areInMeetingControlsDisplayed();
        LOGGER.info(String.format(
                "enableInMeetingControls: Called from: '%s': headers displayed?: '%s'", calledFrom,
                isTopHeaderDisplayed));
        int retryAttempt = 0;
        if (!isTopHeaderDisplayed) {
            do {
                int seconds = 1;
                LOGGER.info(String.format(
                        "enableInMeetingControls: Called from: '%s', ': headers not displayed. " +
                        "Wait for '%d' sec and try again",
                        calledFrom, seconds));
                waitFor(seconds);
                retryAttempt++;
                driver.tapOnMiddleOfScreen();
                isTopHeaderDisplayed = areInMeetingControlsDisplayed();
                LOGGER.info(String.format(
                        "enableInMeetingControls: Called from: '%s': retryAttempt: '%d' : are " +
                        "headers displayed now: '%s'",
                        calledFrom, retryAttempt, isTopHeaderDisplayed));
            } while (!isTopHeaderDisplayed && retryAttempt < 8);
            if (!isTopHeaderDisplayed) {
                throw new InAMeetingException(
                        "Unable to see In Meeting Controls called from '" + calledFrom + "' in " + retryAttempt + " retry attempts");
            }
        }
    }

    private boolean areInMeetingControlsDisplayed() {
        return driver.isElementPresent(byTopHeaderControlsPanelId);
    }
}
