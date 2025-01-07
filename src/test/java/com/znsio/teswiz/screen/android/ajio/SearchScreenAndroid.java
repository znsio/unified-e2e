package com.znsio.teswiz.screen.android.ajio;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.ajio.ProductScreen;
import com.znsio.teswiz.screen.ajio.SearchScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class SearchScreenAndroid
        extends SearchScreen {
    private static final String SCREEN_NAME = SearchScreenAndroid.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final By byResultsId = By.id("com.ril.ajio:id/tv_count_plp_header_is");
    private static final By byProductId = By.id("com.ril.ajio:id/plp_row_product_iv");
    private static final By byProductListTitleId = By.id("com.ril.ajio:id/toolbar_title_tv");
    private static final By byProductLayoutId = By.id("com.ril.ajio:id/layout_category_container/value_iv");
    private final Driver driver;
    private final Visual visually;

    public SearchScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public int numberOfProductFound() {
        int numberOfProductFound = Integer.parseInt(
                driver.waitTillElementIsPresent(byResultsId).getText().split(" ")[0]);
        visually.checkWindow(SCREEN_NAME, "Result for Image search");
        LOGGER.info("numberOfProductFound: " + numberOfProductFound);
        return numberOfProductFound;
    }

    @Override
    public ProductScreen selectProduct() {
        LOGGER.info("Selection of Product in the result page");
        if (!(driver.isElementPresent(byProductLayoutId))) {
            driver.tapOnMiddleOfScreen();
        }
        List<WebElement> list = driver.waitTillPresenceOfAllElements(byProductId);
        list.get(0).click();
        return ProductScreen.get();
    }

    @Override
    public boolean isProductListLoaded(String product) {
        LOGGER.info(String.format("Verifying if %s list is loaded", product));
        if (!(driver.isElementPresent(byProductListTitleId))) {
            driver.tapOnMiddleOfScreen();
        }
        String productLoaded = driver.waitTillElementIsVisible(byProductListTitleId).getText().trim();
        LOGGER.info("Loaded product: " + productLoaded);
        return productLoaded.contains(product);
    }

    @Override
    public String getProductListingPageHeader() {
        return null;
    }

    @Override
    public ProductScreen selectFirstItemFromList() {
        return null;
    }
}
