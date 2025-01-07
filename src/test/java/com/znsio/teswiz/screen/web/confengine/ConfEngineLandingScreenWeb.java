package com.znsio.teswiz.screen.web.confengine;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.confengine.ConfEngineLandingScreen;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfEngineLandingScreenWeb
        extends ConfEngineLandingScreen {
    private static final String SCREEN_NAME = ConfEngineLandingScreenWeb.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final String NOT_YET_IMPLEMENTED = " not yet implemented";
    private final Driver driver;
    private final Visual visually;

    public ConfEngineLandingScreenWeb(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
        visually.checkWindow(SCREEN_NAME, "Launch screen");
    }

    @Override
    public ConfEngineLandingScreen getListOfConferences() {
        throw new NotImplementedException(
                SCREEN_NAME + ":" + new Throwable().getStackTrace()[0].getMethodName() + NOT_YET_IMPLEMENTED);
    }
}
