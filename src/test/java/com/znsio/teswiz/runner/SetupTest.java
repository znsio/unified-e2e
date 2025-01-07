package com.znsio.teswiz.runner;

import com.znsio.teswiz.entities.APPLITOOLS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SetupTest {
    private static final Logger LOGGER = LogManager.getLogger(SetupTest.class.getName());

    private static final String configFilePath = "./configs/ajio/ajio_local_android_config.properties";

    @BeforeClass
    public static void setupBefore() {
        LOGGER.info("Using LOG_DIR: " + System.getProperty("LOG_DIR"));
    }

    @BeforeMethod
    public void beforeMethod() {
        System.clearProperty(APPLITOOLS.PROXY_KEY);
        System.clearProperty(APPLITOOLS.PROXY_URL);
    }

    @Test
    void checkNoApplitoolsProxyKey() {
        Setup.load(configFilePath);
        Setup.loadAndUpdateConfigParameters(configFilePath);
        Setup.initialiseApplitoolsConfiguration();
        Map applitoolsConfiguration = Runner.getApplitoolsConfiguration();
        System.out.println(applitoolsConfiguration.get(APPLITOOLS.PROXY_KEY));
        System.out.println(applitoolsConfiguration.get(APPLITOOLS.PROXY_URL));
        assertThat(applitoolsConfiguration.get(APPLITOOLS.PROXY_KEY)).as("Applitools Proxy key is invalid").hasToString(
                APPLITOOLS.PROXY_KEY);
        assertThat(applitoolsConfiguration.get(APPLITOOLS.PROXY_URL)).as("Applitools Proxy url is invalid").isNull();
    }

    @Test
    void checkWithApplitoolsProxyKeyInConfig() {
        Setup.load(configFilePath);
        Setup.loadAndUpdateConfigParameters(configFilePath);
        Setup.addToConfigs(Setup.APPLITOOLS_CONFIGURATION, "./configs/applitools_config_withValidProxyKey.json");
        Setup.initialiseApplitoolsConfiguration();
        Map applitoolsConfiguration = Runner.getApplitoolsConfiguration();
        System.out.println(applitoolsConfiguration.get(APPLITOOLS.PROXY_KEY));
        System.out.println(applitoolsConfiguration.get(APPLITOOLS.PROXY_URL));
        assertThat(applitoolsConfiguration.get(APPLITOOLS.PROXY_KEY)).as("Applitools Proxy key is invalid").hasToString(
                "user.dir");
        assertThat(applitoolsConfiguration.get(APPLITOOLS.PROXY_URL)).as("Applitools Proxy url is invalid").isNotNull();
    }

    @Test
    void checkWithInvalidApplitoolsProxyKeyInConfig() {
        Setup.load(configFilePath);
        Setup.loadAndUpdateConfigParameters(configFilePath);
        Setup.addToConfigs(Setup.APPLITOOLS_CONFIGURATION, "./configs/applitools_config_withInvalidProxyKey.json");
        Setup.initialiseApplitoolsConfiguration();
        Map applitoolsConfiguration = Runner.getApplitoolsConfiguration();
        System.out.println(applitoolsConfiguration.get(APPLITOOLS.PROXY_KEY));
        System.out.println(applitoolsConfiguration.get(APPLITOOLS.PROXY_URL));
        assertThat(applitoolsConfiguration.get(APPLITOOLS.PROXY_KEY)).as("Applitools Proxy key is invalid").hasToString(
                "user.di");
        assertThat(applitoolsConfiguration.get(APPLITOOLS.PROXY_URL)).as("Applitools Proxy url is invalid").isNull();
    }

    @Test
    void checkWithEmptyApplitoolsProxyKeyInConfig() {
        Setup.load(configFilePath);
        Setup.loadAndUpdateConfigParameters(configFilePath);
        Setup.addToConfigs(Setup.APPLITOOLS_CONFIGURATION, "./configs/applitools_config_withEmptyProxyKey.json");
        Setup.initialiseApplitoolsConfiguration();
        Map applitoolsConfiguration = Runner.getApplitoolsConfiguration();
        System.out.println(applitoolsConfiguration.get(APPLITOOLS.PROXY_KEY));
        System.out.println(applitoolsConfiguration.get(APPLITOOLS.PROXY_URL));
        assertThat(applitoolsConfiguration.get(APPLITOOLS.PROXY_KEY)).as("Applitools Proxy key is invalid").hasToString(
                APPLITOOLS.PROXY_KEY);
        assertThat(applitoolsConfiguration.get(APPLITOOLS.PROXY_URL)).as("Applitools Proxy url is invalid").isNull();
    }

    @Test
    void checkInvalidApplitoolsProxyKey() {
        String proxyKey = "user.di";
        System.setProperty(APPLITOOLS.PROXY_KEY, proxyKey);
        Setup.load(configFilePath);
        Setup.loadAndUpdateConfigParameters(configFilePath);
        Setup.initialiseApplitoolsConfiguration();
        Map applitoolsConfiguration = Runner.getApplitoolsConfiguration();
        System.out.println(applitoolsConfiguration.get(APPLITOOLS.PROXY_KEY));
        System.out.println(applitoolsConfiguration.get(APPLITOOLS.PROXY_URL));
        assertThat(applitoolsConfiguration.get(APPLITOOLS.PROXY_KEY)).as("Applitools Proxy key is invalid").hasToString(
                proxyKey);
        assertThat(applitoolsConfiguration.get(APPLITOOLS.PROXY_URL)).as("Applitools Proxy url is invalid").isNull();
    }

    @Test
    void checkValidApplitoolsProxyKey() {
        String proxyKey = "user.dir";
        System.setProperty(APPLITOOLS.PROXY_KEY, proxyKey);
        Setup.load(configFilePath);
        Setup.loadAndUpdateConfigParameters(configFilePath);
        Setup.initialiseApplitoolsConfiguration();
        Map applitoolsConfiguration = Runner.getApplitoolsConfiguration();
        System.out.println(applitoolsConfiguration.get(APPLITOOLS.PROXY_KEY));
        System.out.println(applitoolsConfiguration.get(APPLITOOLS.PROXY_URL));
        assertThat(applitoolsConfiguration.get(APPLITOOLS.PROXY_KEY)).as("Applitools Proxy key is invalid").hasToString(
                proxyKey);
        assertThat(applitoolsConfiguration.get(APPLITOOLS.PROXY_URL)).as("Applitools Proxy url is invalid").isNotNull();
    }

    @Test
    void checkInvalidApplitoolsProxyKeyOverridingConfig() {
        String proxyKey = "user.nam";
        System.setProperty(APPLITOOLS.PROXY_KEY, proxyKey);
        Setup.load(configFilePath);
        Setup.loadAndUpdateConfigParameters(configFilePath);
        Setup.addToConfigs(Setup.APPLITOOLS_CONFIGURATION, "./configs/applitools_config_withValidProxyKey.json");
        Setup.initialiseApplitoolsConfiguration();
        Map applitoolsConfiguration = Runner.getApplitoolsConfiguration();
        System.out.println(applitoolsConfiguration.get(APPLITOOLS.PROXY_KEY));
        System.out.println(applitoolsConfiguration.get(APPLITOOLS.PROXY_URL));
        assertThat(applitoolsConfiguration.get(APPLITOOLS.PROXY_KEY)).as("Applitools Proxy key is invalid").hasToString(
                proxyKey);
        assertThat(applitoolsConfiguration.get(APPLITOOLS.PROXY_URL)).as("Applitools Proxy url is invalid").isNull();
    }

    @Test
    void checkValidApplitoolsProxyKeyOverridingConfig() {
        String proxyKey = "user.name";
        System.setProperty(APPLITOOLS.PROXY_KEY, proxyKey);
        Setup.load(configFilePath);
        Setup.loadAndUpdateConfigParameters(configFilePath);
        Setup.addToConfigs(Setup.APPLITOOLS_CONFIGURATION, "./configs/applitools_config_withValidProxyKey.json");
        Setup.initialiseApplitoolsConfiguration();
        Map applitoolsConfiguration = Runner.getApplitoolsConfiguration();
        System.out.println(applitoolsConfiguration.get(APPLITOOLS.PROXY_KEY));
        System.out.println(applitoolsConfiguration.get(APPLITOOLS.PROXY_URL));
        assertThat(applitoolsConfiguration.get(APPLITOOLS.PROXY_KEY)).as("Applitools Proxy key is invalid").hasToString(
                proxyKey);
        assertThat(applitoolsConfiguration.get(APPLITOOLS.PROXY_URL)).as("Applitools Proxy url is invalid").isNotNull();
    }
}
