package com.znsio.teswiz;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.services.UnirestService;
import com.znsio.teswiz.tools.*;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;

import java.io.File;
import java.util.HashMap;

class PublicMethodsTest {
    private static final Logger LOGGER = LogManager.getLogger(PublicMethodsTest.class.getName());
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    PublicMethodsTest(String userPersona, Platform forPlatform) {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = userPersona;
        this.currentPlatform = forPlatform;
        Runner.setCurrentDriverForUser(userPersona, forPlatform, context);
    }

    //    @Test
    private void teswizPublicMethodsCheck() {
        long threadId = Thread.currentThread().getId();

        String osName = Runner.OS_NAME;
        boolean isWindows = Runner.IS_WINDOWS;
        boolean isMac = Runner.IS_MAC;
        String userDirectory = Runner.USER_DIRECTORY;
        String userName = Runner.USER_NAME;
        String notSet = Runner.NOT_SET;
        String aDefault = Runner.DEFAULT;
        String debug = Runner.DEBUG;
        String info = Runner.INFO;
        String warn = Runner.WARN;

        new Runner();
        new Runner("configFilePath", "stepDefDirName", "featuresDirName");
        Runner.getPlatformForUser("me");
        Runner.getHostName();
        Runner.getPlatform();
        Runner.getApplitoolsConfiguration();
        Runner.getCloudName();
        Runner.getCloudUser();
        Runner.getCloudKey();
        Runner.getRemoteDriverGridPort();
        Runner.getRemoteDriverGridHostName();
        Runner.getMaxNumberOfAppiumDrivers();
        Runner.getMaxNumberOfWebDrivers();
        Runner.isVisualTestingEnabled();
        Runner.shouldFailTestOnVisualDifference();
        Runner.getFromEnvironmentConfiguration("BASE_URL");
        Runner.getTestData("USERNAME");
        Runner.getTestDataAsMap("ENV");
        Runner.main(new String[]{});
        Runner.getTestExecutionContext(1);
        Runner.getSoftAssertion(threadId);
        Runner.setCurrentDriverForUser(currentUserPersona, currentPlatform, context);
        Runner.fetchPlatform(threadId);
        Runner.getTargetEnvironment();
        Runner.getBaseURLForWeb();
        Runner.getAppPackageName();
        Runner.isRunningInCI();
        Runner.isCLI();
        Runner.isPDF();
        Runner.isAPI();
        Runner.getBrowser();
        Runner.getProxyURL();
        Runner.getBrowserConfigFileContents();
        Runner.getBrowserConfigFileContents("non-default-browserConfig.json");
        Runner.getBrowserConfigFile();

        Drivers.setDriverFor(currentUserPersona, currentPlatform, context);
        Drivers.isDriverAssignedForUser(currentUserPersona);
        Drivers.createDriverFor(currentUserPersona, currentPlatform, context);
        Drivers.createDriverFor(currentUserPersona, "appName", "browserName", currentPlatform,
                                context);
        Drivers.createDriverFor(currentUserPersona, "appNane", currentPlatform, context);
        Drivers.createPDFDriverFor(currentUserPersona, currentPlatform, context, "pdfFileName");

        Drivers.getDriverForUser(currentUserPersona);
        Drivers.assignNewPersonaToExistingDriver(currentUserPersona, "newPersona", context);
        Drivers.getDriverForCurrentUser(threadId);
        Drivers.getVisualDriverForCurrentUser(threadId);
        Drivers.getNameOfDeviceUsedByUser(currentUserPersona);
        Drivers.getAvailableUserPersonas();
        Scenario scenario = null;
        Drivers.attachLogsAndCloseAllDrivers(scenario);

        ReportPortalLogger.logDebugMessage("logDebugMessage");
        ReportPortalLogger.logInfoMessage("logInfoMessage");
        ReportPortalLogger.logWarningMessage("logWarningMessage");
        ReportPortalLogger.attachFileInReportPortal("attachFileInReportPortal", new File("build.gradle"));

        UnirestService.getHttpResponse("");
        UnirestService.getHttpResponseWithQueryParameter("", "", "");
        UnirestService.getHttpResponseWithQueryMap("", new HashMap<>());
        UnirestService.postHttpRequest("", "");
        UnirestService.patchHttpRequest("", "");
        UnirestService.deleteHttpRequest("");

        DateTime.getFormattedMeetingTime(5);

        IPAddress.getPublicIPAddress();
        IPAddress.getPrivateIPAddress();

        JsonFile.saveJsonToFile(new HashMap<>(), "");
        JsonFile.getNodeValueAsMapFromJsonFile("", "");
        JsonFile.loadJsonFile("");
        String[] a = new String[0];
        JsonFile.getNodeValueAsStringFromJsonFile("", a);
        JsonFile.getValueFromLoadedJsonMap("", a, new HashMap<>());
        JsonFile.getNodeValueAsArrayListFromJsonFile("", "");
        JsonFile.convertToMap("");
        JsonFile.convertToArray("");
        JsonFile.compareFiles("", "");

        JsonSchemaValidator.validateJsonFileAgainstSchema("", "", "");

        Randomizer.randomize(5);
        Randomizer.randomize("5");
        Randomizer.randomizeAlphaNumericString(5);
        Randomizer.randomizeString(5);
        Randomizer.getRandomNumberBetween(1, 2);
        Randomizer.getRandomNumberBetween(1L, 2L);

        Wait.waitFor(1);

        YamlFile.compareFiles("", "");
    }
}
