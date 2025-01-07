package com.znsio.teswiz.steps;

import com.znsio.teswiz.businessLayer.ajio.HomeBL;
import com.znsio.teswiz.businessLayer.ajio.ProductBL;
import com.znsio.teswiz.businessLayer.ajio.SearchBL;
import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AjioSteps {
    private static final Logger LOGGER = LogManager.getLogger(AjioSteps.class.getName());
    private final TestExecutionContext context;

    public AjioSteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        LOGGER.info("context: " + context.getTestName());
    }

    @Given("I search for products using {string}")
    public void iSearchForProductsUsing(String searchtype) {
        LOGGER.info(System.out.printf("iSearchForProductsUsing:'%s' - Persona:'%s'", searchtype,
                                      SAMPLE_TEST_CONTEXT.ME));
        Drivers.createDriverFor(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform(), context);
        new HomeBL().handlePopups();
        new SearchBL().searchProduct(Runner.getTestDataAsMap(searchtype));
    }

    @When("I add the product to the cart")
    public void iAddTheProductToTheCart() {
        LOGGER.info(System.out.printf("iAddTheProductToTheCart:- Persona:'%s'",
                                      SAMPLE_TEST_CONTEXT.ME));
        new SearchBL().prepareCart();
    }

    @Then("I should see the product in the cart")
    public void iShouldSeeTheProductInTheCart() {
        LOGGER.info(System.out.printf("iShouldSeeTheProductInTheCart:- Persona:'%s'",
                                      SAMPLE_TEST_CONTEXT.ME));
        new SearchBL().verifyCart();
    }

    @Given("I open {string} from {string} section for {string}")
    public void iOpenShirtsSectionForMen(String product, String category, String gender) {
        Drivers.createDriverFor(SAMPLE_TEST_CONTEXT.ME, Runner.getPlatform(), context);
        new HomeBL().handlePopups().openProduct(product, category, gender);
    }


    @When("I select the first result")
    public void iSelectTheFirstResult() {
        new ProductBL().selectTheFirstResultFromList();
    }

    @Then("I should be able to perform flick and view images")
    public void iShouldBeAbleToPerformFlickAndViewImages() {
        new ProductBL().flickAndViewImages();
    }

    @Given("{string} search {string} item on {string}")
    public void searchItemOn(String userPersona, String product, String onPlatform) {
        LOGGER.info(System.out.printf(
                "LoginWithValidCredentials - Persona:'%s', Platform: '%s'", userPersona, onPlatform));
        context.addTestState(userPersona, userPersona);
        Drivers.createDriverFor(userPersona, Platform.valueOf(onPlatform), context);
        new HomeBL(userPersona, Runner.getPlatformForUser(userPersona)).searchProduct(product);
    }

    @When("{string} select first item")
    public void selectFirstItem(String userPersona) {
        new SearchBL(userPersona, Runner.getPlatformForUser(userPersona)).selectFirstItem(userPersona);
    }

    @Then("{string} add item to cart")
    public void addItemToCart(String userPersona) {
        new ProductBL(userPersona, Runner.getPlatformForUser(userPersona)).addItemToCart(userPersona);
    }
}
