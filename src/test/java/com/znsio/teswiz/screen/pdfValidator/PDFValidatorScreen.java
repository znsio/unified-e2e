package com.znsio.teswiz.screen.pdfValidator;

import com.applitools.eyes.TestResults;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.android.PDFValidator.PDFValidatorScreenAndroid;
import com.znsio.teswiz.screen.pdf.PDFValidatorScreenPDF;
import com.znsio.teswiz.screen.web.PDFValidator.PDFValidatorScreenWeb;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PDFValidatorScreen {
    private static final String SCREEN_NAME = PDFValidatorScreen.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);

    public static PDFValidatorScreen get() {
        Driver driver = Drivers.getDriverForCurrentUser(Thread.currentThread().getId());
        Platform platform = Runner.fetchPlatform(Thread.currentThread().getId());
        LOGGER.info(SCREEN_NAME + ": Driver type: " + driver.getType() + ": Platform: " + platform);
        Visual visually = Drivers.getVisualDriverForCurrentUser(Thread.currentThread().getId());

        switch (platform) {
            case web:
                return new PDFValidatorScreenWeb(driver, visually);
            case android:
                return new PDFValidatorScreenAndroid(driver, visually);
            case pdf:
                return new PDFValidatorScreenPDF(driver, visually);
        }
        throw new NotImplementedException(
                SCREEN_NAME + " is not implemented in " + Runner.getPlatform());
    }

    public abstract TestResults validatePDF();

    public abstract TestResults validatePDF(int[] pageNumbers);

    public abstract TestResults validatePDF(String pdfFileName);

    public abstract TestResults validatePDF(String pdfFileName, int[] pageNumbers);
}
