package com.znsio.teswiz.screen.ios.ajio;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.ajio.CartScreen;
import com.znsio.teswiz.screen.ajio.ProductScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.znsio.teswiz.tools.Wait.waitFor;

public class ProductScreenIOS
        extends ProductScreen {
    private static final String SCREEN_NAME = ProductScreenIOS.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final By byProductNameId = By.id("V2Pdp_brand_ProductName");
    private static final By byAddToCartButtonId = By.id("Add To Bag");
    private static final By byViewBagButtonId = By.id("V2PDP_BottomFloatingView_ATC");
    private static final By bySelectSizeFirstXpath = By.xpath("//XCUIElementTypeOther[@name='pdp_Size_Scroll_View']/XCUIElementTypeCollectionView/XCUIElementTypeCell");
    private static final By byAddToBagXpath = By.xpath("//XCUIElementTypeOther[@name='V2PDP_bottomView_buttonView' or @name='V2PDP_bottomView_AddToButtonView']");
    private static final By byProductBrandNameId = By.id("V2Pdp_brand_brandLabel");
    private static final By byAddToCartId = By.id("V2PDP_BottomFloatingView_ATC");
    private static final By byAllSizesXpath = By.xpath("//XCUIElementTypeCollectionView/descendant::XCUIElementTypeStaticText[@name='V2PDP_sizeView_titleLabel']");
    private static final By byAddToBagToastMessageId = By.id("common_toast_displayer");
    private final Driver driver;
    private final Visual visually;

    public ProductScreenIOS(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public CartScreen addProductToCart() {
        LOGGER.info("addProductToCart");
        driver.waitTillElementIsPresent(byAddToCartButtonId).click();
        visually.checkWindow(SCREEN_NAME, "Add to cart");
        driver.waitTillElementIsPresent(bySelectSizeFirstXpath).click();
        driver.waitTillElementIsPresent(byAddToBagXpath).click();
        visually.checkWindow(SCREEN_NAME, "Product Added");
        driver.waitTillElementIsPresent(byViewBagButtonId).click();
        visually.checkWindow(SCREEN_NAME, "Updated Cart with Product");
        return CartScreen.get();
    }

    @Override
    public String getProductName() {
        LOGGER.info("getProductName");
        waitFor(5);
        WebElement product = driver.waitTillElementIsPresent(byProductNameId);
        String productName = product.getText();
        visually.checkWindow(SCREEN_NAME, "Product Details");
        LOGGER.info("Product Name: " + productName);
        return productName;
    }

    @Override
    public boolean isProductDetailsLoaded() {
        return false;
    }

    @Override
    public ProductScreen flickImage() {
        return null;
    }

    @Override
    public String isElementIdChanged() {
        return null;
    }

    @Override
    public boolean isProductBrandNameVisible() {
        waitFor(10);
        LOGGER.info("Product Description page is opened");
        visually.checkWindow(SCREEN_NAME, "Product Description page");
        return driver.waitTillElementIsVisible(byProductBrandNameId).isDisplayed();
    }

    @Override
    public ProductScreen clickOnAddToCart() {
        driver.waitTillElementIsPresent(byAddToCartId).click();
        LOGGER.info("Add to Cart button is clicked");
        visually.checkWindow(SCREEN_NAME, "Add to Cart option");
        return this;
    }

    @Override
    public ProductScreen selectAvailableSize() {
        LOGGER.info("Get all the available sizes for the item and click on first available size");
        List<WebElement> allSizes = driver.findElements(byAllSizesXpath);
        allSizes.get(0).click();
        visually.checkWindow(SCREEN_NAME, "Size option selected");
        return this;
    }

    @Override
    public ProductScreen clickOnAddToBagButton() {
        LOGGER.info("Click on Add to Bag after selecting size");
        driver.waitTillElementIsPresent(byAddToBagXpath).click();
        return this;
    }

    @Override
    public String getAddedToBagToastMessage() {
        LOGGER.info("A toast message is displayed after adding item to cart");
        visually.checkWindow(SCREEN_NAME, "Add To Cart toast message");
        return driver.waitTillElementIsPresent(byAddToBagToastMessageId).getText().trim();
    }

    @Override
    public CartScreen clickOnCartIcon() {
        driver.waitTillElementIsPresent(byViewBagButtonId).click();
        LOGGER.info("Cart page is opened");
        visually.checkWindow(SCREEN_NAME, "Cart Page");
        return CartScreen.get();
    }
}
