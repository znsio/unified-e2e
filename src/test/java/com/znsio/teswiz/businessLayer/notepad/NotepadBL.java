package com.znsio.teswiz.businessLayer.notepad;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.screen.ScreenShotScreen;
import com.znsio.teswiz.screen.notepad.NotepadScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;

public class NotepadBL {
    private static final Logger LOGGER = LogManager.getLogger(NotepadBL.class.getName());
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    public NotepadBL() {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = SAMPLE_TEST_CONTEXT.ME;
        this.currentPlatform = Runner.getPlatform();
        LOGGER.info("NotepadBL created");
    }

    public NotepadBL(String userPersona, Platform forPlatform) {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = userPersona;
        this.currentPlatform = forPlatform;
        Runner.setCurrentDriverForUser(userPersona, forPlatform, context);
        LOGGER.info("NotepadBL created for userPersona: " + userPersona);
    }

    public NotepadBL verifyLaunched() {
        ScreenShotScreen.get().takeScreenshot();
        return this;
    }

    public NotepadBL typeMessage(String message) {
        NotepadScreen.get().typeMessage(message);
        return this;
    }
}
