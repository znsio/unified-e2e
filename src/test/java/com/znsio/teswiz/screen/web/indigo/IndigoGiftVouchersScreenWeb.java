package com.znsio.teswiz.screen.web.indigo;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.indigo.IndigoGiftVouchersScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static com.znsio.teswiz.tools.Wait.waitFor;

public class IndigoGiftVouchersScreenWeb
        extends IndigoGiftVouchersScreen {
    private static final String SCREEN_NAME = IndigoGiftVouchersScreenWeb.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final String NOT_YET_IMPLEMENTED = " not yet implemented";
    private static final By bySelectedVoucherValueDropdownId = By.id("SelectedVoucherValue");
    private static final By bySelectedVoucherQuantityDropdownId = By.id("SelectedVoucherQuantity");
    private static final By byTotalAmountId = By.id("lblTotal");
    private static final By byForNameId = By.id("Per_Fname");
    private static final By byCustomMessageId = By.id("Message");
    private static final By byPreviewButtonXpath = By.xpath("//input[@class='preview-btn']");
    private static final By byPreviewVoucherHeadingXpath = By.xpath(
            "//div[@class='heading']/h2[contains(text(),'Preview Your Voucher')]");
    private final Driver driver;
    private final Visual visually;
    private final WebDriver innerDriver;
    private final TestExecutionContext context;

    public IndigoGiftVouchersScreenWeb(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
        this.innerDriver = this.driver.getInnerDriver();
        long threadId = Thread.currentThread().getId();
        context = Runner.getTestExecutionContext(threadId);
        waitFor(2);
    }

    @Override
    public IndigoGiftVouchersScreen select(String numberOfGiftVouchersToPurchase,
            String denomination) {
        Select selectDenomination = new Select(
                driver.findElement(bySelectedVoucherValueDropdownId));
        selectDenomination.selectByValue(denomination);
        Select selectQuantity = new Select(driver.findElement(bySelectedVoucherQuantityDropdownId));
        selectQuantity.selectByValue(numberOfGiftVouchersToPurchase);
        visually.checkWindow(SCREEN_NAME, "Selected denomination and quality");
        return this;
    }

    @Override
    public int getTotalPrice() {
        String total = driver.findElement(byTotalAmountId).getText();
        int totalAmount = Integer.parseInt(total.split(" ")[1]);
        return totalAmount;
    }

    @Override
    public IndigoGiftVouchersScreen select(String numberOfGiftVouchersToPurchase,
            String denomination, String forWhom,
            String customMessage) {
        select(numberOfGiftVouchersToPurchase, denomination);
        driver.findElement(By.id("chkPersonal")).click();
        WebElement forWhomElement = driver.findElement(byForNameId);
        forWhomElement.clear();
        forWhomElement.sendKeys(forWhom);

        WebElement customMessageElement = driver.findElement(byCustomMessageId);
        customMessageElement.clear();
        customMessageElement.sendKeys(customMessage);
        visually.checkWindow(SCREEN_NAME, "Personalised Gift Voucher");
        return this;
    }

    @Override
    public IndigoGiftVouchersScreen preview() {
        driver.waitForClickabilityOf(byPreviewButtonXpath).click();
        driver.waitTillElementIsVisible(byPreviewVoucherHeadingXpath);
        visually.checkWindow(SCREEN_NAME, "Preview Gift Voucher");
        return this;
    }
}
