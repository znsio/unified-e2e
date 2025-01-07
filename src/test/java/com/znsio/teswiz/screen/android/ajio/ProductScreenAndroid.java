package com.znsio.teswiz.screen.android.ajio;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.ajio.CartScreen;
import com.znsio.teswiz.screen.ajio.ProductScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ProductScreenAndroid
        extends ProductScreen {
    private static final String SCREEN_NAME = ProductScreenAndroid.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final By byProductNameId = By.id("com.ril.ajio:id/product_name");
    private static final By byAddToCartButtonId = By.id("com.ril.ajio:id/add_to_cart_tv");
    private static final By byViewBagButtonXpath = By.xpath(
            "//android.widget.TextView[@text='View Bag']");
    private static final By byBrandNameId = By.id("com.ril.ajio:id/product_name");
    private static final By byProductImageId = By.id("com.ril.ajio:id/pdp_product_img");
    private final TestExecutionContext context;

    private final Driver driver;
    private final Visual visually;

    public ProductScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
        long threadId = Thread.currentThread().getId();
        context = Runner.getTestExecutionContext(threadId);
    }

    @Override
    public CartScreen addProductToCart() {
        LOGGER.info("addProductToCart");
        driver.waitTillElementIsPresent(byAddToCartButtonId).click();
        visually.checkWindow(SCREEN_NAME, "Add to cart");
        driver.waitTillElementIsPresent(byViewBagButtonXpath).click();
        return CartScreen.get();
    }

    @Override
    public String getProductName() {
        LOGGER.info("getProductName");
        driver.waitTillElementIsPresent(byBrandNameId).click();
        WebElement product = driver.waitTillElementIsPresent(byProductNameId);
        product.click();
        String productName = product.getText();
        visually.checkWindow(SCREEN_NAME, "Product Details");
        LOGGER.info("Product Name: " + productName);
        return productName;
    }

    @Override
    public boolean isProductDetailsLoaded() {
        LOGGER.info("Verifying if Product Details page is loaded");
        boolean isProductedLoaded = false;
        driver.tapOnMiddleOfScreen();
        driver.waitTillElementIsPresent(byProductImageId);
        driver.findElement(byProductImageId).click();
        if (driver.isElementPresent(byProductImageId)) {
            isProductedLoaded = true;
        }
        return isProductedLoaded;
    }

    @Override
    public ProductScreen flickImage() {
        LOGGER.info("Performing flick to view multiple product images");
        driver.findElement(byProductImageId).click();
        String initialElementId = driver.findElement(byProductImageId).getAttribute("bounds");
        context.addTestState(SAMPLE_TEST_CONTEXT.INITIAL_ELEMENT_ID, initialElementId);
        driver.flick();
        return this;
    }

    @Override
    public String isElementIdChanged() {
        LOGGER.info("Verifying other images after flick");
        visually.checkWindow(SCREEN_NAME, "Other visible images after flick");
        return driver.findElement(byProductImageId).getAttribute("bounds");
    }

    @Override
    public boolean isProductBrandNameVisible() {
        return false;
    }

    @Override
    public ProductScreen clickOnAddToCart() {
        return null;
    }

    @Override
    public ProductScreen selectAvailableSize() {
        return null;
    }

    @Override
    public ProductScreen clickOnAddToBagButton() {
        return null;
    }

    @Override
    public String getAddedToBagToastMessage() {
        return null;
    }

    @Override
    public CartScreen clickOnCartIcon() {
        return null;
    }
}
