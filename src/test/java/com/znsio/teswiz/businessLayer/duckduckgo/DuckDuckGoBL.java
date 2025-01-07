package com.znsio.teswiz.businessLayer.duckduckgo;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.screen.duckduckgo.DuckDuckGoScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;

import static org.assertj.core.api.Assertions.assertThat;

public class DuckDuckGoBL {
    private static final Logger LOGGER = LogManager.getLogger(DuckDuckGoBL.class.getName());
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    public DuckDuckGoBL(String userPersona, Platform forPlatform) {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = userPersona;
        this.currentPlatform = forPlatform;
        Runner.setCurrentDriverForUser(userPersona, forPlatform, context);
    }

    public DuckDuckGoBL() {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = SAMPLE_TEST_CONTEXT.ME;
        this.currentPlatform = Runner.getPlatform();
    }

    public DuckDuckGoBL launchBrowser() {
        DuckDuckGoScreen.get().launchBrowser();
        return this;
    }

    public DuckDuckGoBL cancelChangingDefaultBrowserPopUp() {
        DuckDuckGoScreen.get().cancelChangingDefaultBrowserPopup();
        return this;
    }

    public DuckDuckGoBL switchToWebViewAndCheckDefaultText() {
        String defaultTextFromWebView = DuckDuckGoScreen.get().getDefaultTextFromWebView();
        assertThat(defaultTextFromWebView).as("Default text is empty").isNotEmpty();
        return this;
    }

    public DuckDuckGoBL swithToNativeContextAndGoToTeswiz() {
        DuckDuckGoScreen.get().switchToNativeContextAndGoToTeswizGithub();
        return this;
    }
}
