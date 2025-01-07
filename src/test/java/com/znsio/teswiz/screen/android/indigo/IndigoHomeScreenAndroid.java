package com.znsio.teswiz.screen.android.indigo;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.indigo.IndigoFlightSearchResultsScreen;
import com.znsio.teswiz.screen.indigo.IndigoGiftVouchersScreen;
import com.znsio.teswiz.screen.indigo.IndigoHomeScreen;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class IndigoHomeScreenAndroid
        extends IndigoHomeScreen {
    private static final String SCREEN_NAME = IndigoHomeScreenAndroid.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final String NOT_YET_IMPLEMENTED = " not yet implemented";
    private static final By byContinueAsGuestId = By.id("in.goindigo.android:id/button_as_guest");
    private static final By byGiftVoucherXpath = By.xpath(
            "//android.widget.TextView[@text='Gift voucher']/..");
    private final Driver driver;
    private final Visual visually;

    public IndigoHomeScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
        WebElement continueAsGuestElement = this.driver.waitForClickabilityOf(byContinueAsGuestId,
                                                                              20);
        visually.checkWindow(SCREEN_NAME, "Launch screen");
        continueAsGuestElement.click();
    }

    @Override
    public IndigoHomeScreen selectFrom(String from) {
        throw new NotImplementedException(
                SCREEN_NAME + ":" + new Throwable().getStackTrace()[0].getMethodName() + NOT_YET_IMPLEMENTED);
    }

    @Override
    public IndigoHomeScreen selectTo(String destination) {
        throw new NotImplementedException(
                SCREEN_NAME + ":" + new Throwable().getStackTrace()[0].getMethodName() + NOT_YET_IMPLEMENTED);
    }

    @Override
    public IndigoHomeScreen selectNumberOfAdultPassengers(int numberOfAdults) {
        throw new NotImplementedException(
                SCREEN_NAME + ":" + new Throwable().getStackTrace()[0].getMethodName() + NOT_YET_IMPLEMENTED);
    }

    @Override
    public IndigoHomeScreen selectJourneyType(String journeyType) {
        throw new NotImplementedException(
                SCREEN_NAME + ":" + new Throwable().getStackTrace()[0].getMethodName() + NOT_YET_IMPLEMENTED);
    }

    @Override
    public IndigoFlightSearchResultsScreen searchFlightOptions() {
        throw new NotImplementedException(
                SCREEN_NAME + ":" + new Throwable().getStackTrace()[0].getMethodName() + NOT_YET_IMPLEMENTED);
    }

    @Override
    public IndigoGiftVouchersScreen selectGiftVouchers() {
        WebElement giftVoucherElement = driver.waitForClickabilityOf(byGiftVoucherXpath, 20);
        visually.checkWindow(SCREEN_NAME, "On Landing screen");
        giftVoucherElement.click();
        return IndigoGiftVouchersScreen.get();
    }
}
