package com.znsio.teswiz.runner;

import com.znsio.teswiz.exceptions.InvalidTestDataException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.assertj.core.api.Assertions;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class VisualTest {
    private static final Logger LOGGER = LogManager.getLogger(VisualTest.class.getName());
    private static String pdfFileName;
    private static File pdfFile;
    private static PDDocument pdfDocument;

    @BeforeClass
    public static void setupBefore(ITestContext context) throws IOException {
        LOGGER.info("Using LOG_DIR: " + System.getProperty("LOG_DIR"));

        pdfFileName = "src/test/resources/pdf/Teswiz.pdf";
        pdfFile = new File(pdfFileName);
        pdfDocument = Loader.loadPDF(new RandomAccessReadBufferedFile(pdfFile.getAbsolutePath()));
    }

    @Test
    void processAllPagesByDefaultTest() throws IOException {
        int[] expectedPagesToProcess = new int[]{1, 2, 3};
        int[] pagesToProcess = Visual.getPagesToProcess(expectedPagesToProcess, pdfDocument);
        assertThat(pagesToProcess).isEqualTo(expectedPagesToProcess);
    }

    @Test
    void processAllPagesIfNullIsProvidedTest() throws IOException {
        int[] expectedPagesToProcess = new int[]{1, 2, 3};
        int[] pagesToProcess = Visual.getPagesToProcess(null, pdfDocument);
        assertThat(pagesToProcess).isEqualTo(expectedPagesToProcess);
    }

    @Test
    void processSpecificPagesByDefaultTest() throws IOException {
        int[] expectedPagesToProcess = new int[]{1, 3};
        int[] pagesToProcess = Visual.getPagesToProcess(expectedPagesToProcess, pdfDocument);
        assertThat(pagesToProcess).isEqualTo(expectedPagesToProcess);
    }

    @Test
    void allOutOfBoundPageNumberTest() throws IOException {
        int[] expectedPagesToProcess = new int[]{0, 19};
        Assertions.assertThatThrownBy(() -> Visual.getPagesToProcess(expectedPagesToProcess, pdfDocument))
                .isInstanceOf(InvalidTestDataException.class)
                .hasMessageContaining("Invalid page numbers provided to process the pdf: [0, 19]");
    }

    @Test
    void partialOfBoundPageNumberTest() throws IOException {
        int[] expectedPagesToProcess = new int[]{1, 19};
        Assertions.assertThatThrownBy(() -> Visual.getPagesToProcess(expectedPagesToProcess, pdfDocument))
                .isInstanceOf(InvalidTestDataException.class)
                .hasMessageContaining("Invalid page numbers provided to process the pdf: [19]");
    }

    @Test
    void onlyOutOfBoundPageNumberTest() throws IOException {
        int[] expectedPagesToProcess = new int[]{19};
        Assertions.assertThatThrownBy(() -> Visual.getPagesToProcess(expectedPagesToProcess, pdfDocument))
                .isInstanceOf(InvalidTestDataException.class)
                .hasMessageContaining("Invalid page numbers provided to process the pdf: [19]");
    }

    @Test
    void borderLineOutOfBoundPageNumberTest() throws IOException {
        int[] expectedPagesToProcess = new int[]{10};
        Assertions.assertThatThrownBy(() -> Visual.getPagesToProcess(expectedPagesToProcess, pdfDocument))
                .isInstanceOf(InvalidTestDataException.class)
                .hasMessageContaining("Invalid page numbers provided to process the pdf: [10]");
    }

}
