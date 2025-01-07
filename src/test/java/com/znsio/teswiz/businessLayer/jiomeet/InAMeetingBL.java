package com.znsio.teswiz.businessLayer.jiomeet;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.screen.jiomeet.InAMeetingScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;

import static org.assertj.core.api.Assertions.assertThat;


public class InAMeetingBL {
    private static final Logger LOGGER = LogManager.getLogger(InAMeetingBL.class.getName());
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    public InAMeetingBL(String userPersona, Platform forPlatform) {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = userPersona;
        this.currentPlatform = forPlatform;
        Runner.setCurrentDriverForUser(userPersona, forPlatform, context);
    }

    public InAMeetingBL() {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = SAMPLE_TEST_CONTEXT.ME;
        this.currentPlatform = Runner.getPlatform();
    }

    public InAMeetingBL unmuteMyself() {
        InAMeetingScreen.get().unmute();
        return this;
    }


    public InAMeetingBL muteMyself() {
        InAMeetingScreen.get().mute();
        return this;
    }

    public InAMeetingBL openNotificationFromNotificationBar() {
        InAMeetingScreen.get().openJioMeetNotification();
        return this;
    }

    public InAMeetingBL verifyMeetingOpenedInJioMeetApplication() {
        assertThat(InAMeetingScreen.get().isMeetingStarted()).as(
                "Meeting is not opened in Jio Meet Application").isTrue();
        return this;
    }
}
