package com.znsio.teswiz.businessLayer.vodqa;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.screen.vodqa.DragAndDropScreen;
import com.znsio.teswiz.screen.vodqa.VodqaScreen;
import com.znsio.teswiz.screen.vodqa.WebViewScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.Dimension;

import static org.assertj.core.api.Assertions.assertThat;

public class VodqaBL {
    private static final Logger LOGGER = LogManager.getLogger(VodqaBL.class.getName());
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    public VodqaBL(String userPersona, Platform forPlatform) {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = userPersona;
        this.currentPlatform = forPlatform;
        Runner.setCurrentDriverForUser(userPersona, forPlatform, context);
    }

    public VodqaBL() {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = SAMPLE_TEST_CONTEXT.ME;
        this.currentPlatform = Runner.getPlatform();
    }

    public VodqaBL login() {
        VodqaScreen.get().login();
        return this;
    }

    public VodqaBL scrollFromOneElementPointToAnother() {
        VodqaScreen.get().scrollFromOneElementPointToAnother();
        return this;
    }

    public VodqaBL verifySwipe(String elementText) {
        assertThat(VodqaScreen.get().isSwipeSuccessful(elementText))
                .as("swipe was not successful")
                .isTrue();
        return this;
    }

    public VodqaBL selectScreenAndSwipeByPassingPercentageAttributes(int atPercentScreenHeight, int fromPercentScreenWidth, int toPercentScreenWidth, String screenName) {
        VodqaScreen.get().selectScreen(screenName)
                .swipeByPassingPercentageAttributes(atPercentScreenHeight, fromPercentScreenWidth, toPercentScreenWidth);
        return this;
    }

    public VodqaBL scrollDownByScreenSizeOnVerticalSwipingScreen() {
        VodqaScreen.get().openVerticalSwipingScreen().scrollDownByScreenSize();
        return this;
    }

    public VodqaBL tapInTheMiddleOfTheScreen() {
        LOGGER.info("performTapActionInTheMiddle(): perform tap operation in the middle of the screen");
        VodqaScreen.get().tapInTheMiddle();
        return this;
    }

    public VodqaBL verifyUserMoveToNextPage(String pageHeading) {
        LOGGER.info("performTapActionInTheMiddle(): verify the operation has been executed successfully or not");
        assertThat(VodqaScreen.get().isPreviousPageHeadingVisible(pageHeading)).as(String.format("User is still on %s page", pageHeading)).isFalse();
        return this;
    }

    public VodqaBL enterAndVerifyLoginOptionUnderWebViewSection() {
        LOGGER.info("Entering into hacker news under webView section");
        assertThat(VodqaScreen.get().enterIntoNewsWebViewSection()
                           .isUserOnNewsWebViewScreen())
                .as("User unable to navigate to news webview screen")
                .isTrue();

        LOGGER.info("Verify login option is visible for hacker news under web view");
        assertThat(WebViewScreen.get().isLoginOptionVisible())
                .as("Login Option for hacker news under web view is not visible")
                .isTrue();
        return this;
    }

    public VodqaBL enterIntoNativeViewSection() {
        LOGGER.info("Navigating into Native view section");
        assertThat(WebViewScreen.get().navigateToSamplesList()
                           .enterIntoNativeViewSection()
                           .isUserOnNativeViewScreen())
                .as("User Unable to navigate to native view section")
                .isTrue();
        return this;
    }

    public VodqaBL verifyAppWorksInBackground(int time) {
        LOGGER.info("Validating app working in background");
        boolean isAppWorkInBackground = VodqaScreen.get().putAppInTheBackground(time).isAppWorkingInBackground();
        assertThat(isAppWorkInBackground).as(String.format("App do not works in background")).isTrue();
        return this;
    }

    public VodqaBL isElementWithTextVisible(String elementText) {
        boolean isScrollSuccessful = VodqaScreen.get().isElementWithTextVisible(elementText);
        assertThat(isScrollSuccessful).as("Scroll was not successful, text is not visible").isTrue();
        return this;
    }

    public VodqaBL scrollVerticallyByPercentageOnVerticalSwipingScreen(int fromPercentHeight, int toPercentHeight, int percentWidth) {
        VodqaScreen.get().openVerticalSwipingScreen().scrollVerticallyByPercentage(fromPercentHeight, toPercentHeight, percentWidth);
        return this;
    }

    public VodqaBL dragAndDropElement() {
        VodqaScreen.get().openDragAndDropScreen().dragAndDropCircleObject();
        return this;
    }

    public VodqaBL isMessageDisplayedOnTheScreen(String displayedMessage) {
        assertThat(DragAndDropScreen.get().isMessageVisible())
                .as(String.format("The %s message is not displayed", displayedMessage)).isTrue();
        return this;
    }

    public VodqaBL doubleTapOnAnElement() {
        assertThat(VodqaScreen.get().doubleTapOnElement()
                           .isDoubleTapSuccessful())
                .as("Double tap on element failed")
                .isTrue();
        return this;
    }

    public VodqaBL longPressOnElement() {
        VodqaScreen.get().longPressOnElement();
        return this;
    }

    public VodqaBL verifyLongPressedPopup() {
        assertThat(VodqaScreen.get().isLongPressedPopupVisible()).as("Long Pressed Popup is not visible").isTrue();
        return this;
    }

    public VodqaBL pinchAndZoomInOnAnElement() {
        VodqaScreen vodqaScreen = VodqaScreen.get();
        Dimension elementDimension = vodqaScreen.navigateToUImageView().getImageElementDimension();
        assertThat(vodqaScreen.pinchAndZoomInOnAnElement().isPinchAndZoomInSuccessful(elementDimension)).as("Pinch and Zoom In on an element failed").isTrue();
        return this;
    }

    public VodqaBL pinchAndZoomOutOnAnElement() {
        VodqaScreen vodqaScreen = VodqaScreen.get();
        Dimension elementDimension = vodqaScreen.navigateToUImageView().getImageElementDimension();
        assertThat(vodqaScreen.pinchAndZoomOutOnAnElement().isPinchAndZoomOutSuccessful(elementDimension)).as("Pinch and Zoom Out on an element failed").isTrue();
        return this;
    }

    public VodqaBL performMultiTouchForBothSilders(float sliderValue) {
        VodqaScreen.get().multiTouchOnElements();
        assertThat(VodqaScreen.get().getSliderValue()).as("Multi Touch failed as slider value is not equal").isEqualTo(sliderValue);
        return this;
    }
}
