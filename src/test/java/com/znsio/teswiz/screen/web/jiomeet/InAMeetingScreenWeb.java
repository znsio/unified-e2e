package com.znsio.teswiz.screen.web.jiomeet;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.jiomeet.InAMeetingScreen;
import com.znsio.teswiz.tools.ReportPortalLogger;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class InAMeetingScreenWeb
        extends InAMeetingScreen {
    private static final String SCREEN_NAME = InAMeetingScreenWeb.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private static final String NOT_YET_IMPLEMENTED = " not yet implemented";
    private static final By byMeetingInfoIconXpath = By.xpath("//div[@class='icon pointer']");
    private static final By byMicLabelXpath = By.xpath("//div[contains(@class, 'mic-section')]//img");
    private static final By byCurrentMeetingNumberXpath = By.xpath(
            "//div[text()='Meeting ID']/following-sibling::div");
    private static final By byCurrentMeetingPinXpath = By.xpath(
            "//div[text()='Password']/following-sibling::div");
    private static final By byCurrentMeetingInvitationLinkXpath = By.xpath(
            "//div[text()='Invitation Link']/following-sibling::div[1]");
    private static final By microPhoneButtonXpath = By.xpath("//div[@id = 'toggleMicButton']//div[contains(@class, 'img-holder')]");
    private final Driver driver;
    private final Visual visually;
    private final WebDriver innerDriver;
    private final TestExecutionContext context;


    public InAMeetingScreenWeb(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
        this.innerDriver = this.driver.getInnerDriver();
        long threadId = Thread.currentThread().getId();
        context = Runner.getTestExecutionContext(threadId);
    }

    @Override
    public boolean isMeetingStarted() {
        LOGGER.info("Has meeting started? " + getMicLabelText());
        return true;
    }

    @Override
    public String getMeetingId() {
        WebElement infoIcon = driver.waitTillElementIsPresent(byMeetingInfoIconXpath, 20);
        JavascriptExecutor js = (JavascriptExecutor) innerDriver;
        js.executeScript("arguments[0].click()", infoIcon);
        visually.takeScreenshot(SCREEN_NAME, "getCurrentMeetingDetails");
        String meetingId = (String) js.executeScript("return arguments[0].innerText",
                                                     driver.waitTillElementIsPresent(byCurrentMeetingNumberXpath));
        meetingId = meetingId.replaceAll("\\s", "");
        String pin = (String) js.executeScript("return arguments[0].innerText",
                                               driver.waitTillElementIsPresent(byCurrentMeetingPinXpath));
        String invitationLink = (String) js.executeScript("return arguments[0].innerText",
                                                          driver.waitTillElementIsPresent(byCurrentMeetingInvitationLinkXpath));
        js.executeScript("arguments[0].click()", infoIcon);//to close the meeting info frame
        visually.takeScreenshot(SCREEN_NAME, "After closing meeting info icon");
        LOGGER.info("On Web the meeting id: " + meetingId + " Password: " + pin);

        context.addTestState(SAMPLE_TEST_CONTEXT.MEETING_ID, meetingId);
        context.addTestState(SAMPLE_TEST_CONTEXT.MEETING_PASSWORD, pin);
        context.addTestState(SAMPLE_TEST_CONTEXT.INVITATION_LINK, invitationLink);
        return meetingId;
    }

    @Override
    public String getMeetingPassword() {
        String pin = context.getTestStateAsString(SAMPLE_TEST_CONTEXT.MEETING_PASSWORD);
        return pin;
    }

    @Override
    public InAMeetingScreen unmute() {
        enableInMeetingControls("unmute");
        driver.waitTillElementIsPresent(microPhoneButtonXpath).click();
        visually.checkWindow(SCREEN_NAME, "Mic is unmuted");
        return this;
    }

    @Override
    public InAMeetingScreen mute() {
        enableInMeetingControls("mute");
        driver.waitTillElementIsPresent(microPhoneButtonXpath).click();
        visually.checkWindow(SCREEN_NAME, "Mic is muted");
        return this;
    }


    @Override
    public String getMicLabelText() {
        LOGGER.info("getMicLabelText");
        enableInMeetingControls("getMicLabelText");
        String micLabelText = driver.waitTillElementIsPresent(byMicLabelXpath).getText().trim();
        visually.takeScreenshot(SCREEN_NAME, "in a meeting after micLabel text");
        LOGGER.info("getMicLabelText: mic label text : " + micLabelText);
        return micLabelText;
    }

    @Override
    public InAMeetingScreen openJioMeetNotification() {
        throw new NotImplementedException("Jio Meet Device Notification of Meeting is not available for Web");
    }

    private void enableInMeetingControls(String calledFrom) {
        try {
            LOGGER.info(String.format("enableInMeetingControls: Called from: '%s'%n", calledFrom));
            Actions actions = new Actions(innerDriver);
            actions.moveToElement(driver.waitForClickabilityOf(byMeetingInfoIconXpath))
                    .moveByOffset(25, 25).perform();
        } catch (Exception e) {
            String logMessage = String.format(
                    "Exception occurred : enableInMeetingControls%nException: %s",
                    e.getLocalizedMessage());
            LOGGER.info(logMessage);
            ReportPortalLogger.logDebugMessage(logMessage);
        }
    }
}
