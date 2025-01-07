package com.znsio.teswiz.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonFileTest {
    private static final Logger LOGGER = LogManager.getLogger(JsonFileTest.class.getName());
    private static final String LOG_DIR = "./target/testLogs";

    @BeforeClass
    public static void setupBefore() {
        LOGGER.info("Using LOG_DIR: " + System.getProperty("LOG_DIR"));
    }

    @Test
    void compareIdenticalFiles() {
        assertThat(JsonFile.compareFiles("configs/browser_config.json", "configs/browser_config.json")).as("Files are not identical").isTrue();
    }

    @Test
    void compareDifferentFiles() {
        assertThat(JsonFile.compareFiles("caps/theapp/theapp_browserstack_capabilities.json", "caps/theapp/theapp_headspin_android_capabilities.json")).as("Files are not identical").isFalse();
    }
}
