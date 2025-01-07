package com.znsio.teswiz.screen.android.vodqa;

import com.applitools.eyes.appium.Target;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.vodqa.DragAndDropScreen;
import com.znsio.teswiz.screen.vodqa.NativeViewScreen;
import com.znsio.teswiz.screen.vodqa.VodqaScreen;
import com.znsio.teswiz.screen.vodqa.WebViewScreen;
import com.znsio.teswiz.tools.cmd.CommandLineExecutor;
import io.appium.java_client.AppiumBy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

public class VodqaScreenAndroid extends VodqaScreen {
    private static final Logger LOGGER = LogManager.getLogger(VodqaScreenAndroid.class.getName());
    private final Driver driver;
    private final Visual visually;
    private final String SCREEN_NAME = VodqaScreenAndroid.class.getSimpleName();
    private final By byLoginButton = AppiumBy.xpath("//android.view.ViewGroup[@content-desc='login']/android.widget.Button");
    private final By byVerticalSwipeViewGroup = AppiumBy.xpath("//android.view.ViewGroup[@content-desc='verticalSwipe']");
    private final By byCLanguageTextView = AppiumBy.xpath("//android.widget.TextView[@text=' C']");
    private final By byRubyLanguageTextView = AppiumBy.xpath("//android.widget.TextView[@text=' Ruby']");
    private final String screenSelectionXpath = "//android.view.ViewGroup[@content-desc='%s']";
    private final String swipeViewXpath = "//android.widget.TextView[@text='%s']";
    private final String swipeViewTileXpath = "//android.view.ViewGroup[@content-desc='view%s']/android.view.ViewGroup";
    private final By byNativeViewXpath = AppiumBy.xpath("//android.widget.TextView[@content-desc=\"chainedView\"]");
    private final String byPageHeaderXpath = "//android.widget.TextView[@text='%s']";
    private final By byWebViewSectionOptionXpath = AppiumBy.xpath("//android.view.ViewGroup[@content-desc='webView']");
    private final By byNativeViewSectionXpath = AppiumBy.xpath("//android.view.ViewGroup[@content-desc='chainedView']");
    private final String languageTextView = "//android.widget.TextView[@text=' %s']";

    private final By byLongPressOptionXpath = AppiumBy.xpath("//android.widget.TextView[@content-desc='longPress']");
    private final By byLongPressButtonAccessibilityId = AppiumBy.accessibilityId("longpress");
    private final By byLongPressedPopupId = AppiumBy.id("android:id/alertTitle");
    private final By byDragAndDropTextView = AppiumBy.xpath("//android.widget.TextView[@content-desc='dragAndDrop']");
    private final By byDoubleTapElementXpath = AppiumBy.xpath("//android.view.ViewGroup[@content-desc='doubleTapMe']");
    private final By byDoubleTapScreenXpath = AppiumBy.xpath("//android.widget.TextView[@text='Double Tap']");
    private final By byDoubleTapSuccessfulXpath = AppiumBy.xpath("//android.widget.TextView[@text='Double tap successful!']");
    private final By byPhotoViewElementXpath = AppiumBy.xpath("//android.view.ViewGroup[@content-desc='photoView']");
    private final By byImageElementXpath = AppiumBy.xpath("//android.view.ViewGroup[@content-desc='photo']/android.widget.ImageView");

    private final By bySliderSectionXpath = AppiumBy.xpath("//android.view.ViewGroup[@content-desc='slider1']/android.view.ViewGroup");
    private final By byFirstSliderElementId = AppiumBy.accessibilityId("slider");
    private final By bySecondSliderElementId = AppiumBy.accessibilityId("slider1");
    private final By bySliderValueXpath = AppiumBy.xpath("//android.view.ViewGroup/android.widget.TextView[2]");


    public VodqaScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public VodqaScreen login() {
        driver.waitTillElementIsPresent(byLoginButton);
        visually.checkWindow(SCREEN_NAME, "Login Screen");
        driver.findElement(byLoginButton).click();
        return this;
    }

    @Override
    public VodqaScreen scrollFromOneElementPointToAnother() {
        driver.waitTillElementIsPresent(byVerticalSwipeViewGroup);
        visually.checkWindow(SCREEN_NAME, "Home Screen");
        driver.findElement(byVerticalSwipeViewGroup).click();
        driver.waitTillElementIsPresent(byCLanguageTextView);
        visually.checkWindow(SCREEN_NAME, "Vertical Swiping Screen Before Scroll");
        Point fromPoint = driver.findElement(byRubyLanguageTextView).getLocation();
        Point toPoint = driver.findElement(byCLanguageTextView).getLocation();
        driver.scroll(fromPoint, toPoint);
        visually.checkWindow(SCREEN_NAME, "Vertical Swiping Screen After Scroll");
        return this;
    }

    @Override
    public VodqaScreen tapInTheMiddle() {
        driver.waitTillElementIsVisible(byNativeViewXpath);
        visually.checkWindow(SCREEN_NAME, "Sample List page");
        driver.tapOnMiddleOfScreen();
        return this;
    }

    @Override
    public boolean isPreviousPageHeadingVisible(String pageHeading) {
        visually.checkWindow(SCREEN_NAME, "Page landed after tapping in the middle");
        return driver.isElementPresent(AppiumBy.xpath(String.format(byPageHeaderXpath, pageHeading)));
    }

    @Override
    public VodqaScreen openVerticalSwipingScreen() {
        driver.waitTillElementIsPresent(byVerticalSwipeViewGroup);
        visually.checkWindow(SCREEN_NAME, "Home Screen");
        driver.findElement(byVerticalSwipeViewGroup).click();
        LOGGER.info("vertical swiping screen is open");
        return this;
    }

    @Override
    public VodqaScreen scrollDownByScreenSize() {
        driver.waitTillElementIsPresent(byCLanguageTextView);
        driver.scrollDownByScreenSize();
        visually.checkWindow(SCREEN_NAME, "Screen scrolled down");
        return this;
    }

    @Override
    public VodqaScreen selectScreen(String screenName) {
        By byScreenNameXpath = AppiumBy.xpath(String.format(screenSelectionXpath, screenName));
        driver.waitTillElementIsPresent(byScreenNameXpath);
        driver.findElement(byScreenNameXpath).click();
        return this;
    }

    @Override
    public boolean isSwipeSuccessful(String elementText) {
        boolean isSwipeSuccessful = driver.findElement(AppiumBy.xpath(String.format(swipeViewXpath, elementText))).isDisplayed();
        visually.check(SCREEN_NAME, "Carousel Tile after swipe", Target.region(AppiumBy.xpath(String.format(swipeViewTileXpath, elementText))));
        return isSwipeSuccessful;
    }

    @Override
    public VodqaScreen swipeByPassingPercentageAttributes(int atPercentScreenHeight, int fromPercentageWidth, int toPercentScreenWidth) {
        driver.waitTillElementIsPresent(AppiumBy.xpath(String.format(swipeViewXpath, "1")));
        visually.check(SCREEN_NAME, "Carousel Tile before swipe by percentage Attributes",
                       Target.region(AppiumBy.xpath(String.format(swipeViewTileXpath, "1"))));
        driver.swipeByPassingPercentageAttributes(atPercentScreenHeight, fromPercentageWidth, toPercentScreenWidth);
        return this;
    }

    @Override
    public WebViewScreen enterIntoNewsWebViewSection() {
        LOGGER.info("Enter into news web view section");
        visually.checkWindow(SCREEN_NAME, "Sample List Screen");
        driver.waitTillElementIsVisible(byWebViewSectionOptionXpath).click();
        return WebViewScreen.get();
    }

    @Override
    public NativeViewScreen enterIntoNativeViewSection() {
        LOGGER.info("Enter into native view section");
        visually.checkWindow(SCREEN_NAME, "Sample List Screen");
        driver.waitTillElementIsVisible(byNativeViewSectionXpath).click();
        return NativeViewScreen.get();
    }

    @Override
    public VodqaScreen putAppInTheBackground(int time) {
        driver.putAppInBackgroundFor(time);
        visually.checkWindow(SCREEN_NAME, "App screen should visible after putting app in background");
        return this;
    }

    @Override
    public boolean isAppWorkingInBackground() {
        LOGGER.info("Validating current app package to know app work in background");
        String adbCommand = "adb shell dumpsys window | grep -E 'mCurrentFocus'";
        LOGGER.info(adbCommand);
        String currentOpenApp = CommandLineExecutor.execCommand(new String[]{adbCommand}).toString();
        String currentAppPackageName = Runner.getAppPackageName();
        return currentOpenApp.contains(currentAppPackageName);
    }

    @Override
    public boolean isElementWithTextVisible(String elementText) {
        By byLanguageTextView = AppiumBy.xpath(String.format(this.languageTextView, elementText));
        visually.check(SCREEN_NAME, String.format("%s language element text view", elementText), Target.region(byLanguageTextView));
        return driver.isElementPresent(byLanguageTextView);
    }

    @Override
    public VodqaScreen scrollVerticallyByPercentage(int fromPercentHeight, int toPercentHeight, int percentWidth) {
        driver.waitTillElementIsPresent(byCLanguageTextView);
        driver.scrollVertically(fromPercentHeight, toPercentHeight, percentWidth);
        visually.checkWindow(SCREEN_NAME, "Screen scrolled down");
        return this;
    }

    @Override
    public VodqaScreen longPressOnElement() {
        driver.waitForClickabilityOf(byLongPressOptionXpath).click();
        LOGGER.info("Performing long press on element");
        visually.checkWindow(SCREEN_NAME, "Long press screen");
        driver.longPress(byLongPressButtonAccessibilityId, 3);
        return this;
    }

    @Override
    public boolean isLongPressedPopupVisible() {
        visually.checkWindow(SCREEN_NAME, "Long pressed popup");
        return driver.isElementPresent(byLongPressedPopupId);
    }

    @Override
    public DragAndDropScreen openDragAndDropScreen() {
        driver.waitTillElementIsPresent(byDragAndDropTextView);
        visually.checkWindow(SCREEN_NAME, "Home Screen");
        driver.findElement(byDragAndDropTextView).click();
        return DragAndDropScreen.get();
    }

    @Override
    public VodqaScreen doubleTapOnElement() {
        LOGGER.info("Performing Double tap on element in Double tap screen");
        driver.waitTillElementIsVisible(byDoubleTapScreenXpath).click();
        visually.check(SCREEN_NAME, "Double Tap Element Screen", Target.window());
        driver.doubleTap(driver.waitTillElementIsVisible(byDoubleTapElementXpath));
        return this;
    }

    @Override
    public boolean isDoubleTapSuccessful() {
        LOGGER.info("Checking if double tap on element is successful");
        driver.waitTillElementIsVisible(byDoubleTapSuccessfulXpath);
        visually.check(SCREEN_NAME, "Double Tap Successful Message", Target.region(byDoubleTapSuccessfulXpath));
        return driver.isElementPresent(byDoubleTapSuccessfulXpath);
    }

    @Override
    public VodqaScreen navigateToUImageView() {
        LOGGER.info("Navigate to Photo View Section");
        driver.waitTillElementIsVisible(byPhotoViewElementXpath).click();
        return this;
    }

    @Override
    public VodqaScreen pinchAndZoomInOnAnElement() {
        LOGGER.info("Perform Pinch action and Zoom In Image element");
        driver.pinchAndZoomIn(driver.waitTillElementIsVisible(byImageElementXpath));
        visually.check(SCREEN_NAME, "Zoomed In Image element", Target.window());
        return this;
    }

    @Override
    public VodqaScreen pinchAndZoomOutOnAnElement() {
        LOGGER.info("Perform Pinch action and Zoom out Image element");
        driver.pinchAndZoomOut(driver.waitTillElementIsVisible(byImageElementXpath));
        visually.check(SCREEN_NAME, "Zoomed Out Image element", Target.window());
        return this;
    }

    @Override
    public boolean isPinchAndZoomInSuccessful(Dimension initialElementDimension) {
        Dimension actualElementDimension = driver.waitTillElementIsVisible(byImageElementXpath).getSize();
        return (initialElementDimension.width * initialElementDimension.height) > (actualElementDimension.width * actualElementDimension.height);
    }

    @Override
    public boolean isPinchAndZoomOutSuccessful(Dimension initialElementDimension) {
        Dimension actualElementDimension = driver.waitTillElementIsVisible(byImageElementXpath).getSize();
        return (initialElementDimension.width * initialElementDimension.height) < (actualElementDimension.width * actualElementDimension.height);
    }

    @Override
    public Dimension getImageElementDimension() {
        return driver.waitTillElementIsVisible(byImageElementXpath).getSize();
    }

    @Override
    public VodqaScreen multiTouchOnElements() {
        LOGGER.info("Performing multi touch action in Slider Screen");
        driver.waitTillElementIsVisible(bySliderSectionXpath).click();
        visually.check(SCREEN_NAME, "Slider Section Screen", Target.window());
        WebElement firstSliderElement = driver.findElement(byFirstSliderElementId);
        WebElement secondSliderElement = driver.findElement(bySecondSliderElementId);
        driver.multiTouchOnElements(firstSliderElement, secondSliderElement);
        return this;
    }

    @Override
    public float getSliderValue() {
        float actualSliderValue = Float.parseFloat(driver.waitTillElementIsPresent(bySliderValueXpath).getText());
        visually.check(SCREEN_NAME, "Slider value check", Target.region(bySliderValueXpath));
        return actualSliderValue;
    }
}
