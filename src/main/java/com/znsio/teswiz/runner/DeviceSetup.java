package com.znsio.teswiz.runner;

import com.google.gson.internal.LinkedTreeMap;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.exceptions.InvalidTestDataException;
import com.znsio.teswiz.tools.JsonFile;
import com.znsio.teswiz.tools.cmd.CommandLineExecutor;
import com.znsio.teswiz.tools.cmd.CommandLineResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.znsio.teswiz.runner.Runner.NOT_SET;
import static com.znsio.teswiz.runner.Setup.APP_PATH;
import static com.znsio.teswiz.runner.Setup.APP_VERSION;
import static com.znsio.teswiz.runner.Setup.CAPS;
import static com.znsio.teswiz.runner.Setup.EXECUTED_ON;
import static com.znsio.teswiz.runner.Setup.LOG_DIR;
import static com.znsio.teswiz.runner.Setup.PARALLEL;
import static com.znsio.teswiz.runner.Setup.PLUGIN;
import static com.znsio.teswiz.runner.Setup.RUN_IN_CI;

class DeviceSetup {
    private static final Logger LOGGER = LogManager.getLogger(DeviceSetup.class.getName());
    private static final String DEFAULT_TEMP_SAMPLE_APP_DIRECTORY =
            System.getProperty("user.dir") + File.separator +
                    "temp" + File.separator + "sampleApps";
    private static final String CLOUD_NAME_NOT_SUPPORTED_MESSAGE = "Provided cloudName: '%s' is not supported";
    private static final String CUCUMBER_SCENARIO_LISTENER = "com.cucumber.listener.CucumberScenarioListener";
    private static final String CUCUMBER_SCENARIO_REPORTER_LISTENER = "com.cucumber.listener.CucumberScenarioReporterListener";

    private DeviceSetup() {
        LOGGER.debug("DeviceSetup - private constructor");
    }

    static void saveNewCapabilitiesFile(String platformName, String capabilityFile,
                                        Map<String, Map> loadedCapabilityFile,
                                        ArrayList listOfDevices) {
        Object pluginConfig = ((LinkedTreeMap) loadedCapabilityFile.get("serverConfig").get("server")).get(
                "plugin");
        Map cloudConfig = (Map) ((LinkedTreeMap) ((LinkedTreeMap) pluginConfig).get("device-farm")).get(
                "cloud");
        cloudConfig.put("devices", listOfDevices);

        LOGGER.info(
                String.format("Updated Device Lab Capabilities file: %n%s", loadedCapabilityFile));

        String updatedCapabilitiesFile = getPathForFileInLogDir(capabilityFile);
        JsonFile.saveJsonToFile(loadedCapabilityFile, updatedCapabilitiesFile);
        Setup.addToConfigs(CAPS, updatedCapabilitiesFile);
    }

    static String getPathForFileInLogDir(String fullFilePath) {
        LOGGER.info(String.format("\tgetPathForFileInLogDir: fullFilePath: %s", fullFilePath));
        Path path = Paths.get(fullFilePath);
        String fileName = path.getFileName().toString();
        String newFileName = new File(
                Setup.getFromConfigs(LOG_DIR) + File.separator + fileName).getAbsolutePath();
        LOGGER.info(String.format("\tNew file available here: %s", newFileName));
        return newFileName;
    }

    static Collection<String> setupAndroidExecution() {
        ArrayList<String> androidCukeArgs = new ArrayList<>();
        if (Setup.getPlatform().equals(Platform.android)) {
            verifyAppExistsAtMentionedPath();
            fetchAndroidAppVersion();
            if (Setup.getBooleanValueFromConfigs(RUN_IN_CI)) {
                setupCloudExecution();
            } else {
                LocalDevicesSetup.setupLocalExecution();
            }
        }
        return androidCukeArgs;
    }

    static void verifyAppExistsAtMentionedPath() {
        String appPath = Setup.getFromConfigs(APP_PATH);
        LOGGER.info(String.format("Original path to apk/app: %s", appPath));
        if (appPath.equals(NOT_SET)) {
            if (null == Setup.getLoadedCapabilities().get(Runner.getPlatform().name()).get("browserName")) {
                appPath = downloadAppToDirectoryIfNeeded(getAppPathFromCapabilities(), DEFAULT_TEMP_SAMPLE_APP_DIRECTORY);
            }
            LOGGER.info(String.format("Updated path to apk/app: %s", appPath));
        } else {
            appPath = downloadAppToDirectoryIfNeeded(appPath, DEFAULT_TEMP_SAMPLE_APP_DIRECTORY);
            LOGGER.info(String.format("\tUsing AppPath provided as environment variable -  %s", appPath));
        }
        Setup.addToConfigs(APP_PATH, appPath);
    }

    public static String downloadAppToDirectoryIfNeeded(String appPath, String saveToLocalDirectory) {
        String fileName = new File(appPath).getName();
        String localFilePath = saveToLocalDirectory + File.separator + fileName;
        if (isAppPathAUrl(appPath)) {
            LOGGER.info(String.format("App url '%s' is provided in capabilities. Download it, if " +
                                              "not already available at '%s'", appPath, localFilePath));
            downloadFileIfDoesNotExist(appPath, localFilePath, saveToLocalDirectory);
            LOGGER.info("Changing value of appPath from URL to file path");
            LOGGER.info(String.format("Before change, appPath value: %s", appPath));
            appPath = localFilePath;
            LOGGER.info(String.format("After change, appPath value: %s", localFilePath));
        } else {
            LOGGER.info(String.format("App file path '%s' is provided in capabilities.", appPath));
            if (!(new File(appPath).exists())) {
                throw new InvalidTestDataException(String.format("App file path '%s' provided in capabilities is incorrect", appPath));
            }
        }
        LOGGER.info(String.format("App file path '%s' is provided in capabilities.", appPath));
        LOGGER.info(String.format("File available at App file path '%s'", appPath));
        return appPath;
    }

    private static void downloadFile(String url, String filePath, String saveToDirectory) {
        LOGGER.info(String.format("Downloading App from url: '%s'", url));
        try {
            URL fileUrl = new URL(url);
            HttpURLConnection connection = getHttpURLConnection(fileUrl);
            downloadFileFromHttpURL(filePath, saveToDirectory, connection);
            String formattedSize = getDownloadedAppSize(Path.of(filePath));
            LOGGER.info(String.format("App downloaded at path: '%s', having size: '%s MB'", filePath, formattedSize));
        } catch (IOException e) {
            throw new InvalidTestDataException("An error occurred while opening the URL/downloading file: " + e.getMessage());
        }
    }

    private static String getDownloadedAppSize(Path filePath) {
        long fileSizeBytes = 0;
        try {
            fileSizeBytes = Files.size(filePath);
        } catch (IOException e) {
            throw new InvalidTestDataException("Unable to get downloaded app file size. Download " +
                                                       "may be corrupt. Check and fix before " +
                                                       "rerunning the test.", e);
        }
        double fileSizeMB = (double) fileSizeBytes / (1024 * 1024);
        return new DecimalFormat("#.##").format(fileSizeMB);
    }

    private static void downloadFileFromHttpURL(String filePath, String saveToDirectory, HttpURLConnection connection)  {
        try (InputStream inputStream = connection.getInputStream()) {
            createDirectoryIfNotExists(saveToDirectory);
            Files.copy(inputStream, Path.of(filePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new InvalidTestDataException(String.format("Unable to download file '%s'", connection.getURL().toString()), e);
        }
    }

    @NotNull
    private static HttpURLConnection getHttpURLConnection(URL fileUrl) {
        try {
            HttpURLConnection connection = (HttpURLConnection) fileUrl.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new InvalidTestDataException(String.format("Unable to connect to url: '%s'. Got connection error '%d'", fileUrl, responseCode));
            }
            return connection;
        } catch (IOException e) {
            throw new InvalidTestDataException(String.format("Unable to connect to url: '%s'.", fileUrl));
        }
    }

    private static void createDirectoryIfNotExists(String directory) throws IOException {
        Path directoryPath = Path.of(directory);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
    }

    private static void downloadFileIfDoesNotExist(String appPath, String filePath, String saveToDirectory){
        if (!(new File(filePath).exists())) {
            LOGGER.info(String.format("App is not available at path: '%s'. Download it.", appPath));
            downloadFile(appPath, filePath, saveToDirectory);
        } else {
            LOGGER.info(String.format("App is already available at path: '%s'. No need to download it.", appPath));
        }
    }

    private static void fetchAndroidAppVersion() {
        Pattern versionNamePattern = Pattern.compile("versionName='(\\d+(\\.\\d+)+)'",
                Pattern.MULTILINE);
        String searchPattern = "grep";
        if (Runner.IS_WINDOWS) {
            searchPattern = "findstr";
        }

        try {
            File appFile = new File(Setup.getFromConfigs(APP_PATH));
            if (!isAppPathAUrl(appFile.getPath())) {
                String appFilePath = appFile.getCanonicalPath();
                String androidHomePath = System.getenv("ANDROID_HOME");
                File buildToolsFolder = new File(androidHomePath, "build-tools");
                File buildVersionFolder = Objects.requireNonNull(buildToolsFolder.listFiles())[0];
                File aaptExecutable = new File(buildVersionFolder, "aapt").getAbsoluteFile();

                String[] commandToGetAppVersion = new String[]{aaptExecutable.toString(), "dump",
                        "badging", appFilePath, "|",
                        searchPattern, "versionName"};
                fetchAppVersion(commandToGetAppVersion, versionNamePattern);
            }
        } catch (Exception e) {
            LOGGER.info(
                    String.format("fetchAndroidAppVersion: Exception: %s", e.getLocalizedMessage()));
        }
    }

    static void setupCloudExecution() {
        String cloudName = getCloudNameFromCapabilities();
        String deviceLabURL;
        switch (cloudName.toLowerCase()) {
            case "headspin":
                deviceLabURL = getCloudApiUrlFromCapabilities();
                HeadSpinSetup.updateHeadspinCapabilities(deviceLabURL);
                break;
            case "pcloudy":
                deviceLabURL = getCloudApiUrlFromCapabilities();
                PCloudySetup.updatePCloudyCapabilities(deviceLabURL);
                break;
            case "browserstack":
                deviceLabURL = getCloudApiUrlFromCapabilities();
                BrowserStackSetup.updateBrowserStackCapabilities(deviceLabURL);
                break;
            default:
                throw new InvalidTestDataException(
                    String.format(CLOUD_NAME_NOT_SUPPORTED_MESSAGE, cloudName));
        }
        Setup.addToConfigs(EXECUTED_ON, cloudName);
    }

    private static String getAppPathFromCapabilities() {
        String capabilityFile = Setup.getFromConfigs(CAPS);
        return JsonFile.getValueFromLoadedJsonMap(capabilityFile,
                new String[]{Setup.getPlatform().name(), "app"}, Setup.getLoadedCapabilities());
    }

    private static boolean isAppPathAUrl(String appPathUrl) {
        try {
            new URL(appPathUrl);
            LOGGER.info(String.format("'%s' is a URL.", appPathUrl));
            isAppUrlValid(appPathUrl);
            return true;
        } catch (MalformedURLException e) {
            LOGGER.info(String.format("'%s' is not a URL.", appPathUrl));
            return false;
        }
    }

    private static void isAppUrlValid(String appPathUrl) {
        int responseCode=999;
        String responseMessage=NOT_SET;
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URL(appPathUrl).openConnection();
            connection.setRequestMethod("HEAD");
            responseMessage = connection.getResponseMessage();
            responseCode = connection.getResponseCode();
            connection.disconnect();
        } catch (IOException e) {
            LOGGER.info(MessageFormat.format("isAppUrlValid response message: {0}'', responseCode: {1}", responseMessage, responseCode));
            throw new InvalidTestDataException(String.format("Failed to make a connection using url: '%s'", appPathUrl) + e);
        }

        if (responseCode != HttpURLConnection.HTTP_OK) {
            LOGGER.info(String.format("'%s' is an invalid URL.", appPathUrl));
            throw new InvalidTestDataException("URL is not accessible: " + appPathUrl);
        }
        LOGGER.info(String.format("'%s' is a valid URL.", appPathUrl));
    }

    private static void fetchAppVersion(String[] commandToGetAppVersion, Pattern pattern) {
        CommandLineResponse commandResponse = CommandLineExecutor.execCommand(
                commandToGetAppVersion);
        String commandOutput = commandResponse.getStdOut();
        if (!(null == commandOutput || commandOutput.isEmpty())) {
            Matcher matcher = pattern.matcher(commandOutput);
            if (matcher.find()) {
                Setup.addToConfigs(APP_VERSION, matcher.group(1));
                LOGGER.info(String.format("APP_VERSION: %s", matcher.group(1)));
            }
        } else {
            LOGGER.info("fetchAppVersion: " + commandResponse.getErrOut());
        }
    }

    static String getCloudNameFromCapabilities() {
        if (Runner.isRunningInCI() && !Runner.isAPI() && !Runner.isCLI() && !Runner.isPDF()) {
            String capabilityFile = Setup.getFromConfigs(CAPS);
            return JsonFile.getValueFromLoadedJsonMap(capabilityFile,
                    new String[]{"serverConfig", "server", "plugin",
                            "device-farm", "cloud", "cloudName"}, Setup.getLoadedCapabilities());
        } else {
            return NOT_SET;
        }
    }

    private static String getCloudUrlFromCapabilities() {
        String capabilityFile = Setup.getFromConfigs(CAPS);
        return JsonFile.getValueFromLoadedJsonMap(capabilityFile,
                new String[]{"serverConfig", "server", "plugin",
                        "device-farm", "cloud", "url"}, Setup.getLoadedCapabilities());
    }

    private static String getCloudApiUrlFromCapabilities() {
        String capabilityFile = Setup.getFromConfigs(CAPS);
        return JsonFile.getValueFromLoadedJsonMap(capabilityFile,
                new String[]{"serverConfig", "server", "plugin",
                        "device-farm", "cloud", "apiUrl"}, Setup.getLoadedCapabilities());
    }

    static Collection<String> setupWindowsExecution() {
        ArrayList<String> windowsCukeArgs = new ArrayList<>();
        if (Setup.getPlatform().equals(Platform.windows)) {
            verifyAppExistsAtMentionedPath();
            fetchWindowsAppVersion();
            Setup.addToConfigs(EXECUTED_ON, "Local Desktop Apps");
        }
        return windowsCukeArgs;
    }

    private static void fetchWindowsAppVersion() {
        Pattern versionNamePattern = Pattern.compile("Version=(\\d+(\\.\\d+)+)", Pattern.MULTILINE);
        try {
            File appFile = new File(Setup.getFromConfigs(APP_PATH));
            String nameVariable = "name=\"" + appFile.getCanonicalPath()
                    .replace("\\", "\\\\") + "\"";
            String[] commandToGetAppVersion = new String[]{"wmic", "datafile", "where",
                    nameVariable, "get", "Version",
                    "/value"};
            fetchAppVersion(commandToGetAppVersion, versionNamePattern);
        } catch (IOException e) {
            LOGGER.info(
                    String.format("fetchWindowsAppVersion: Exception: %s", e.getLocalizedMessage()));
        }
    }

    static void cleanupCloudExecution() {
        String cloudName = getCloudNameFromCapabilities();
        switch (cloudName.toLowerCase()) {
            case "browserstack":
                BrowserStackSetup.cleanUp();
                break;
            case "headspin":
            case "pcloudy":
            case "saucelabs":
                LOGGER.info(String.format("No cleanup required for cloud: '%s'", cloudName));
                break;
            case "docker":
                LOGGER.info(String.format("No cleanup required for: '%s'", cloudName));
                break;
            default:
                throw new InvalidTestDataException(
                        String.format(CLOUD_NAME_NOT_SUPPORTED_MESSAGE, cloudName));
        }
    }

    static Collection<String> setupIOSExecution()  {
        ArrayList<String> iOSCukeArgs = new ArrayList<>();
        if (Setup.getPlatform().equals(Platform.iOS)) {
            verifyAppExistsAtMentionedPath();
//            TODO
//            fetchIOSAppVersion();
            if (Setup.getBooleanValueFromConfigs(RUN_IN_CI)) {
                setupCloudExecution();
            } else {
                LocalDevicesSetup.setupLocalIOSExecution();
            }
        }
        return iOSCukeArgs;
    }
}
