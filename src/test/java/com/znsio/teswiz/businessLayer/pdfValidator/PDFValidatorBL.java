package com.znsio.teswiz.businessLayer.pdfValidator;

import com.applitools.eyes.TestResults;
import com.applitools.eyes.TestResultsStatus;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.TEST_CONTEXT;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.screen.pdfValidator.PDFValidatorScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;

import java.util.Arrays;

public class PDFValidatorBL {
    private static final Logger LOGGER = LogManager.getLogger(PDFValidatorBL.class.getName());
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    public PDFValidatorBL(String userPersona, Platform platform) {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = userPersona;
        this.currentPlatform = platform;
    }

    public PDFValidatorBL validate(String pdfFileName) {
        TestResults testResults = PDFValidatorScreen.get().validatePDF(pdfFileName);
        testResults.getStatus().equals(TestResultsStatus.Passed);
        softly.assertThat(testResults.getStatus()).as("PDF validation failed for file: %s".formatted(pdfFileName)).isEqualTo(TestResultsStatus.Passed);
        return this;
    }

    public PDFValidatorBL validateStandalonePDFFile() {
        String pdfFileName = context.getTestStateAsString(TEST_CONTEXT.PDF_FILE_NAME);
        LOGGER.info("Validating standalone PDF file: %%s%s".formatted(pdfFileName));
        TestResults testResults = PDFValidatorScreen.get().validatePDF();
        testResults.getStatus().equals(TestResultsStatus.Passed);
        softly.assertThat(testResults.getStatus()).as("PDF validation failed for file: '%s'".formatted(pdfFileName)).isEqualTo(TestResultsStatus.Passed);
        return this;
    }

    public PDFValidatorBL validate(String pdfFileName, int[] intArray) {
        LOGGER.info("Validate page numbers: '%s' of PDF file: '%s'".formatted(pdfFileName, Arrays.toString(intArray)));
        TestResults testResults = PDFValidatorScreen.get().validatePDF(pdfFileName, intArray);
        testResults.getStatus().equals(TestResultsStatus.Passed);
        softly.assertThat(testResults.getStatus()).as("PDF validation failed for file: '%s'".formatted(pdfFileName)).isEqualTo(TestResultsStatus.Passed);
        return this;
    }

    public PDFValidatorBL validateStandalonePDFFile(int[] intArray) {
        String pdfFileName = context.getTestStateAsString(TEST_CONTEXT.PDF_FILE_NAME);
        LOGGER.info("Validating page numbers: '%s' of standalone PDF file: '%s'".formatted(Arrays.toString(intArray), pdfFileName));
        TestResults testResults = PDFValidatorScreen.get().validatePDF(intArray);
        testResults.getStatus().equals(TestResultsStatus.Passed);
        softly.assertThat(testResults.getStatus()).as("PDF validation failed for file: %s".formatted(pdfFileName)).isEqualTo(TestResultsStatus.Passed);
        return this;
    }
}
