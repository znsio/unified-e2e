package com.znsio.teswiz.screen.web.theapp;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.theapp.EchoScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EchoScreenWeb
        extends EchoScreen {
    private static final String NOT_YET_IMPLEMENTED = "NOT_YET_IMPLEMENTED";
    private static final Logger LOGGER = LogManager.getLogger(EchoScreenWeb.class.getName());
    private final Driver driver;
    private final Visual visually;
    private final String SCREEN_NAME = EchoScreenWeb.class.getSimpleName();

    public EchoScreenWeb(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public EchoScreen echoMessage(String message) {
        LOGGER.info("Skipping this step for Web");
        return this;
    }
}
