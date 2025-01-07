package com.znsio.teswiz.businessLayer.ajio;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.screen.ajio.HomeScreen;
import com.znsio.teswiz.screen.ajio.SearchScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;

import static org.assertj.core.api.Assertions.assertThat;

public class HomeBL {
    private static final Logger LOGGER = LogManager.getLogger(HomeBL.class.getName());
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    public HomeBL(String userPersona, Platform forPlatform) {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = userPersona;
        this.currentPlatform = forPlatform;
        Runner.setCurrentDriverForUser(userPersona, forPlatform, context);
    }

    public HomeBL() {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = SAMPLE_TEST_CONTEXT.ME;
        this.currentPlatform = Runner.getPlatform();
    }


    public HomeBL openProduct(String product, String category, String gender) {
        assertThat(HomeScreen.get()
                           .goToMenu()
                           .selectProductFromCategory(product, category, gender)
                           .isProductListLoaded(product))
                .as("Selected Product list is not loaded")
                .isTrue();
        return this;
    }

    public HomeBL handlePopups() {
        HomeScreen.get().clickOnAllowToSendNotifications().clickOnAllowToSendNotifications().clickOnAllowLocation().clickOnAllowLocationWhileUsingApp().relaunchApplication();
        return this;
    }

    public SearchBL searchProduct(String productName) {
        new HomeBL().handlePopups();
        HomeScreen.get().searchForTheProduct(productName);
        assertThat(SearchScreen.get().getProductListingPageHeader().toLowerCase()).as("Product searched is not displayed").contains(productName.toLowerCase());
        return new SearchBL();
    }
}
