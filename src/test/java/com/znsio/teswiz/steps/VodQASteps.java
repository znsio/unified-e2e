package com.znsio.teswiz.steps;

import com.znsio.teswiz.businessLayer.vodqa.VodqaBL;
import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VodQASteps {
    private static final Logger LOGGER = LogManager.getLogger(VodQASteps.class.getName());
    private final TestExecutionContext context;

    public VodQASteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        LOGGER.info("context: " + context.getTestName());
    }

    @Given("I login to vodqa application using valid credentials")
    public void loginToApplication() {
        Drivers.createDriverFor(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform(), context);
        new VodqaBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).login();
    }

    @When("I scroll from one to another element point on vertical swiping screen")
    public void scrollToElement() {
        new VodqaBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).scrollFromOneElementPointToAnother();
    }

    @When("I tap in the middle of the screen")
    public void iTapInTheMiddleOfTheScreen() {
        new VodqaBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).tapInTheMiddleOfTheScreen();
    }

    @Then("I am able to move from {string} page to next page")
    public void iAmAbleToMoveFromPageToNextPage(String pageHeading) {
        new VodqaBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).verifyUserMoveToNextPage(pageHeading);
    }

    @When("I scroll down by screen size on vertical swiping screen")
    public void iScrollDownByScreenSizeOnVerticalSwipingScreen() {
        new VodqaBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).scrollDownByScreenSizeOnVerticalSwipingScreen();
    }

    @Then("I am able to see element with text {string} on the screen")
    public void iAmAbleToSeeElementWithTextOnTheScreen(String elementText) {
        new VodqaBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).verifySwipe(elementText);
    }

    @When("I swipe at {int} percent height from {int} percent width to {int} percent width on {string} screen")
    public void iSwipeAtPercentHeightFromPercentWidthToPercentWidthOnScreen(int atPercentileHeight, int fromPercentageWidth, int toPercentageWidth, String screenName) {
        new VodqaBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform())
                .selectScreenAndSwipeByPassingPercentageAttributes(atPercentileHeight, fromPercentageWidth, toPercentageWidth, screenName);
    }

    @Then("I am able to view hacker news login button inside web view section")
    public void iAmAbleToViewHackerNewsLoginButtonInsideWebViewSection() {
        new VodqaBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).enterAndVerifyLoginOptionUnderWebViewSection();
    }

    @And("I am able to view section header by navigating inside native view section")
    public void iAmAbleToViewSectionHeaderByNavigatingInsideNativeViewSection() {
        new VodqaBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).enterIntoNativeViewSection();
    }

    @Then("App should work in background for {int} sec")
    public void appShouldWorkInBackgroundForDefinedTime(int time) {
        new VodqaBL().verifyAppWorksInBackground(time);
    }

    @Then("Element text {string} should be visible")
    public void elementTextShouldBeVisible(String elementText) {
        new VodqaBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).isElementWithTextVisible(elementText);
    }

    @When("I scroll vertically from {int} percent height to {int} percent height and {int} percent width")
    public void iScrollVerticallyFromPercentHeightToPercentHeightAndPercentWidth(int fromPercentHeight, int toPercentHeight, int percentWidth) {
        new VodqaBL().scrollVerticallyByPercentageOnVerticalSwipingScreen(fromPercentHeight, toPercentHeight, percentWidth);
    }

    @When("I long press on element")
    public void iLongPressOnElement() {
        new VodqaBL().longPressOnElement();
    }

    @Then("long pressed popup should be visible")
    public void longPressedPopupShouldBeVisible() {
        new VodqaBL().verifyLongPressedPopup();
    }

    @When("I drag the circle object to the drop target")
    public void iDragTheCircleObjectToDropTarget() {
        new VodqaBL().dragAndDropElement();
    }

    @Then("I am able to view {string} message")
    public void iAmAbleToViewMessage(String displayedMessage) {
        new VodqaBL().isMessageDisplayedOnTheScreen(displayedMessage);
    }

    @Then("I should be able to double tap on an element")
    public void iShouldBeAbleToDoubleTapOnAnElement() {
        new VodqaBL().doubleTapOnAnElement();
    }

    @Then("I should be able to pinch and zoom in on an element")
    public void iShouldBeAbleToPinchAndZoomInOnAnElement() {
        new VodqaBL().pinchAndZoomInOnAnElement();
    }

    @And("I should be able to pinch and zoom out on an element")
    public void iShouldBeAbleToPinchAndZoomOutOnAnElement() {
        new VodqaBL().pinchAndZoomOutOnAnElement();
    }

    @Then("I should be able set both sliders to value {float} by multi touch action")
    public void iShouldBeAbleSetBothSlidersToValueByMultiTouchAction(float setSliderValue) {
        new VodqaBL().performMultiTouchForBothSilders(setSliderValue);
    }
}
