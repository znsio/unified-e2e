package com.znsio.teswiz.runner;

import com.applitools.eyes.*;
import com.applitools.eyes.appium.AppiumCheckSettings;
import com.applitools.eyes.selenium.*;
import com.applitools.eyes.selenium.fluent.SeleniumCheckSettings;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.RenderBrowserInfo;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.znsio.teswiz.entities.APPLITOOLS;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.TEST_CONTEXT;
import com.znsio.teswiz.exceptions.InvalidTestDataException;
import com.znsio.teswiz.exceptions.VisualTestSetupException;
import com.znsio.teswiz.tools.ReportPortalLogger;
import com.znsio.teswiz.tools.ScreenShotManager;
import com.znsio.teswiz.tools.Wait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.assertj.core.api.SoftAssertions;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.znsio.teswiz.runner.Runner.*;
import static java.lang.String.format;
import static java.lang.String.valueOf;

public class Visual {
    private static final Logger LOGGER = LogManager.getLogger(Visual.class.getName());
    private static final String DEFAULT_APPLITOOLS_SERVER_URL = "https://eyesapi.applitools.com";
    private static final int DEFAULT_UFG_CONCURRENCY = 5;
    private static final String VISUAL_TESTING_DIFFERENCES_FOUND = "Visual testing differences " +
                                                                   "found? - " + "%s";
    private static final String APP_CHECK_WINDOW_TIME_TAKEN = "%s :%s:: App: checkWindow: Time " +
                                                              "taken: %d" + " sec ";
    private static final String WEB_CHECK_WINDOW_TIME_TAKEN = "%s :%s:: Web: checkWindow: Time " +
                                                              "taken: %d" + " sec ";
    private final Eyes eyesOnWeb;
    private final com.applitools.eyes.appium.Eyes eyesOnApp;
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final ScreenShotManager screenShotManager;
    private final String targetEnvironment = getTargetEnvironment();
    private final Map applitoolsConfig;
    private final boolean isEnableBenchmarkPerValidation;
    private final boolean isVerboseLoggingEnabled;
    private final WebDriver innerDriver;
    private final String userPersona;
    private String applitoolsLogFileNameForWeb = NOT_SET;
    private EyesRunner seleniumEyesRunner;

    public Visual(String driverType, Platform platform, WebDriver innerDriver, String testName,
            String userPersona, String appName) {
        boolean isVisualTestingEnabled = isVisualTestingEnabled();
        LOGGER.debug(format(
                "Visual constructor: Driver type: %s, platform: %s, testName: %s, " +
                "isVisualTestingEnabled:  %s",
                driverType, platform.name(), testName, isVisualTestingEnabled));
        this.context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        long threadId = Thread.currentThread().getId();
        this.softly = getSoftAssertion(threadId);
        this.screenShotManager = (ScreenShotManager) context.getTestState(
                TEST_CONTEXT.SCREENSHOT_MANAGER);
        this.applitoolsConfig = getApplitoolsConfiguration();
        this.isEnableBenchmarkPerValidation = Boolean.parseBoolean(valueOf(
                this.applitoolsConfig.get(APPLITOOLS.ENABLE_BENCHMARK_PER_VALIDATION)));
        this.innerDriver = innerDriver;
        this.isVerboseLoggingEnabled = getValueFromConfig(APPLITOOLS.SHOW_LOGS, true);
        this.userPersona = userPersona;
        appName = appName.equalsIgnoreCase(DEFAULT) ? (String) this.applitoolsConfig.get(
                APPLITOOLS.APP_NAME) : appName;
        eyesOnApp = instantiateAppiumEyes(driverType, platform, innerDriver, appName, testName,
                                          isVisualTestingEnabled);
        eyesOnWeb = instantiateWebEyes(driverType, platform, innerDriver, appName, testName,
                                       isVisualTestingEnabled);
    }

    public Visual(String driverType, Platform platform, String testName, String userPersona, String pdfFileName) {
        boolean isVisualTestingEnabled = isVisualTestingEnabled();
        File pdfFile = new File(pdfFileName);
        this.eyesOnApp = null;
        this.eyesOnWeb = null;
        LOGGER.debug(format(
                "Visual constructor: Driver type: %s, platform: %s, testName: %s, " +
                "pdfFileName:  %s",
                "isVisualTestingEnabled:  %s",
                driverType, platform.name(), testName, pdfFile.getName(), isVisualTestingEnabled));
        this.context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        long threadId = Thread.currentThread().getId();
        this.softly = getSoftAssertion(threadId);
        this.screenShotManager = (ScreenShotManager) context.getTestState(
                TEST_CONTEXT.SCREENSHOT_MANAGER);
        this.applitoolsConfig = getApplitoolsConfiguration();
        this.isEnableBenchmarkPerValidation = Boolean.parseBoolean(valueOf(
                this.applitoolsConfig.get(APPLITOOLS.ENABLE_BENCHMARK_PER_VALIDATION)));
        this.innerDriver = null;
        this.isVerboseLoggingEnabled = getValueFromConfig(APPLITOOLS.SHOW_LOGS, true);
        this.userPersona = userPersona;
        this.context.addTestState(TEST_CONTEXT.PDF_FILE_NAME, pdfFileName);
    }

    public TestResults validatePdf() {
        return validatePdf(context.getTestStateAsString(TEST_CONTEXT.PDF_FILE_NAME), null);
    }

    public TestResults validatePdf(int[] pageNumbers) {
        return validatePdf(context.getTestStateAsString(TEST_CONTEXT.PDF_FILE_NAME), pageNumbers);
    }

    public TestResults validatePdf(String pdfFileName) {
        return validatePdf(pdfFileName, null);
    }

    public TestResults validatePdf(String pdfFileName, int[] pageNumbers) {
        File pdfFile = new File(pdfFileName);
        checkIfPDFFileExists(pdfFileName, pdfFile);
        boolean isVisualTestingEnabled = isVisualTestingEnabled();
        LOGGER.info("validatePdf: isVisualTestingEnabled: %s".formatted(isVisualTestingEnabled));

        if (!isVisualTestingEnabled) {
            throw new VisualTestSetupException("Set IS_VISUAL=true to validate PDF file: %s".formatted(pdfFileName));
        }

        com.applitools.eyes.images.Eyes eyesImages = new com.applitools.eyes.images.Eyes();

        String appName = (String) applitoolsConfig.get(APPLITOOLS.APP_NAME);
        eyesImages.setConfiguration(configureExecutionForPDF(isVisualTestingEnabled, appName));
        eyesImages.setIsDisabled(!isVisualTestingEnabled);
        addCustomPropertiesInPDFTestExecution(eyesImages);

        String applitoolsLogFileNameForPDF = getApplitoolsLogFileNameFor(Platform.pdf.name());
        eyesImages.setLogHandler(new FileLogger(applitoolsLogFileNameForPDF, true, isVerboseLoggingEnabled));

        String pdfTestName = getPdfTestName(pdfFileName, pageNumbers, pdfFile);
        eyesImagesOpen(eyesImages, appName, pdfTestName);

        if (isVisualTestingEnabled) {
            loadPDFAndValidate(pdfFile, eyesImages, pageNumbers);
        }

        TestResults testResults = eyesImages.close(false);

        checkEachTestVisualResults(userPersona, Platform.pdf.name(), null, testResults);
        return testResults;
    }

    private static @NotNull String getPdfTestName(String pdfFileName, int[] pageNumbers, File pdfFile) {
        String pdfTestName = pdfFile.getName();
        if (null != pageNumbers && pageNumbers.length > 0) {
            String pdfFileNameWithoutExtension = pdfTestName.contains(".")
                                                 ? pdfTestName.substring(0, pdfTestName.lastIndexOf('.'))
                                                 : pdfTestName;
            pdfTestName = pdfFileNameWithoutExtension + "-" + Arrays.stream(pageNumbers)
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining(","));
            String extension = pdfFileName.contains(".")
                               ? pdfFileName.substring(pdfFileName.lastIndexOf('.') + 1)
                               : "";
            pdfTestName += "." + extension;
        }
        return pdfTestName;
    }

    private static void loadPDFAndValidate(File pdfFile, com.applitools.eyes.images.Eyes eyesImages, int[] pageNumbers) {
        try (PDDocument document = Loader.loadPDF(new RandomAccessReadBufferedFile(pdfFile.getAbsolutePath()))) {

            int[] pagesToProcess = getPagesToProcess(pageNumbers, document);
            PDFRenderer renderer = new PDFRenderer(document);
            String pageNamePrefix = pdfFile.getName() + "-";
            String pdfReadingMessage = "\n\tLoad pdf '" + pdfFile.getAbsolutePath() + "' and validate all pages";
            int concurrency = getValueFromConfig(APPLITOOLS.CONCURRENCY, DEFAULT_UFG_CONCURRENCY);

            for (int pageNum : pagesToProcess) {
                BufferedImage image = renderer.renderImage(pageNum - 1);
                pdfReadingMessage += "\n\t\tProcessing page: " + (pageNum);
                eyesImages.check(pageNamePrefix + pageNum, com.applitools.eyes.images.Target.image(image));

                if (0 == pageNum % concurrency) {
                    int seconds = 5;
                    LOGGER.info("Processing page %d. Wait for %d before proceeding".formatted(pageNum, seconds));
                    Wait.waitFor(seconds);
                }
            }
            LOGGER.info(pdfReadingMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static int[] getPagesToProcess(int[] pageNumbers, PDDocument document) {
        int totalPages = document.getNumberOfPages();

        // Determine the pages to process (1-based indexing)
        int[] pagesToProcess = (pageNumbers == null || pageNumbers.length == 0)
                               ? java.util.stream.IntStream.rangeClosed(1, totalPages).toArray() // 1-based indexing
                               : pageNumbers;

        LOGGER.info("Provided Page numbers to process (1-based): " + java.util.Arrays.toString(pagesToProcess));

        // Validate page numbers
        java.util.List<Integer> invalidPages = new java.util.ArrayList<>();
        for (int pageNum : pagesToProcess) {
            if (pageNum > totalPages || pageNum < 1) { // 1-based validation
                invalidPages.add(pageNum);
                LOGGER.warn("\n\tPage number " + pageNum + " is out of bounds for this PDF.");
            }
        }

        if (!invalidPages.isEmpty()) {
            throw new InvalidTestDataException("Invalid page numbers provided to process the pdf: " + invalidPages);
        }

        LOGGER.info("Valid Pages to process (1-based): " + java.util.Arrays.toString(pagesToProcess));
        return pagesToProcess;
    }


    private void eyesImagesOpen(com.applitools.eyes.images.Eyes eyesImages, String appName, String testName) {
        try {
            setProxyForPdfExecution(eyesImages);
            eyesImages.open(appName, testName);
            LOGGER.debug(format("instantiateWebEyes:  Is Applitools Visual Testing enabled? - %s", !eyesImages.getIsDisabled()));
        } catch (IllegalArgumentException | EyesException e) {
            String message = format(
                    "Exception in instantiating Applitools for Web: '%s', Closing Web-driver " +
                    "instance",
                    e.getMessage());
            LOGGER.error(message);
            innerDriver.quit();
            throw new VisualTestSetupException(message, e);
        }
    }

    private com.applitools.eyes.config.@NotNull Configuration configureExecutionForPDF(boolean isVisualTestingEnabled, String appName) {
        com.applitools.eyes.config.Configuration config = new com.applitools.eyes.config.Configuration();

        config.setServerUrl(
                getValueFromConfig(APPLITOOLS.SERVER_URL, DEFAULT_APPLITOOLS_SERVER_URL));
        config.setApiKey(getValueFromConfig(APPLITOOLS.API_KEY, NOT_SET));
        config.setApiKey(getApplitoolsAPIKey(isVisualTestingEnabled));
        config.setBatch((BatchInfo) getValueFromConfig(APPLITOOLS.BATCH_NAME));
        config.setBranchName(valueOf(getValueFromConfig(Setup.BRANCH_NAME)));
        config.setEnvironmentName(targetEnvironment);
        config.setMatchLevel((MatchLevel) getValueFromConfig(APPLITOOLS.DEFAULT_MATCH_LEVEL, MatchLevel.STRICT));
        config.setSaveNewTests(getValueFromConfig(APPLITOOLS.SAVE_NEW_TESTS_AS_BASELINE, true));
        config.setHostOS(OS_NAME);
        config.setHostApp(appName);
        return config;
    }

    private static void checkIfPDFFileExists(String pdfFileName, File pdfFile) {
        if (!pdfFile.exists() || !pdfFile.isFile()) {
            throw new InvalidTestDataException("PDF file: '%s' does not exist".formatted(pdfFileName));
        }
        if (!pdfFile.getName().endsWith(".pdf")) {
            throw new InvalidTestDataException("Invalid PDF file name provided: '%s'".formatted(pdfFileName));
        }
    }

    @NotNull
    private static HashMap<String, Object> parseVisualTestResults(RenderBrowserInfo browserInfo, TestResults result) {
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("Number of steps", result.getSteps());
        resultMap.put("Number of matches", result.getMatches());
        resultMap.put("Number of mismatches", result.getMismatches());
        resultMap.put("Number of missing", result.getMissing());
        resultMap.put("Number of strict matches", result.getStrictMatches());
        resultMap.put("Number of content matches", result.getContentMatches());
        resultMap.put("Number of layout matches", result.getLayoutMatches());
        resultMap.put("Number of no matches", result.getNoneMatches());
        resultMap.put("Result url", result.getUrl());
        resultMap.put("Status", result.getStatus());
        resultMap.put("Duration", result.getDuration());
        resultMap.put("Accessibility status", result.getAccessibilityStatus());
        resultMap.put("Is passed?", result.isPassed());
        resultMap.put("Is aborted?", result.isAborted());
        resultMap.put("Is new?", result.isNew());
        resultMap.put("Is difference?", result.isDifferent());
        if (null != browserInfo) {
            resultMap.put("Browser/Device info", browserInfo.toString());
        }
        return resultMap;
    }

    private boolean getValueFromConfig(String key, boolean defaultValue) {
        return (null == applitoolsConfig.get(key)) ? defaultValue : Boolean.parseBoolean(
                valueOf(applitoolsConfig.get(key)));
    }

    private com.applitools.eyes.appium.Eyes instantiateAppiumEyes(String driverType,
            Platform platform,
            WebDriver innerDriver,
            String appName, String testName,
            boolean isVisualTestingEnabled) {
        if (driverType.equals(Driver.WEB_DRIVER)) {
            isVisualTestingEnabled = false;
        }
        LOGGER.debug(format("instantiateAppiumEyes: isVisualTestingEnabled: %s",
                            isVisualTestingEnabled));
        com.applitools.eyes.appium.Eyes appEyes = new com.applitools.eyes.appium.Eyes();

        configureExecutionForApp(isVisualTestingEnabled, appEyes);

        appName = appName + "-" + platform;
        addCustomPropertiesInAppTestExecution(platform, appEyes);
        try {
            setProxyForAppExecution(appEyes);
            appEyes.open(innerDriver, appName, testName);
            LOGGER.debug(format("instantiateAppiumEyes: Is Applitools Visual Testing enabled? - %s", !appEyes.getIsDisabled()));
        } catch (IllegalArgumentException e) {
            String message = format(
                    "Exception in instantiating Applitools for App: '%s', Closing driver instance",
                    e.getMessage());
            LOGGER.error(message);
            innerDriver.quit();
            throw new VisualTestSetupException(message, e);
        }

        return appEyes;
    }

    private void setProxyForAppExecution(com.applitools.eyes.appium.Eyes appEyes) {
        String proxyUrl = (String) applitoolsConfig.get(APPLITOOLS.PROXY_URL);
        if (null != proxyUrl) {
            LOGGER.info(format("Set proxyUrl for appEyes: %s", proxyUrl));
            appEyes.setProxy(new ProxySettings(proxyUrl));
        } else {
            LOGGER.debug("proxyUrl is null. No proxy set for appEyes");
        }
    }

    private void configureExecutionForApp(boolean isVisualTestingEnabled, com.applitools.eyes.appium.Eyes appEyes) {
        appEyes.setServerUrl(
                getValueFromConfig(APPLITOOLS.SERVER_URL, DEFAULT_APPLITOOLS_SERVER_URL));
        appEyes.setApiKey(getApplitoolsAPIKey(isVisualTestingEnabled));
        appEyes.setBatch((BatchInfo) getValueFromConfig(APPLITOOLS.BATCH_NAME));
        appEyes.setBranchName(valueOf(getValueFromConfig(Setup.BRANCH_NAME)));
        appEyes.setEnvName(targetEnvironment);
        appEyes.setMatchLevel(
                (MatchLevel) getValueFromConfig(APPLITOOLS.DEFAULT_MATCH_LEVEL, MatchLevel.STRICT));
        appEyes.setIsDisabled(!isVisualTestingEnabled);
        appEyes.setSaveNewTests(getValueFromConfig(APPLITOOLS.SAVE_NEW_TESTS_AS_BASELINE, true));
        appEyes.setIgnoreDisplacements(getValueFromConfig(APPLITOOLS.IGNORE_DISPLACEMENT, true));
        appEyes.setIgnoreCaret(true);
        String applitoolsLogFileNameForApp = getApplitoolsLogFileNameFor("app");
        appEyes.setLogHandler(new FileLogger(applitoolsLogFileNameForApp, true, isVerboseLoggingEnabled));
    }

    private void addCustomPropertiesInAppTestExecution(Platform platform, com.applitools.eyes.appium.Eyes appEyes) {
        appEyes.addProperty("USER_PERSONA", userPersona);
        appEyes.addProperty("HOST_NAME", getHostName());
        appEyes.addProperty(Setup.BRANCH_NAME,
                            valueOf(getValueFromConfig(Setup.BRANCH_NAME)));
        appEyes.addProperty(Setup.PLATFORM, platform.name());
        appEyes.addProperty(Setup.RUN_IN_CI, valueOf(getValueFromConfig(Setup.RUN_IN_CI)));
        appEyes.addProperty(Setup.TARGET_ENVIRONMENT,
                            valueOf(getValueFromConfig(Setup.TARGET_ENVIRONMENT)));
        appEyes.addProperty("USER_NAME", USER_NAME);
    }

    private String getApplitoolsAPIKey(boolean isVisualTestingEnabled) {
        return isVisualTestingEnabled ? getValueFromConfig(APPLITOOLS.API_KEY, null)
                                      : getValueFromConfig(APPLITOOLS.API_KEY, NOT_SET);
    }

    private Eyes instantiateWebEyes(String driverType,
            Platform platform,
            WebDriver innerDriver,
            String appName, String testName,
            boolean isVisualTestingEnabled) {
        if (driverType.equals(Driver.APPIUM_DRIVER)) {
            isVisualTestingEnabled = false;
        }
        LOGGER.debug(format("instantiateWebEyes: isVisualTestingEnabled: %s",
                            isVisualTestingEnabled));
        boolean isUFG = getValueFromConfig(APPLITOOLS.USE_UFG, false);

        configureEyesRunnerForWeb(isUFG);
        Eyes webEyes = new Eyes(seleniumEyesRunner);
        Configuration configuration = configureUFGExecutionForWeb(isVisualTestingEnabled, webEyes, isUFG);

        webEyes.setConfiguration(configuration);

        applitoolsLogFileNameForWeb = getApplitoolsLogFileNameFor("web");
        webEyes.setIsDisabled(!isVisualTestingEnabled);
        webEyes.setLogHandler(new FileLogger(applitoolsLogFileNameForWeb, true, isVerboseLoggingEnabled));

        appName = appName + "-" + platform;
        addCustomPropertiesInWebTestExecution(platform, webEyes);

        RectangleSize setBrowserViewPortSize = getBrowserViewPortSize(driverType, innerDriver);
        LOGGER.info(format("Using browser dimensions for Applitools: %s", setBrowserViewPortSize));

        try {
            setProxyForWebExecution(webEyes);
            webEyes.open(innerDriver, appName, testName, setBrowserViewPortSize);
            LOGGER.debug(format("instantiateWebEyes:  Is Applitools Visual Testing enabled? - %s", !webEyes.getIsDisabled()));
        } catch (IllegalArgumentException | EyesException e) {
            String message = format(
                    "Exception in instantiating Applitools for Web: '%s', Closing Web-driver " +
                    "instance",
                    e.getMessage());
            LOGGER.error(message);
            innerDriver.quit();
            throw new VisualTestSetupException(message, e);

        }
        return webEyes;
    }

    private void configureEyesRunnerForWeb(boolean isUFG) {
        int ufgConcurrency = getValueFromConfig(APPLITOOLS.CONCURRENCY, DEFAULT_UFG_CONCURRENCY);
        seleniumEyesRunner = isUFG ? new VisualGridRunner(ufgConcurrency) : new ClassicRunner();
        seleniumEyesRunner.setDontCloseBatches(true);
    }

    private void setProxyForWebExecution(Eyes webEyes) {
        String proxyUrl = (String) applitoolsConfig.get(APPLITOOLS.PROXY_URL);
        if (null != proxyUrl) {
            LOGGER.info(format("Set proxyUrl for webEyes: %s", proxyUrl));
            webEyes.setProxy(new ProxySettings(proxyUrl));
        } else {
            LOGGER.debug("proxyUrl is null. No proxy set for webEyes");
        }
    }

    private void setProxyForPdfExecution(com.applitools.eyes.images.Eyes eyesImages) {
        String proxyUrl = (String) applitoolsConfig.get(APPLITOOLS.PROXY_URL);
        if (null != proxyUrl) {
            LOGGER.info(format("Set proxyUrl for eyesImages: %s", proxyUrl));
            eyesImages.setProxy(new ProxySettings(proxyUrl));
        } else {
            LOGGER.debug("proxyUrl is null. No proxy set for eyesImages");
        }
    }

    private void addCustomPropertiesInPDFTestExecution(com.applitools.eyes.images.Eyes eyesImages) {
        eyesImages.addProperty("USER_PERSONA", userPersona);
        eyesImages.addProperty("HOST_NAME", getHostName());
        eyesImages.addProperty(Setup.BRANCH_NAME,
                               valueOf(getValueFromConfig(Setup.BRANCH_NAME)));
        eyesImages.addProperty(Setup.RUN_IN_CI, valueOf(getValueFromConfig(Setup.RUN_IN_CI)));
        eyesImages.addProperty(Setup.TARGET_ENVIRONMENT,
                               valueOf(getValueFromConfig(Setup.TARGET_ENVIRONMENT)));
        eyesImages.addProperty("USER_NAME", USER_NAME);
    }

    private void addCustomPropertiesInWebTestExecution(Platform platform, Eyes webEyes) {
        webEyes.addProperty("USER_PERSONA", userPersona);
        webEyes.addProperty("HOST_NAME", getHostName());
        webEyes.addProperty(Setup.BRANCH_NAME,
                            valueOf(getValueFromConfig(Setup.BRANCH_NAME)));
        webEyes.addProperty(Setup.PLATFORM, platform.name());
        webEyes.addProperty(Setup.RUN_IN_CI, valueOf(getValueFromConfig(Setup.RUN_IN_CI)));
        webEyes.addProperty(Setup.TARGET_ENVIRONMENT,
                            valueOf(getValueFromConfig(Setup.TARGET_ENVIRONMENT)));
        webEyes.addProperty("USER_NAME", USER_NAME);
    }

    @NotNull
    private Configuration configureUFGExecutionForWeb(boolean isVisualTestingEnabled, Eyes webEyes, boolean isUFG) {
        Configuration configuration = webEyes.getConfiguration();
        if (isUFG) {
            configuration.setBrowsersInfo(addBrowserAndDeviceConfigForUFG(isUFG, configuration));
        }
        configuration.setServerUrl(
                getValueFromConfig(APPLITOOLS.SERVER_URL, DEFAULT_APPLITOOLS_SERVER_URL));
        configuration.setApiKey(getValueFromConfig(APPLITOOLS.API_KEY, NOT_SET));
        configuration.setApiKey(getApplitoolsAPIKey(isVisualTestingEnabled));
        configuration.setBatch((BatchInfo) getValueFromConfig(APPLITOOLS.BATCH_NAME));

        configuration.setBranchName(valueOf(getValueFromConfig(Setup.BRANCH_NAME)));
        configuration.setEnvironmentName(targetEnvironment);
        configuration.setMatchLevel(
                (MatchLevel) getValueFromConfig(APPLITOOLS.DEFAULT_MATCH_LEVEL, MatchLevel.STRICT));

        configuration.setDisableBrowserFetching(
                getValueFromConfig(APPLITOOLS.DISABLE_BROWSER_FETCHING, true));
        configuration.setSendDom(getValueFromConfig(APPLITOOLS.SEND_DOM, true));
        configuration.setStitchMode(StitchMode.valueOf(
                valueOf(getValueFromConfig(APPLITOOLS.STITCH_MODE, StitchMode.CSS))
                        .toUpperCase()));
        configuration.setForceFullPageScreenshot(
                getValueFromConfig(APPLITOOLS.TAKE_FULL_PAGE_SCREENSHOT, true));
        configuration.setSaveNewTests(
                getValueFromConfig(APPLITOOLS.SAVE_NEW_TESTS_AS_BASELINE, true));
        return configuration;
    }

    private String getValueFromConfig(String key, String defaultValue) {
        return (null == applitoolsConfig.get(key) || applitoolsConfig.get(key).equals("null"))
               ? defaultValue : valueOf(applitoolsConfig.get(key));
    }

    private Object getValueFromConfig(String key) {
        return applitoolsConfig.get(key);
    }

    private Object getValueFromConfig(String key, Object defaultValue) {
        return (null == applitoolsConfig.get(key)) ? defaultValue : applitoolsConfig.get(key);
    }

    @NotNull
    private String getApplitoolsLogFileNameFor(String appType) {
        String scenarioLogDir = USER_DIRECTORY + context.getTestStateAsString(
                TEST_CONTEXT.SCENARIO_LOG_DIRECTORY);
        return format("%s%sdeviceLogs%sapplitools-%s.log", scenarioLogDir, File.separator,
                      File.separator, appType);
    }

    private static int getValueFromConfig(String key, int defaultValue) {
        Object valueFromConfig = getApplitoolsConfiguration().get(key);
        return (null == valueFromConfig) ? defaultValue
                                         : convertValueFromConfigToInt(valueFromConfig);
    }

    private List<RenderBrowserInfo> addBrowserAndDeviceConfigForUFG(boolean isUFG, Configuration ufgConfig) {
        if (null != context.getTestState(APPLITOOLS.UFG_CONFIG)) {
            ufgConfig = (Configuration) context.getTestState(APPLITOOLS.UFG_CONFIG);
            LOGGER.info(format("Using Browsers and devices in UFG_CONFIG provided by test: %s", ufgConfig.getBrowsersInfo()));
            return ufgConfig.getBrowsersInfo();
        } else {
            List<RenderBrowserInfo> defaultBrowserInfo = defaultApplitoolsUFGConfig();
            LOGGER.info(format("UFG_CONFIG NOT provided by test. Using default Browsers and devices in UFG_CONFIG: %s", defaultBrowserInfo));
            return defaultBrowserInfo;
        }
    }

    private RectangleSize getBrowserViewPortSize(String driverType, WebDriver innerDriver) {
        RectangleSize providedBrowserViewPortSizeFromConfig = (RectangleSize) getValueFromConfig(
                APPLITOOLS.RECTANGLE_SIZE);
        int providedBrowserViewPortSizeFromConfigHeight =
                providedBrowserViewPortSizeFromConfig.getHeight();
        int providedBrowserViewPortSizeFromConfigWidth =
                providedBrowserViewPortSizeFromConfig.getWidth();
        LOGGER.info(format("Provided browser dimensions: %s",
                           providedBrowserViewPortSizeFromConfig));

        if (driverType.equals(Driver.APPIUM_DRIVER)) {
            return providedBrowserViewPortSizeFromConfig;
        } else {
            JavascriptExecutor js = (JavascriptExecutor) innerDriver;
            Dimension actualBrowserSize;
            if (getPlatform().equals(Platform.electron)) {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                actualBrowserSize = new Dimension(toolkit.getScreenSize().width, toolkit.getScreenSize().height);
            } else {
                actualBrowserSize = innerDriver.manage().window().getSize();
            }
            LOGGER.info(format("Actual browser dimensions: %s", actualBrowserSize));
            Long actualHeight = (Long) js.executeScript("return (window.innerHeight);");
            Long actualWidth = (Long) js.executeScript("return (window.innerWidth);");

            if (providedBrowserViewPortSizeFromConfigHeight > actualHeight.intValue() || providedBrowserViewPortSizeFromConfigWidth > actualWidth.intValue()) {
                return new RectangleSize(actualWidth.intValue(), actualHeight.intValue());
            } else {
                return providedBrowserViewPortSizeFromConfig;
            }
        }
    }

    private static int convertValueFromConfigToInt(Object valueFromConfig) {
        try {
            return Integer.parseInt(valueOf(valueFromConfig));
        } catch (NumberFormatException e) {
            return (int) (Double.parseDouble(valueOf(valueFromConfig)));
        }
    }

    private List<RenderBrowserInfo> defaultApplitoolsUFGConfig() {
        Configuration ufgConfig = new Configuration();
        String applitoolsUFGConfigMessage = "Using default browser & device configuration for " +
                                            "Applitools Ultrafast Grid: ";
        ufgConfig.addBrowser(1024, 1024, BrowserType.CHROME);
        ufgConfig.addBrowser(1024, 1024, BrowserType.FIREFOX);
        ufgConfig.addBrowser(1024, 1024, BrowserType.SAFARI);
        ufgConfig.addBrowser(1024, 1024, BrowserType.EDGE_CHROMIUM);
        ufgConfig.addBrowser(1600, 1200, BrowserType.CHROME);
        ufgConfig.addBrowser(1600, 1200, BrowserType.FIREFOX);
        ufgConfig.addBrowser(1600, 1200, BrowserType.SAFARI);
        ufgConfig.addBrowser(1600, 1200, BrowserType.EDGE_CHROMIUM);
        ufgConfig.addDeviceEmulation(DeviceName.iPhone_X, ScreenOrientation.PORTRAIT);
        ufgConfig.addDeviceEmulation(DeviceName.iPad_Pro, ScreenOrientation.LANDSCAPE);
        ufgConfig.addDeviceEmulation(DeviceName.Nexus_5X, ScreenOrientation.PORTRAIT);
        ufgConfig.addDeviceEmulation(DeviceName.Nexus_6P, ScreenOrientation.LANDSCAPE);
        LOGGER.info(applitoolsUFGConfigMessage);
        ReportPortalLogger.logDebugMessage(applitoolsUFGConfigMessage + ufgConfig.getBrowsersInfo());
        return ufgConfig.getBrowsersInfo();
    }

    public Visual checkWindow(String fromScreen, String tag) {
        String formattedTagName = getFormattedTagName(fromScreen, tag);
        LOGGER.info(format("checkWindow: fromScreen: %s, tag: %s", fromScreen,
                           formattedTagName));
        LOGGER.debug(format("checkWindow: eyesOnWeb.getIsDisabled(): %s",
                            eyesOnWeb.getIsDisabled()));
        LOGGER.debug(format("checkWindow: eyesOnApp.getIsDisabled(): %s",
                            eyesOnApp.getIsDisabled()));

        LocalDateTime webStart = LocalDateTime.now();
        eyesOnWeb.checkWindow(formattedTagName);
        LocalDateTime webFinish = LocalDateTime.now();
        Duration webDuration = Duration.between(webStart, webFinish);
        if (isEnableBenchmarkPerValidation) {
            LOGGER.info(format(WEB_CHECK_WINDOW_TIME_TAKEN, fromScreen, tag,
                               webDuration.getSeconds()));
        }

        LocalDateTime appStart = LocalDateTime.now();
        eyesOnApp.checkWindow(formattedTagName);
        LocalDateTime appFinish = LocalDateTime.now();
        Duration appDuration = Duration.between(appStart, appFinish);
        if (isEnableBenchmarkPerValidation) {
            LOGGER.info(format(APP_CHECK_WINDOW_TIME_TAKEN, fromScreen, tag,
                               appDuration.getSeconds()));
        }

        screenShotManager.takeScreenShot(innerDriver, formattedTagName);
        return this;

    }

    @NotNull
    private String getFormattedTagName(String fromScreen, String tag) {
        return fromScreen + " : " + tag;
    }

    public Visual check(String fromScreen, String tag, SeleniumCheckSettings checkSettings) {
        String formattedTagName = getFormattedTagName(fromScreen, tag);
        LOGGER.info(format("check: fromScreen: %s, tag: %s", fromScreen, formattedTagName));
        LOGGER.debug(
                format("check: eyesOnWeb.getIsDisabled(): %s", eyesOnWeb.getIsDisabled()));
        LOGGER.debug(
                format("check: eyesOnApp.getIsDisabled(): %s", eyesOnApp.getIsDisabled()));

        LocalDateTime webStart = LocalDateTime.now();
        eyesOnWeb.check(formattedTagName, checkSettings);
        LocalDateTime webFinish = LocalDateTime.now();
        Duration webDuration = Duration.between(webStart, webFinish);
        if (isEnableBenchmarkPerValidation) {
            LOGGER.info(format(WEB_CHECK_WINDOW_TIME_TAKEN, fromScreen, tag,
                               webDuration.getSeconds()));
        }

        LocalDateTime appStart = LocalDateTime.now();
        eyesOnApp.check(formattedTagName, checkSettings);
        LocalDateTime appFinish = LocalDateTime.now();
        Duration appDuration = Duration.between(appStart, appFinish);
        if (isEnableBenchmarkPerValidation) {
            LOGGER.info(format(APP_CHECK_WINDOW_TIME_TAKEN, fromScreen, tag,
                               appDuration.getSeconds()));
        }

        screenShotManager.takeScreenShot(innerDriver, formattedTagName);
        return this;
    }

    public Visual check(String fromScreen, String tag, AppiumCheckSettings checkSettings) {
        String formattedTagName = getFormattedTagName(fromScreen, tag);
        LOGGER.info(format("check: fromScreen: %s, tag: %s", fromScreen, formattedTagName));
        LOGGER.debug(
                format("check: eyesOnWeb.getIsDisabled(): %s", eyesOnWeb.getIsDisabled()));
        LOGGER.debug(
                format("check: eyesOnApp.getIsDisabled(): %s", eyesOnApp.getIsDisabled()));

        LocalDateTime webStart = LocalDateTime.now();
        eyesOnWeb.check(formattedTagName, checkSettings);
        LocalDateTime webFinish = LocalDateTime.now();
        Duration webDuration = Duration.between(webStart, webFinish);
        if (isEnableBenchmarkPerValidation) {
            LOGGER.info(format(WEB_CHECK_WINDOW_TIME_TAKEN, fromScreen, tag,
                               webDuration.getSeconds()));
        }

        LocalDateTime appStart = LocalDateTime.now();
        eyesOnApp.check(formattedTagName, checkSettings);
        LocalDateTime appFinish = LocalDateTime.now();
        Duration appDuration = Duration.between(appStart, appFinish);
        if (isEnableBenchmarkPerValidation) {
            LOGGER.info(format(APP_CHECK_WINDOW_TIME_TAKEN, fromScreen, tag,
                               appDuration.getSeconds()));
        }

        screenShotManager.takeScreenShot(innerDriver, formattedTagName);
        return this;
    }

    public Visual checkWindow(String fromScreen, String tag, MatchLevel level) {
        String formattedTagName = getFormattedTagName(fromScreen, tag);
        LOGGER.info(
                format("checkWindow: fromScreen: %s, MatchLevel: %s, tag: %s", fromScreen,
                       level, formattedTagName));
        LOGGER.debug(format("checkWindow: eyesOnWeb.getIsDisabled(): %s",
                            eyesOnWeb.getIsDisabled()));
        LOGGER.debug(format("checkWindow: eyesOnApp.getIsDisabled(): %s",
                            eyesOnApp.getIsDisabled()));


        LocalDateTime webStart = LocalDateTime.now();
        eyesOnWeb.check(getFormattedTagName(fromScreen, tag), Target.window().matchLevel(level));
        LocalDateTime webFinish = LocalDateTime.now();
        Duration webDuration = Duration.between(webStart, webFinish);
        if (isEnableBenchmarkPerValidation) {
            LOGGER.info(format(
                    "%s:%s:: Web: checkWindow with MatchLevel: %s: Time taken: %d sec", fromScreen,
                    tag, level.name(), webDuration.getSeconds()));
        }

        LocalDateTime appStart = LocalDateTime.now();
        eyesOnApp.check(getFormattedTagName(fromScreen, tag), Target.window().matchLevel(level));
        LocalDateTime appFinish = LocalDateTime.now();
        Duration appDuration = Duration.between(appStart, appFinish);
        if (isEnableBenchmarkPerValidation) {
            LOGGER.info(format(
                    "%s:%s:: App: checkWindow with MatchLevel: %s: Time taken: %d sec", fromScreen,
                    tag, level.name(), appDuration.getSeconds()));
        }

        screenShotManager.takeScreenShot(innerDriver, getFormattedTagName(fromScreen, tag));
        return this;
    }

    public void handleTestResults(String userPersona, String driverType) {
        switch (driverType) {
            case Driver.WEB_DRIVER:
                getVisualResultsFromWeb(userPersona);
                break;

            case Driver.APPIUM_DRIVER:
                getVisualResultsFromApp(userPersona);
                break;

            case Driver.PDF_DRIVER:
                break;

            default:
                throw new InvalidTestDataException(
                        format("Unexpected driver type: '%s'", driverType));
        }
    }

    public Visual takeScreenshot(String fromScreen, String tag) {
        screenShotManager.takeScreenShot(innerDriver, getFormattedTagName(fromScreen, tag));
        return this;
    }

    private void getVisualResultsFromWeb(String userPersona) {
        if (Boolean.TRUE.equals(eyesOnWeb.getIsDisabled())) {
            return;
        }
        LOGGER.info(format("getVisualResultsFromWeb: user: %s", userPersona));
        eyesOnWeb.closeAsync();
        TestResultsSummary allTestResults = seleniumEyesRunner.getAllTestResults(false);
        checkVisualTestResults(allTestResults, userPersona, "web", applitoolsLogFileNameForWeb);
    }

    private void getVisualResultsFromApp(String userPersona) {
        if (Boolean.TRUE.equals(eyesOnApp.getIsDisabled())) {
            return;
        }
        LOGGER.info(format("getVisualResultsFromApp: user: %s", userPersona));
        TestResults allTestResults = eyesOnApp.close(false);
        checkEachTestVisualResults(userPersona, "app", null, allTestResults);
    }

    private void checkVisualTestResults(TestResultsSummary allTestResults, String userPersona,
            String onPlatform, String applitoolsLogFileName) {
        if (null != allTestResults) {
            for (TestResultContainer allTestResult : allTestResults) {
                TestResults result = allTestResult.getTestResults();
                checkEachTestVisualResults(userPersona, onPlatform, allTestResult.getBrowserInfo(),
                                           result);
            }
            LOGGER.info(format("Applitools logs available here: %s", applitoolsLogFileName));
        }
    }

    private void checkEachTestVisualResults(String userPersona, String onPlatform,
            RenderBrowserInfo browserInfo, TestResults result) {
        HashMap<String, Object> resultMap = parseVisualTestResults(browserInfo, result);

        logVisualTestResults(userPersona, onPlatform, resultMap);

        boolean areVisualDifferenceFound = result.getStatus()
                                                   .equals(TestResultsStatus.Unresolved) || result.getStatus()
                                                   .equals(TestResultsStatus.Failed);
        LOGGER.info(format(VISUAL_TESTING_DIFFERENCES_FOUND, areVisualDifferenceFound));
        softlyFailTestIfDifferencesFound(userPersona, onPlatform, result, areVisualDifferenceFound);
    }

    private void softlyFailTestIfDifferencesFound(String userPersona, String onPlatform, TestResults result, boolean areVisualDifferenceFound) {
        if (areVisualDifferenceFound) {
            ReportPortalLogger.logWarningMessage(
                    format(VISUAL_TESTING_DIFFERENCES_FOUND, areVisualDifferenceFound));
            if (shouldFailTestOnVisualDifference()) {
                softly.assertThat(areVisualDifferenceFound).as(format(
                        "Visual differences for user persona: '%s' on '%s' found in test: '%s'. See " +
                        "results here: ",
                        userPersona, onPlatform, context.getTestName()) + result.getUrl()).isFalse();
            } else {
                ReportPortalLogger.logInfoMessage("Not failing the tests because FAIL_TEST_ON_VISUAL_DIFFERENCE=false");
            }
        } else {
            ReportPortalLogger.logInfoMessage(
                    format(VISUAL_TESTING_DIFFERENCES_FOUND, areVisualDifferenceFound));
        }
    }

    private void logVisualTestResults(String userPersona, String onPlatform, HashMap<String, Object> resultMap) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultMap);
        } catch (JsonProcessingException e) {
            LOGGER.error(
                    format("ERROR parsing Applitools results as a map%n%s", e.getMessage()));
        }
        String message = format(
                "'%s' Visual Testing Results for user persona: '%s' :: Test: '%s'%n'%s'",
                onPlatform, userPersona, context.getTestName(), json);
        LOGGER.info(message);
        ReportPortalLogger.logDebugMessage(message);
    }
}
