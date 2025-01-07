package com.znsio.teswiz.businessLayer.failing;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Runner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

public class FailingBL {
    private static final Logger LOGGER = LogManager.getLogger(FailingBL.class.getName());
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    public FailingBL() {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = SAMPLE_TEST_CONTEXT.ME;
        this.currentPlatform = Runner.getPlatform();
    }

    public void softlyFail(String randomString) {
        LOGGER.info("softlyFail-" + randomString);
        softly.fail(randomString);
    }

    public void hardFail(String randomString) {
        LOGGER.info("hardFail-" + randomString);
        Assertions.assertThat(true).as("Hard fail - " + randomString).isFalse();
    }

    public void pass(String randomString) {
        LOGGER.info("pass-" + randomString);
    }
}
