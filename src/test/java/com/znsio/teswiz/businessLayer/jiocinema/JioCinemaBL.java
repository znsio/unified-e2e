package com.znsio.teswiz.businessLayer.jiocinema;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Direction;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.screen.jiocinema.JioCinemaScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;

import static org.assertj.core.api.Assertions.assertThat;

public class JioCinemaBL {
    private static final Logger LOGGER = LogManager.getLogger(JioCinemaBL.class.getName());
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    public JioCinemaBL(String userPersona, Platform forPlatform) {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = userPersona;
        this.currentPlatform = forPlatform;
        Runner.setCurrentDriverForUser(userPersona, forPlatform, context);
    }

    public JioCinemaBL swipeRightOnTrendingInIndiaSection() {
        JioCinemaScreen.get().swipeRight();
        return this;
    }

    public JioCinemaBL swipeLeftOnTrendingInIndiaSection() {
        JioCinemaScreen.get().swipeLeft();
        return this;
    }

    public JioCinemaBL scrollTillTrendingInIndiaSection() {
        JioCinemaScreen.get().scrollTillTrendingInIndiaSection();
        return this;
    }

    public JioCinemaBL swipeMovieTrendingInIndiaSection(Direction direction, int movieNumberOnScreen) {
        JioCinemaScreen.get().swipeTrendingItem(direction, movieNumberOnScreen);
        return this;
    }

    public JioCinemaBL verifyMovieNumberVisibleOnScreen(int movieNumberOnScreen) {
        LOGGER.info("verifying movie number visible on trending in india screen after swipe");
        assertThat(JioCinemaScreen.get().isMovieNumberVisibleOnScreen(movieNumberOnScreen))
                .as(String.format("movie number %s is not visible on screen", movieNumberOnScreen))
                .isTrue();
        return this;
    }
}
