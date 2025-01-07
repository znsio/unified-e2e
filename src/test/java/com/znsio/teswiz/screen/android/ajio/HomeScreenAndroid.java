package com.znsio.teswiz.screen.android.ajio;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.ajio.HomeScreen;
import com.znsio.teswiz.screen.ajio.ProductScreen;
import com.znsio.teswiz.screen.ajio.SearchScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

import java.util.Map;

public class HomeScreenAndroid
        extends HomeScreen {
    private static final String SCREEN_NAME = HomeScreenAndroid.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final By byStartSearchBoxId = By.id("com.ril.ajio:id/llpsTvSearch");
    private static final By byUploadPhotoButtonId = By.id("com.ril.ajio:id/layout_select_photo");
    private static final By byImageDirectoryXpath = By.xpath(
            "//android.widget.TextView[contains(@text, 'Images')]");
    private static final By byImageXpath = By.xpath(
            "//android.view.ViewGroup[contains(@content-desc,'Photo taken')][1]");
    private static final By byDismissButtonId = By.id("com.ril.ajio:id/footer_button_2");
    private static final By bySystemPermissionMessageId = By.id(
            "com.android.permissioncontroller:id/permission_message");
    private static final By byAllowButtonId = By.id(
            "com.android.permissioncontroller:id/permission_allow_button");
    private static final By bySideMenuId = By.id("com.ril.ajio:id/fahIvMenu");
    private static final String byFilterProductXpath = "//android.widget.TextView[@text='%s']";
    private static final By byAllowNotificationsId = By.id("com.android.permissioncontroller:id/permission_allow_always_button");
    private static final By byAllowLocationId = By.xpath("//*[@text='Allow Location']");
    private static final By byAllowLocationWhileUsingAppId = By.id("//*[@text='Allow While Using App]");
    private final Driver driver;
    private final Visual visually;

    public HomeScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public SearchScreen searchByImage() {
        driver.waitTillElementIsPresent(byImageDirectoryXpath).click();
        driver.waitTillElementIsPresent(byImageXpath).click();
        return SearchScreen.get();
    }

    @Override
    public HomeScreen attachFileToDevice(Map imageData) {
        String sourceFileLocation = System.getProperty("user.dir") + imageData.get(
                "IMAGE_FILE_LOCATION");
        String destinationFileLocation = (String) imageData.get("UPLOAD_IMAGE_LOCATION");
        LOGGER.info("searchByImage");

        if (driver.isElementPresent(byDismissButtonId)) {
            driver.findElement(byDismissButtonId).click();
        }

        driver.waitTillElementIsPresent(byStartSearchBoxId).click();
        visually.checkWindow(SCREEN_NAME, "Upload a Photo");
        driver.waitTillElementIsPresent(byUploadPhotoButtonId).click();
        if (driver.isElementPresent(bySystemPermissionMessageId)) {
            driver.waitTillElementIsPresent(byAllowButtonId).click();
        }

        driver.pushFileToDevice(sourceFileLocation, destinationFileLocation);
        LOGGER.info("Image Pushed to Device path" + destinationFileLocation);
        return this;
    }


    @Override
    public HomeScreen goToMenu() {
        LOGGER.info("Opening Side Drawer Menu");
        driver.waitTillElementIsVisible(bySideMenuId).click();
        return this;
    }

    @Override
    public SearchScreen selectProductFromCategory(String product, String category, String gender) {
        LOGGER.info(String.format("Selecting %s for %s", product, gender));
        driver.waitTillElementIsVisible(By.xpath(String.format(byFilterProductXpath, gender))).click();
        driver.scrollVertically(20, 60, 50);
        driver.waitTillElementIsVisible(By.xpath(String.format(byFilterProductXpath, category))).click();
        driver.waitTillElementIsVisible(By.xpath(String.format(byFilterProductXpath, product))).click();
        return SearchScreen.get();
    }

    @Override
    public HomeScreen clickOnAllowToSendNotifications() {
        if (driver.findElements(byAllowNotificationsId).size() > 0) {
            driver.waitTillElementIsPresent(byAllowNotificationsId).click();
        }
        return HomeScreen.get();
    }

    @Override
    public HomeScreen clickOnAllowLocation() {
        if (driver.findElements(byAllowLocationId).size() > 0) {
            driver.waitTillElementIsPresent(byAllowLocationId).click();
        }
        return HomeScreen.get();
    }

    @Override
    public HomeScreen clickOnAllowLocationWhileUsingApp() {
        if (driver.findElements(byAllowLocationWhileUsingAppId).size() > 0) {
            driver.waitTillElementIsPresent(byAllowLocationWhileUsingAppId).click();
        }
        return HomeScreen.get();
    }

    @Override
    public HomeScreen relaunchApplication() {
        driver.relaunchApp();
        return HomeScreen.get();
    }

    @Override
    public ProductScreen searchForTheProduct(String productName) {
        return null;
    }

}
