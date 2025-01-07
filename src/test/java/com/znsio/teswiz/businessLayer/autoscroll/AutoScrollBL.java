package com.znsio.teswiz.businessLayer.autoscroll;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Direction;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.screen.autoscroll.AutoScrollScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;


public class AutoScrollBL {

    private static final Logger LOGGER = LogManager.getLogger(AutoScrollBL.class.getName());
    private final TestExecutionContext context;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    public AutoScrollBL(String userPersona, Platform forPlatform) {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        this.currentUserPersona = userPersona;
        this.currentPlatform = forPlatform;
        Runner.setCurrentDriverForUser(userPersona, forPlatform, context);
    }

    public AutoScrollBL goToDropdownWindow() {
        AutoScrollScreen.get().goToDropdownWindow();
        return this;
    }

    public AutoScrollBL verifyScrollInDynamicLayerFunctionality(Direction direction) {
        LOGGER.info("verifying scroll in dynamic layer functionality");
        assertThat(AutoScrollScreen.get().scrollInDynamicLayer(direction).isScrollSuccessful())
                .as("scroll did not happen in inner dropdown")
                .isTrue();
        return this;
    }
}
