package com.znsio.teswiz.screen.android.indigo;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.indigo.IndigoGiftVouchersScreen;
import io.appium.java_client.AppiumDriver;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

import java.util.Set;

public class IndigoGiftVouchersScreenAndroid
        extends IndigoGiftVouchersScreen {
    private static final String SCREEN_NAME = IndigoGiftVouchersScreenAndroid.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final String NOT_YET_IMPLEMENTED = " not yet implemented";
    private static final By bySelectDenominationDropdownXpath = By.xpath(
            "//android.widget.TextView[@text='Select Denomination']");
    private static final String bySelectValueFromDropdown = "//android.widget" +
                                                            ".CheckedTextView[@text='%s']";
    private static final By bySelectQuantityDropdownXpath = By.xpath(
            "//android.widget.TextView[@text='Select']");
    private static final By byPreviewButtonXpath = By.xpath(
            "//android.widget.Button[@text='Preview']");
    private static final By byTotalAmountId = By.id("in.goindigo.android:id/lblTotal");
    private static final String byGiftValueTitleXpath = "//android.widget.TextView[@text='Gift " +
                                                        "Voucher Denomination']";
    private final Driver driver;
    private final Visual visually;

    public IndigoGiftVouchersScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
        driver.waitTillElementIsVisible(By.xpath(byGiftValueTitleXpath), 20);
        visually.checkWindow(SCREEN_NAME, "On Gift Vouchers screen");
    }

    @Override
    public IndigoGiftVouchersScreen select(String numberOfGiftVouchersToPurchase,
            String denomination) {
        throw new NotImplementedException(
                SCREEN_NAME + ":" + new Throwable().getStackTrace()[0].getMethodName() + NOT_YET_IMPLEMENTED);
    }

    @Override
    public int getTotalPrice() {
        throw new NotImplementedException(
                SCREEN_NAME + ":" + new Throwable().getStackTrace()[0].getMethodName() + NOT_YET_IMPLEMENTED);
    }

    @Override
    public IndigoGiftVouchersScreen select(String numberOfGiftVouchersToPurchase,
            String denomination, String forWhom,
            String customMessage) {
        driver.findElement(bySelectDenominationDropdownXpath).click();
        String denominationToSelectXpath = String.format(bySelectValueFromDropdown, denomination);
        driver.waitTillElementIsVisible(By.xpath(denominationToSelectXpath)).click();
        visually.checkWindow(SCREEN_NAME, "Selected voucher denomination");
        driver.waitTillElementIsVisible(bySelectQuantityDropdownXpath).click();
        String quantitytoSelectXpath = String.format(bySelectValueFromDropdown,
                                                     numberOfGiftVouchersToPurchase);
        driver.waitTillElementIsVisible(By.xpath(quantitytoSelectXpath)).click();
        visually.checkWindow(SCREEN_NAME, "Selected voucher quantity");
        Set<String> contextNames = ((AppiumDriver) driver.getInnerDriver()).getWindowHandles();
        driver.scrollDownByScreenSize();

        String lblTotal = driver.findElement(byTotalAmountId).getText();
        LOGGER.info("Total amount: " + lblTotal);
        driver.findElement(By.id("chkPersonal")).click();
        visually.checkWindow(SCREEN_NAME, "Personalise gift voucher selected");

        return this;
    }

    @Override
    public IndigoGiftVouchersScreen preview() {
        throw new NotImplementedException(
                SCREEN_NAME + ":" + new Throwable().getStackTrace()[0].getMethodName() + NOT_YET_IMPLEMENTED);
    }
}
