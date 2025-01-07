package com.znsio.teswiz.businessLayer.theapp;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.screen.theapp.FileUploadScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class FileUploadBL {
    private static final Logger LOGGER = LogManager.getLogger(FileUploadBL.class.getName());
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    public FileUploadBL(String userPersona, Platform forPlatform) {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = userPersona;
        this.currentPlatform = forPlatform;
        Runner.setCurrentDriverForUser(userPersona, forPlatform, context);
    }

    public FileUploadBL() {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = SAMPLE_TEST_CONTEXT.ME;
        this.currentPlatform = Runner.getPlatform();
    }

    public FileUploadBL navigationToUploadScreen() {
        FileUploadScreen.get().navigateToFileUplaodPage();
        return this;
    }

    public FileUploadBL uploadFile(Map file) {
        FileUploadScreen.get().uploadFile(file);
        return this;
    }

    public FileUploadBL verifyFileUpload() {
        assertThat(FileUploadScreen.get().getFileUploadText()).as("File upload validation")
                .contains("File Uploaded!");
        return this;
    }


}
