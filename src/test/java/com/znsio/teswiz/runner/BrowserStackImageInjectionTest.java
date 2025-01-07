package com.znsio.teswiz.runner;

import io.appium.java_client.android.AndroidDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.BeforeClass;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

class BrowserStackImageInjectionTest {
    private static final String LOG_DIR = "./target/testLogs";

    private static final Logger LOGGER = LogManager.getLogger(
            BrowserStackImageInjectionTest.class.getName());

    static AndroidDriver driver;
    static String authenticationUser = null;
    static String authenticationKey = null;
    static String cloudName = null;

    @BeforeClass
    public static void setupBefore() {
        LOGGER.info("Using LOG_DIR: " + System.getProperty("LOG_DIR"));

        authenticationUser = System.getenv("CLOUD_USERNAME");
        authenticationKey = System.getenv("CLOUD_KEY");
        cloudName = Runner.getCloudName();
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("device", "Samsung Galaxy S20");
        caps.setCapability("os_version", "10.0");
        caps.setCapability("app", "bs://db95520c7e93e980a6fc6221554b1f905195cb66");
        caps.setCapability("browserstack.enableCameraImageInjection", "true");
        try {
            driver = new AndroidDriver(
                    new URL(String.format("https://%s:%s@hub-cloud.browserstack.com/wd/hub",
                                          authenticationUser, authenticationKey)), caps);

        } catch (MalformedURLException e) {
            throw new RuntimeException(
                    String.format("Error starting Android driver: %s", e.getMessage()), e);
        }
    }

    //    @Test
    void toVerifyUploadURLIsGettingGenerated() {
        String imageFile = System.getProperty(
                "user.dir") + "/src/test/resources/images/handbag.jpg";
        assertThat(BrowserStackImageInjection.injectMediaToDriver(imageFile, driver,
                                                                  authenticationUser,
                                                                  authenticationKey)).as(
                "Injecting image to device failed").isNotNull();
    }

}
