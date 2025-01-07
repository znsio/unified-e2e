package com.znsio.teswiz.screen.ios.ajio;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.ajio.HomeScreen;
import com.znsio.teswiz.screen.ajio.ProductScreen;
import com.znsio.teswiz.screen.ajio.SearchScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Map;

import static com.znsio.teswiz.tools.Wait.waitFor;

public class HomeScreenIOS
        extends HomeScreen {
    private static final String SCREEN_NAME = HomeScreenIOS.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final By byStartSearchBoxId = By.id("Home_Search_Label");
    private static final By byUploadPhotoButtonId = By.id("Upload a Photo");
    private static final By byImageId = By.xpath("//XCUIElementTypeImage[contains(@name,'Photo, October 10')]");
    private static final By bySearchBoxId = By.id("Home_Search_Label");
    private static final By bySearchFieldId = By.id("search_searchController_searchField");
    private static final By byAllowNotificationsId = By.id("Allow");
    private static final By byAllowLocationId = By.xpath("//XCUIElementTypeButton[@name='Home_local_localBtn']");
    private static final By byAllowLocationWhileUsingAppId = By.id("Allow While Using App");
    private final Driver driver;
    private final Visual visually;

    public HomeScreenIOS(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public SearchScreen searchByImage() {
        WebElement uploadImageButton = driver.waitTillElementIsPresent(byImageId);
        visually.checkWindow(SCREEN_NAME, "searchByImage");
        uploadImageButton.click();
        LOGGER.info("Clicked on Image");
        return SearchScreen.get();
    }

    @Override
    public HomeScreen attachFileToDevice(Map imageData) {
        driver.waitTillElementIsPresent(byStartSearchBoxId).click();
        LOGGER.info("Clicked on HomePage Search Box");
        visually.checkWindow(SCREEN_NAME, "Get Search Screen");
        driver.waitTillElementIsPresent(byUploadPhotoButtonId).click();
        LOGGER.info("Clicked on Upload Photo Button");
        return this;
    }

    @Override
    public HomeScreen goToMenu() {
        return this;
    }

    @Override
    public SearchScreen selectProductFromCategory(String product, String category, String gender) {
        return SearchScreen.get();
    }

    @Override
    public ProductScreen searchForTheProduct(String productName) {
        driver.waitTillElementIsPresent(bySearchBoxId, 10).click();
        LOGGER.info("Clicked on HomePage Search Box");
        driver.waitTillElementIsPresent(bySearchFieldId, 10).sendKeys(productName + "\n");
        LOGGER.info("Search for the product");
        visually.checkWindow(SCREEN_NAME, "Search Screen");
        return ProductScreen.get();
    }

    @Override
    public HomeScreen clickOnAllowToSendNotifications() {
        if (driver.findElements(byAllowNotificationsId).size() > 0) {
            driver.waitTillElementIsPresent(byAllowNotificationsId, 15).click();
        }
        return HomeScreen.get();
    }

    @Override
    public HomeScreen clickOnAllowLocation() {
        if (driver.findElements(byAllowLocationId).size() > 0) {
            driver.waitTillElementIsPresent(byAllowLocationId, 15).click();
        }
        return HomeScreen.get();
    }

    @Override
    public HomeScreen clickOnAllowLocationWhileUsingApp() {
        waitFor(5);
        if (driver.findElements(byAllowLocationWhileUsingAppId).size() > 0) {
            driver.waitTillElementIsPresent(byAllowLocationWhileUsingAppId, 15).click();
        }
        return HomeScreen.get();
    }

    @Override
    public HomeScreen relaunchApplication() {
        driver.relaunchApp();
        return HomeScreen.get();
    }

}
