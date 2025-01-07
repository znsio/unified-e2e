package com.znsio.teswiz.runner;

import com.znsio.teswiz.exceptions.InvalidTestDataException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppPathTest {

    private static final Logger LOGGER = LogManager.getLogger(AppPathTest.class.getName());
    private static final String directoryPath = System.getProperty("user.dir") + File.separator + "temp" + File.separator + "unitTests" + File.separator + "sampleApps";
    private static final String fileName = "VodQA.apk";
    private static final String expectedAppPath = directoryPath + File.separator + fileName;
    private static final String appPathAsCorrectFilePath = expectedAppPath;
    private static final String appPathAsCorrectUrl = "https://github.com/anandbagmar/sampleAppsForNativeMobileAutomation/raw/main/VodQA.apk";
    private static final String appPathAsIncorrectUrl = "https://github.com/anandbagmar/sampleAppsForNativeMobileAutomation/ra/main/VodQA.apk";
    private static final String appPathAsIncorrectFilePath = System.getProperty("user.dir") + File.separator + "temp" + File.separator + "unitTests" + File.separator + "smleApps" + File.separator + fileName;

    @BeforeClass
    public static void setupBefore() {
        LOGGER.info("Using LOG_DIR: " + System.getProperty("LOG_DIR"));
    }

    @Test
    void givenIncorrectUrl_WhenDirectoryAndFileDoNotExist_ThenIOExceptionOccurWhileTryingToDownloadFile() {
        deleteDirectoryUsedForUnitTests();
        Assertions.assertThatThrownBy(() -> {
                    DeviceSetup.downloadAppToDirectoryIfNeeded(appPathAsIncorrectUrl, directoryPath);
                })
                .isInstanceOf(InvalidTestDataException.class) // Verify exception type
                .hasMessage("URL is not accessible: " + appPathAsIncorrectUrl);
    }

    @Test
    void givenIncorrectUrl_WhenDirectoryExistAndFileDoNotExist_ThenIOExceptionOccurWhileTryingToDownloadFile() {
        createDirectoryUsedForUnitTests();
        deleteFile(appPathAsCorrectFilePath);
        Assertions.assertThatThrownBy(() -> {
                    DeviceSetup.downloadAppToDirectoryIfNeeded(appPathAsIncorrectUrl, directoryPath);
                })
                .isInstanceOf(InvalidTestDataException.class)
                .hasMessage("URL is not accessible: " + appPathAsIncorrectUrl);
    }

    @Test
    void givenIncorrectUrl_WhenDirectoryAndFileBothExist_ThenFileIsReadable() {
        createDirectoryUsedForUnitTests();
        DeviceSetup.downloadAppToDirectoryIfNeeded(appPathAsCorrectUrl, directoryPath);
        Assertions.assertThatThrownBy(() -> {
                    DeviceSetup.downloadAppToDirectoryIfNeeded(appPathAsIncorrectUrl, directoryPath);
                })
                .isInstanceOf(InvalidTestDataException.class)
                .hasMessage("URL is not accessible: " + appPathAsIncorrectUrl);
    }

    @Test
    void givenCorrectUrl_WhenDirectoryAndFileDoNotExist_ThenCreateDirectoryAndDownloadFile() {
        deleteDirectoryUsedForUnitTests();
        String actualAppPath = DeviceSetup.downloadAppToDirectoryIfNeeded(appPathAsCorrectUrl, directoryPath);
        Assertions.assertThat(expectedAppPath).isEqualTo(actualAppPath);
        Assertions.assertThat(new File(actualAppPath).canRead()).isTrue();
    }

    @Test
    void givenCorrectUrl_WhenDirectoryExistButFileDoNotExist_ThenDownloadFile() {
        createDirectoryUsedForUnitTests();
        deleteFile(appPathAsCorrectFilePath);
        String actualAppPath = DeviceSetup.downloadAppToDirectoryIfNeeded(appPathAsCorrectUrl, directoryPath);
        Assertions.assertThat(expectedAppPath).isEqualTo(actualAppPath);
        Assertions.assertThat(new File(actualAppPath).canRead()).isTrue();
    }

    @Test
    void givenCorrectUrl_WhenDirectoryAndFileAlreadyExist_ThenDoNotDownloadFile() {
        createDirectoryUsedForUnitTests();
        DeviceSetup.downloadAppToDirectoryIfNeeded(appPathAsCorrectUrl, directoryPath);
        Assertions.assertThat(new File(expectedAppPath).canRead()).isTrue();
        String actualAppPath = DeviceSetup.downloadAppToDirectoryIfNeeded(appPathAsCorrectUrl, directoryPath);
        Assertions.assertThat(new File(actualAppPath).canRead()).isTrue();
        Assertions.assertThat(expectedAppPath).isEqualTo(actualAppPath);
    }

    @Test
    void givenIncorrectFilePath_WhenDirectoryAndFileDoNotExist_ThenFileIsNotReadable() {
        deleteDirectoryUsedForUnitTests();
        Assertions.assertThatThrownBy(() -> {
                    DeviceSetup.downloadAppToDirectoryIfNeeded(appPathAsIncorrectFilePath, directoryPath);
                })
                .isInstanceOf(InvalidTestDataException.class)
                .hasMessage(String.format("App file path '%s' provided in capabilities is incorrect", appPathAsIncorrectFilePath));
    }

    @Test
    void givenIncorrectFilePath_WhenDirectoryExistButFileDoNotExist_ThenFileIsNotReadable() {
        createDirectoryUsedForUnitTests();
        deleteFile(appPathAsCorrectFilePath);
        Assertions.assertThatThrownBy(() -> {
                    DeviceSetup.downloadAppToDirectoryIfNeeded(appPathAsIncorrectFilePath, directoryPath);
                })
                .isInstanceOf(InvalidTestDataException.class)
                .hasMessage(String.format("App file path '%s' provided in capabilities is incorrect", appPathAsIncorrectFilePath));
    }

    @Test
    void givenIncorrectFilePath_WhenDirectoryAndFileExist_ThenFileIsNotReadable() {
        createDirectoryUsedForUnitTests();
        Assertions.assertThatThrownBy(() -> {
                    DeviceSetup.downloadAppToDirectoryIfNeeded(appPathAsIncorrectFilePath, directoryPath);
                })
                .isInstanceOf(InvalidTestDataException.class)
                .hasMessage(String.format("App file path '%s' provided in capabilities is incorrect", appPathAsIncorrectFilePath));
    }

    @Test
    void givenCorrectFilePath_WhenDirectoryAndFileDoNotExist_ThenFileIsNotReadable() {
        deleteDirectoryUsedForUnitTests();
        Assertions.assertThatThrownBy(() -> {
                    DeviceSetup.downloadAppToDirectoryIfNeeded(appPathAsCorrectFilePath, directoryPath);
                })
                .isInstanceOf(RuntimeException.class)
                .hasMessage(String.format("App file path '%s' provided in capabilities is incorrect", appPathAsCorrectFilePath));
    }

    @Test
    void givenCorrectFilePath_WhenDirectoryExistButFileDoNotExist_ThenFileIsNotReadable() {
        createDirectoryUsedForUnitTests();
        deleteFile(appPathAsCorrectFilePath);
        Assertions.assertThatThrownBy(() -> {
                    DeviceSetup.downloadAppToDirectoryIfNeeded(appPathAsCorrectFilePath, directoryPath);
                })
                .isInstanceOf(RuntimeException.class)
                .hasMessage(String.format("App file path '%s' provided in capabilities is incorrect", appPathAsCorrectFilePath));
    }

    @Test
    void givenCorrectFilePath_WhenDirectoryAndFileAlreadyExist_ThenFileIsReadable() {
        createDirectoryUsedForUnitTests();
        DeviceSetup.downloadAppToDirectoryIfNeeded(appPathAsCorrectUrl, directoryPath);
        String actualAppPath = DeviceSetup.downloadAppToDirectoryIfNeeded(appPathAsCorrectFilePath, directoryPath);
        Assertions.assertThat(new File(actualAppPath).canRead()).isTrue();
        Assertions.assertThat(expectedAppPath).isEqualTo(actualAppPath);
    }

    private void deleteFile(String filePath) {
        try {
            Files.deleteIfExists(Path.of(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file for unit test: " + e.getMessage());
        }
    }

    private void deleteDirectoryUsedForUnitTests() {
        if (Files.exists(Paths.get(AppPathTest.directoryPath))) {
            try {
                Files.walk(Paths.get(AppPathTest.directoryPath))
                        .sorted(java.util.Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(java.io.File::delete);
            } catch (IOException e) {
                System.err.println("Failed to delete folder for unit test: " + e.getMessage());
            }
        }
    }

    private void createDirectoryUsedForUnitTests() {
        if (!Files.exists(Paths.get(AppPathTest.directoryPath))) {
            try {
                Files.createDirectories(Paths.get(AppPathTest.directoryPath));
            } catch (IOException e) {
                System.err.println("Failed to create directory: " + AppPathTest.directoryPath + " for unit test, error occurred" + e);
            }
        }
    }
}
