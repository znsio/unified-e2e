package com.znsio.teswiz.steps;

import com.znsio.teswiz.businessLayer.heartbeat.HeartBeatBL;
import com.znsio.teswiz.businessLayer.theapp.AppBL;
import com.znsio.teswiz.businessLayer.theapp.ClipboardBL;
import com.znsio.teswiz.businessLayer.theapp.EchoBL;
import com.znsio.teswiz.businessLayer.theapp.FileUploadBL;
import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.entities.TEST_CONTEXT;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TheAppSteps {
    private static final Logger LOGGER = LogManager.getLogger(TheAppSteps.class.getName());
    private final TestExecutionContext context;

    public TheAppSteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        LOGGER.info("context: " + context.getTestName());
    }

    @When("I login with invalid credentials - {string}, {string}")
    public void iLoginWithInvalidCredentials(String username, String password) {
        LOGGER.info(System.out.printf(
                "iLoginWithInvalidCredentials - Persona:'%s', Username: '%s', Password:'%s', " +
                "Platform: '%s'",
                SAMPLE_TEST_CONTEXT.ME, username, password, Runner.getPlatform()));
        context.addTestState(TEST_CONTEXT.UPDATED_BROWSER_CONFIG_FILE_FOR_THIS_TEST,
                             "./configs/browser_config.json");
        context.addTestState(TEST_CONTEXT.UPDATED_BASE_URL_FOR_WEB, "BASE_URL");
        Drivers.createDriverFor(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform(), context);
        context.addTestState(SAMPLE_TEST_CONTEXT.ME, username);
        new AppBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).provideInvalidDetailsForSignup(
                username, password);
    }

    @When("I go back")
    public void iGoBack() {
        new AppBL().goBack();
    }

    @Given("{string} login with invalid credentials - {string}, {string} on {string}")
    public void loginWithInvalidCredentialsOn(String userPersona, String username, String password,
            String onPlatform) {
        LOGGER.info(System.out.printf(
                "LoginWithInvalidCredentials - Persona:'%s', Username: '%s', Password:'%s', " +
                "Platform: '%s'",
                userPersona, username, password, onPlatform));
        context.addTestState(userPersona, username);
        Drivers.createDriverFor(userPersona, Platform.valueOf(onPlatform), context);
        new AppBL(userPersona, Platform.valueOf(onPlatform)).provideInvalidDetailsForSignup(
                username, password);
    }

    @Then("I try to login again with invalid credentials - {string}, {string}")
    public void iTryToLoginAgainWithInvalidCredentials(String username, String password) {
        LOGGER.info(System.out.printf(
                "iTryToLoginAgainWithInvalidCredentials - Username: '%s', Password:'%s'", username,
                password));
        new AppBL().loginAgain(username, password);
    }

    @When("{string} login with invalid credentials - {string}, {string}")
    public void loginWithInvalidCredentials(String userPersona, String username, String password) {
        Platform onPlatform = Runner.getPlatformForUser(userPersona);
        LOGGER.info(System.out.printf(
                "LoginWithInvalidCredentials - Persona:'%s', Username: '%s', Password:'%s', " +
                "Platform: '%s'",
                SAMPLE_TEST_CONTEXT.ME, username, password, onPlatform.name()));
        new AppBL(userPersona, onPlatform).provideInvalidDetailsForSignup(username, password);
    }

    @When("{string} login again with invalid credentials - {string}, {string}")
    public void loginAgainWithInvalidCredentials(String userPersona, String username,
            String password) {
        Platform onPlatform = Runner.getPlatformForUser(userPersona);
        LOGGER.info(System.out.printf(
                "LoginWithInvalidCredentials - Persona:'%s', Username: '%s', Password:'%s', " +
                "Platform: '%s'",
                SAMPLE_TEST_CONTEXT.ME, username, password, onPlatform.name()));
        new AppBL(userPersona, onPlatform).loginAgain(username, password);
    }

    @Then("I can echo {string} in the message box")
    public void iCanEchoInTheMessageBox(String message) {
        new EchoBL().echoMessage(message);
    }

    @Given("I start the app")
    public void iStartTheApp() {
        LOGGER.info(System.out.printf("iStartTheApp - Persona:'%s'", SAMPLE_TEST_CONTEXT.ME));
        Drivers.createDriverFor(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform(), context);
        new AppBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform());
    }

    @When("I set {string} in the clipboard")
    public void iSetInTheClipboard(String content) {
        new ClipboardBL().setContentInClipboard(content);
    }

    @Then("I can see the content saved in the clipboard")
    public void iCanSeeTheContentSavedInTheClipboard() {
        String contentExpectedInClipboard = context.getTestStateAsString("contentInClipboard");
        new ClipboardBL().verifyContentIsSaved(contentExpectedInClipboard);
    }

    @Given("I save {string} in the clipboard")
    public void iSaveInTheClipboard(String content) {
        LOGGER.info(System.out.printf("iStartTheApp - Persona:'%s'", SAMPLE_TEST_CONTEXT.ME));
        Drivers.createDriverFor(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform(), context);
        new ClipboardBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).saveContentInClipboard(
                content);
    }

    @Given("I am on file upload page")
    public void iAmOnFileUploadPage() {
        LOGGER.info(System.out.printf("iStartTheApp - Persona:'%s'", SAMPLE_TEST_CONTEXT.ME));
        Drivers.createDriverFor(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform(), context);
        new FileUploadBL(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform()).navigationToUploadScreen();
    }

    @When("I upload the {string} file")
    public void iUploadTheFile(String file) {
        new FileUploadBL().uploadFile(Runner.getTestDataAsMap(file));
    }

    @Then("File is uploaded successfully")
    public void fileIsUploadedSuccessfully() {
        new FileUploadBL().verifyFileUpload();
    }

    @When("{string} switch my role to {string}")
    public void switchMyRoleTo(String currentUserPersona, String newUserPersona) {
        Drivers.assignNewPersonaToExistingDriver(currentUserPersona, newUserPersona, context);
    }

    @Then("{string} can login again with invalid credentials - {string}, {string}")
    public void canLoginAgainWithInvalidCredentials(String userPersona, String username,
            String password) {
        LOGGER.info(System.out.printf(
                "'%s' canLoginAgainWithInvalidCredentials - Username: '%s', Password:'%s'",
                userPersona, username, password));
        Platform platformForUser = Runner.getPlatformForUser(userPersona);
        new AppBL(userPersona, platformForUser).loginAgain(username, password);
    }

    @And("{string} login to TheApp with invalid credentials - {string}, " + "{string}")
    public void loginToTheAppWithInvalidCredentials(String userPersona, String username,
            String password) {
        LOGGER.info("Active thread count: " + Thread.activeCount());
        LOGGER.info(System.out.printf(
                "'%s' loginToTheAppWithInvalidCredentials - Username: '%s', Password:'%s'",
                userPersona, username, password));
        Platform currentPlatform = Runner.getPlatform();
        Drivers.createDriverFor(userPersona, currentPlatform, context);
        LOGGER.info("Active thread count: " + Thread.activeCount());
        new AppBL(userPersona, currentPlatform).provideInvalidDetailsForSignup(username, password);
        new HeartBeatBL().startHeatBeat(userPersona);
    }


    @And("I login to the switched TheApp with invalid credentials - {string}, {string}")
    public void loginToTheSwitchedTheAppWithInvalidCredentials(String username, String password) {
        new AppBL().provideInvalidDetailsForSignup(username, password);
    }

    @When("{string} changed to {string}")
    public void changedTo(String oldUserPersona, String newUserPersona) {
        Drivers.assignNewPersonaToExistingDriver(oldUserPersona, newUserPersona, context);
    }

    @When("I switch to theapp")
    public void iSwitchToTheapp() {
        new AppBL().stopTheAppAndLaunchItAgain();
    }

    @And("I force stop theapp")
    public void iForceStopTheapp() {
        new AppBL().forceStopTheApp();
    }
}
