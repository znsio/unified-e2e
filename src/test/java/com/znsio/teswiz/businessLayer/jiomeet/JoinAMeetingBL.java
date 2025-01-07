package com.znsio.teswiz.businessLayer.jiomeet;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.screen.jiomeet.SignInScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;

import static org.assertj.core.api.Assertions.assertThat;

public class JoinAMeetingBL {
    private static final Logger LOGGER = LogManager.getLogger(JoinAMeetingBL.class.getName());
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    public JoinAMeetingBL(String userPersona, Platform forPlatform) {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = userPersona;
        this.currentPlatform = forPlatform;
        Runner.setCurrentDriverForUser(userPersona, forPlatform, context);
    }

    public JoinAMeetingBL() {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = SAMPLE_TEST_CONTEXT.ME;
        this.currentPlatform = Runner.getPlatform();
    }

    public InAMeetingBL joinMeeting(String meetingId, String meetingPassword) {
        boolean hasMeetingStarted = SignInScreen.get().joinAMeeting(meetingId, meetingPassword,
                                                                    currentUserPersona)
                .isMeetingStarted();
        assertThat(hasMeetingStarted).as(currentUserPersona + " not yet joined the meeting")
                .isTrue();
        return new InAMeetingBL();
    }
}
