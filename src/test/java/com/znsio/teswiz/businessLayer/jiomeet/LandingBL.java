package com.znsio.teswiz.businessLayer.jiomeet;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.screen.jiomeet.InAMeetingScreen;
import com.znsio.teswiz.screen.jiomeet.LandingScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;

import static org.assertj.core.api.Assertions.assertThat;

public class LandingBL {
    private static final Logger LOGGER = LogManager.getLogger(LandingBL.class.getName());
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    public LandingBL(String userPersona, Platform forPlatform) {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = userPersona;
        this.currentPlatform = forPlatform;
        Runner.setCurrentDriverForUser(userPersona, forPlatform, context);
    }

    public LandingBL() {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = SAMPLE_TEST_CONTEXT.ME;
        this.currentPlatform = Runner.getPlatform();
    }

    public InAMeetingBL startInstantMeeting() {
        InAMeetingScreen inAMeetingScreen = LandingScreen.get().startInstantMeeting();
        boolean hasMeetingStarted = inAMeetingScreen.isMeetingStarted();
        assertThat(hasMeetingStarted).as("Meeting should have been started").isTrue();
        String meetingId = inAMeetingScreen.getMeetingId();
        String meetingPassword = inAMeetingScreen.getMeetingPassword();
        LOGGER.info(String.format("Meeting id: '%s', password: '%s'", meetingId, meetingPassword));
        context.addTestState(SAMPLE_TEST_CONTEXT.MEETING_ID, meetingId);
        context.addTestState(SAMPLE_TEST_CONTEXT.MEETING_PASSWORD, meetingPassword);
        return new InAMeetingBL();
    }
}
