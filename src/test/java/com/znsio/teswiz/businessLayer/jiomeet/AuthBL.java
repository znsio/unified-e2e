package com.znsio.teswiz.businessLayer.jiomeet;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.screen.jiomeet.SignInScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthBL {
    private static final Logger LOGGER = LogManager.getLogger(AuthBL.class.getName());
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    public AuthBL(String userPersona, Platform forPlatform) {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = userPersona;
        this.currentPlatform = forPlatform;
        Runner.setCurrentDriverForUser(userPersona, forPlatform, context);
    }

    public AuthBL() {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = SAMPLE_TEST_CONTEXT.ME;
        this.currentPlatform = Runner.getPlatform();
    }

    public InAMeetingBL signInAndStartMeeting(Map userPersona) {
        return signIn(userPersona).startInstantMeeting();
    }

    public LandingBL signIn(Map userDetails) {
        String username = String.valueOf(userDetails.get("username"));
        String password = String.valueOf(userDetails.get("password"));
        String firstName = String.valueOf(userDetails.get("firstName"));
        String lastName = String.valueOf(userDetails.get("lastName"));

        String expectedWelcomeMessageAndroid = "Hello " + firstName + " \n" + "what would you " +
                                               "like to do?";
        String expectedWelcomeMessageWeb = "Hello " + firstName + " " + lastName + ", what would " +
                                           "you like to do?";
        String expectedWelcomeMessage =
                currentPlatform.equals(Platform.web) || currentPlatform.equals(Platform.electron) ? expectedWelcomeMessageWeb
                                                                                                  : expectedWelcomeMessageAndroid;

        String signedInWelcomeMessage = SignInScreen.get().signIn(username, password)
                .getSignedInWelcomeMessage();

        LOGGER.info(String.format("signedInWelcomeMessage: '%s'", signedInWelcomeMessage));

        assertThat(signedInWelcomeMessage).as("Welcome message is incorrect")
                .isEqualTo(expectedWelcomeMessage);
        return new LandingBL(currentUserPersona, currentPlatform);
    }
}
