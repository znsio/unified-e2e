package com.znsio.teswiz.screen.android.jiomeet;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.jiomeet.InAMeetingScreen;
import com.znsio.teswiz.screen.jiomeet.LandingScreen;
import com.znsio.teswiz.screen.jiomeet.SignInScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.znsio.teswiz.tools.Wait.waitFor;

public class SignInScreenAndroid
        extends SignInScreen {
    private static final String SCREEN_NAME = SignInScreenAndroid.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final By bySignInId = By.id("com.jio.rilconferences:id/signIn");
    private static final By byUserNameId = By.id("com.jio.rilconferences:id/edit_mobile_number");
    private static final By byProceedButtonId = By.id("com.jio.rilconferences:id/btn_otp_next");
    private static final By byPasswordId = By.id("com.jio.rilconferences:id/edt_otp_number");
    private static final By byWelcomeMessageId = By.id("com.jio.rilconferences:id/textUserName");
    private static final By byJoinMeetingId = By.id("com.jio.rilconferences:id/joinMeetingBtn");
    private static final By byEnterMeetingId = By.id("com.jio.rilconferences:id/inputMeetingLink");
    private static final By byEnterMeetingPasswordId = By.id(
            "com.jio.rilconferences:id/inputMeetingPassword");
    private static final By byEnterDisplayNameId = By.id("com.jio.rilconferences:id/inputUserName");
    private static final By byEnterMeeting = By.id("com.jio.rilconferences:id/buttonNext");
    private static final By byJoinMeeting = By.id("com.jio.rilconferences:id/buttonJoinMeeting");
    private final Driver driver;
    private final Visual visually;

    public SignInScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public LandingScreen signIn(String username, String password) {
        startSignInProcess().enterUsernameAndProceed(username).enterPasswordAndSignIn(password);

        return LandingScreen.get().waitTillWelcomeMessageIsSeen();
    }

    @Override
    public InAMeetingScreen joinAMeeting(String meetingId, String meetingPassword,
            String currentUserPersona) {
        joinAMeeting().enterMeetingDetails(meetingId, meetingPassword, currentUserPersona);
        return InAMeetingScreen.get();
    }

    private SignInScreenAndroid enterMeetingDetails(String meetingId, String meetingPassword,
            String currentUserPersona) {
        driver.waitTillElementIsPresent(byEnterMeetingId).sendKeys(meetingId);
        driver.waitTillElementIsPresent(byEnterMeetingPasswordId).sendKeys(meetingPassword);
        driver.waitTillElementIsPresent(byEnterDisplayNameId).sendKeys(currentUserPersona);
        driver.waitTillElementIsPresent(byEnterMeeting).click();
        visually.checkWindow(SCREEN_NAME, "enterMeetingDetails");
        driver.waitTillElementIsPresent(byJoinMeeting).click();
        waitFor(8);
        return this;
    }

    private SignInScreenAndroid joinAMeeting() {
        visually.checkWindow(SCREEN_NAME, "joinAMeeting");
        driver.waitTillElementIsPresent(byJoinMeetingId).click();
        return this;
    }

    private SignInScreenAndroid enterPasswordAndSignIn(String password) {
        WebElement passwordElement = driver.waitTillElementIsPresent(byPasswordId);
        passwordElement.click();
        passwordElement.clear();
        passwordElement.sendKeys(password);
        visually.checkWindow(SCREEN_NAME, "Password entered");
        driver.waitForClickabilityOf(byProceedButtonId).click();
        return this;
    }

    private SignInScreenAndroid enterUsernameAndProceed(String username) {
        WebElement usernameElement = driver.waitTillElementIsPresent(byUserNameId);
        usernameElement.click();
        usernameElement.clear();
        usernameElement.sendKeys(username);
        visually.checkWindow(SCREEN_NAME, "Username entered");
        driver.waitForClickabilityOf(byProceedButtonId).click();
        return this;
    }

    private SignInScreenAndroid startSignInProcess() {
        visually.checkWindow(SCREEN_NAME, "Start Sign In process");
        driver.waitTillElementIsPresent(bySignInId).click();
        return this;
    }
}
