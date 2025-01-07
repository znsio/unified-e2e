package com.znsio.teswiz.businessLayer.theapp;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.screen.ScreenShotScreen;
import com.znsio.teswiz.screen.theapp.AppLaunchScreen;
import com.znsio.teswiz.screen.theapp.LoginScreen;
import com.znsio.teswiz.tools.cmd.CommandLineExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;

public class AppBL {
    private static final Logger LOGGER = LogManager.getLogger(AppBL.class.getName());
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    public AppBL(String userPersona, Platform forPlatform) {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = userPersona;
        this.currentPlatform = forPlatform;
        Runner.setCurrentDriverForUser(userPersona, forPlatform, context);
    }

    public AppBL() {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = SAMPLE_TEST_CONTEXT.ME;
        this.currentPlatform = Runner.getPlatform();
    }

    public LoginBL provideInvalidDetailsForSignup(String username, String password) {
        AppLaunchScreen.get().selectLogin();
        return loginAgain(username, password);
    }

    public LoginBL loginAgain(String username, String password) {
        String errorMessage = "Invalid login credentials error message is incorrect";
        String androidIOSErrorMessage = "Invalid login credentials, please try again";
        String webErrorMessage = "Your username is invalid!";
        String expectedErrorMessage =
                currentPlatform.equals(Platform.web) ? webErrorMessage : androidIOSErrorMessage;

        LoginScreen loginScreen = LoginScreen.get().enterLoginDetails(username, password).login();
        String actualErrorMessage = loginScreen.getInvalidLoginError();
        LOGGER.info("actualErrorMessage: " + actualErrorMessage);

        loginScreen.dismissAlert();

        softly.assertThat(actualErrorMessage).as(errorMessage).contains(expectedErrorMessage);
        return new LoginBL(currentUserPersona, currentPlatform);
    }

    public AppBL goBack() {
        AppLaunchScreen.get().goBack();
        return this;
    }

    public AppBL stopTheAppAndLaunchItAgain() {
        forceStopTheApp();
        LOGGER.info("Start theapp");
        String[] startTheApp = new String[]{"adb shell am start com.appiumpro.the_app/com.appiumpro.the_app.MainActivity"};
        CommandLineExecutor.execCommand(startTheApp);
        ScreenShotScreen.get().takeScreenshot();
        return this;
    }

    public AppBL forceStopTheApp() {
        LOGGER.info("ForceStop TheApp");
        String[] forceStopTheApp = new String[]{"adb shell am force-stop com.appiumpro.the_app"};
        CommandLineExecutor.execCommand(forceStopTheApp);
        return this;
    }

    public AppBL launchAndTakeScreenshot() {
        ScreenShotScreen.get().takeScreenshot();
        return this;
    }

    public AppBL goToLogin() {
        AppLaunchScreen.get().selectLogin();
        return this;
    }
}
