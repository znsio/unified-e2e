package com.znsio.teswiz.screen.web.indigo;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.indigo.IndigoFlightSearchResultsScreen;
import com.znsio.teswiz.screen.indigo.IndigoGiftVouchersScreen;
import com.znsio.teswiz.screen.indigo.IndigoHomeScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static com.znsio.teswiz.tools.Wait.waitFor;

public class IndigoHomeScreenWeb
        extends IndigoHomeScreen {
    private static final String SCREEN_NAME = IndigoHomeScreenWeb.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final String NOT_YET_IMPLEMENTED = " not yet implemented";
    private static final By byFromXpath = By.xpath("//input[@placeholder='From']");
    private static final By byToXpath = By.xpath("//input[@placeholder='To']");
    private static final String byFromDropdown = "..//div[@data-name='%s']";
    private static final String byToDropDown = "..//div[@data-name='%s']";
    private static final By byPopularDestinationsDropdownXpath = By.xpath(
            "..//div[text()=\"Popular Destinations\"]");
    private static final By bySelectedNumberOfPassengersXpath = By.xpath(
            "//input[@name='passenger']");
    private static final By byJourneyTypeXpath = By.xpath(
            "//div[@class='filter-option-inner-inner']");
    private static final By byNumberOfPassengersSelectionDoneXpath = By.xpath(
            "//button[text()='Done']");
    private static final By byCloseExtraSeatTooltipXpath = By.xpath(
            "//i[@class='icon-close close-extraseat-tooltip']");
    private static final By byCloseAcceptCookiesXpath = By.xpath("//a[@class='close-cookie']");
    private static final By bySearchFlightOptionsXpath = By.xpath("//span[text()='Search Flight']");
    ;
    private static final By byDecreaseAdultPassengerCountXpath = By.xpath(
            "//button[@title='Decrease Adult Passenger Count']");
    ;
    private static final By byIncreaseAdultPassengerCountXpath = By.xpath(
            "//button[@title='Increase Adult Passenger Count']");
    private static final By byGetSelectedAdultPassengerCountXpath = By.xpath(
            "//input[@class='counter adult-pax']");
    private static final By bySelectBookingXpath = By.xpath("//a[@title='Book']");
    private static final By bySelectGiftVouchersXpath = By.xpath(
            "//div[@class='menu-wrapper-child']//div[text()='Gift Voucher']");
    private static final By bySelectJourneyTypeXpath = By.xpath(
            "//a[contains(@class,'one-way-tab')]");
    private final Driver driver;
    private final Visual visually;
    private final WebDriver innerDriver;
    private final TestExecutionContext context;

    public IndigoHomeScreenWeb(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
        this.innerDriver = this.driver.getInnerDriver();
        long threadId = Thread.currentThread().getId();
        context = Runner.getTestExecutionContext(threadId);
        waitFor(2);
        if (driver.isElementPresent(byCloseAcceptCookiesXpath)) {
            driver.findElement(byCloseAcceptCookiesXpath).click();
        }
    }

    @Override
    public IndigoHomeScreen selectFrom(String from) {
        WebElement fromElement = driver.findElement(byFromXpath);
        fromElement.click();
        fromElement.sendKeys(from);
        fromElement.findElement(By.xpath(String.format(byFromDropdown, from))).click();
        visually.checkWindow(SCREEN_NAME, "selected from");
        return this;
    }

    @Override
    public IndigoHomeScreen selectTo(String destination) {
        WebElement toElement = driver.findElement(byToXpath);
        toElement.click();
        toElement.sendKeys(destination);
        toElement.findElement(byPopularDestinationsDropdownXpath);
        String dest = String.format(byToDropDown, destination);
        LOGGER.info("dest: " + dest);
        toElement.findElement(By.xpath(dest)).click();
        visually.checkWindow(SCREEN_NAME, "selected destination");
        return this;
    }

    @Override
    public IndigoHomeScreen selectNumberOfAdultPassengers(int numberOfAdultsToSelect) {
        driver.waitForClickabilityOf(bySelectedNumberOfPassengersXpath).click();
        visually.checkWindow(SCREEN_NAME, "Pre-selected number of passengers");
        if (driver.isElementPresent(byCloseExtraSeatTooltipXpath)) {
            driver.findElement(byCloseExtraSeatTooltipXpath).click();
        }
        int numberOfAdultsSelected = getNumberOfAdultsSelected();
        LOGGER.info("numberOfAdultsSelected: " + numberOfAdultsSelected);
        if (numberOfAdultsToSelect == numberOfAdultsSelected) {
            LOGGER.info(
                    "Number of adults selected is already as expected: " + numberOfAdultsToSelect);
        } else if (numberOfAdultsToSelect < numberOfAdultsSelected) {
            decrementAdultPassengerSelection(numberOfAdultsToSelect, numberOfAdultsSelected);
        } else {
            incrementAdultPassengerSelection(numberOfAdultsToSelect, numberOfAdultsSelected);
        }
        driver.findElement(byNumberOfPassengersSelectionDoneXpath).click();
        return this;
    }

    private int getNumberOfAdultsSelected() {
        return Integer.parseInt(
                driver.findElement(byGetSelectedAdultPassengerCountXpath).getAttribute("value"));
    }

    private IndigoHomeScreenWeb decrementAdultPassengerSelection(int numberOfAdultsToSelect,
            int numberOfAdultsSelected) {
        while (numberOfAdultsToSelect < numberOfAdultsSelected && numberOfAdultsSelected != 1) {
            LOGGER.info("Decreasing adult passenger selected count");
            driver.findElement(byDecreaseAdultPassengerCountXpath).click();
            numberOfAdultsSelected--;
        }
        return this;
    }

    private IndigoHomeScreenWeb incrementAdultPassengerSelection(int numberOfAdultsToSelect,
            int numberOfAdultsSelected) {
        while (numberOfAdultsToSelect > numberOfAdultsSelected) {
            LOGGER.info("Increasing adult passenger selected count");
            driver.findElement(byIncreaseAdultPassengerCountXpath).click();
            numberOfAdultsSelected++;
        }
        return this;
    }

    @Override
    public IndigoHomeScreen selectJourneyType(String journeyType) {
        driver.findElement(byJourneyTypeXpath).click();
        visually.checkWindow(SCREEN_NAME, "Journey type options displayed");
        driver.findElement(bySelectJourneyTypeXpath).click();
        return this;
    }

    @Override
    public IndigoFlightSearchResultsScreen searchFlightOptions() {
        driver.findElement(bySearchFlightOptionsXpath).click();
        return IndigoFlightSearchResultsScreen.get();
    }

    @Override
    public IndigoGiftVouchersScreen selectGiftVouchers() {
        WebElement bookElement = driver.findElement(bySelectBookingXpath);
        Actions action = new Actions(driver.getInnerDriver());
        action.moveToElement(bookElement);
        WebElement giftVouchersElement = driver.findElement(bySelectGiftVouchersXpath);
        action.moveToElement(giftVouchersElement);
        action.click().build().perform();
        visually.checkWindow(SCREEN_NAME, "Clicked on Gift Voucher");
        return IndigoGiftVouchersScreen.get();
    }
}
