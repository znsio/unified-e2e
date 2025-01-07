package com.znsio.teswiz.businessLayer.ajio;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.screen.ajio.CartScreen;
import com.znsio.teswiz.screen.ajio.ProductScreen;
import com.znsio.teswiz.screen.ajio.SearchScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductBL {
    private static final Logger LOGGER = LogManager.getLogger(ProductBL.class.getName());
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    public ProductBL(String userPersona, Platform forPlatform) {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = userPersona;
        this.currentPlatform = forPlatform;
        Runner.setCurrentDriverForUser(userPersona, forPlatform, context);
    }

    public ProductBL() {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = SAMPLE_TEST_CONTEXT.ME;
        this.currentPlatform = Runner.getPlatform();
    }

    public ProductBL selectTheFirstResultFromList() {
        assertThat(SearchScreen.get()
                           .selectProduct()
                           .isProductDetailsLoaded())
                .as("Product Details is not loaded")
                .isTrue();
        return this;
    }

    public ProductBL flickAndViewImages() {
        String finalElementId = ProductScreen.get().flickImage().isElementIdChanged();
        String initialElementId = (String) context.getTestState(SAMPLE_TEST_CONTEXT.INITIAL_ELEMENT_ID);
        assertThat(initialElementId).as("Unable to perform flick action").isNotEqualTo(finalElementId);
        return this;
    }

    public ProductBL addItemToCart(String userPersona) {
        ProductScreen productScreen = ProductScreen.get();
        productScreen.clickOnAddToCart().selectAvailableSize().clickOnAddToBagButton();
        assertThat(productScreen.getAddedToBagToastMessage()).as("Product is not added to cart")
                .isEqualTo("Added to Bag");
        productScreen.clickOnCartIcon();
        String actualProductName = CartScreen.get().getActualProductName();
        LOGGER.info("Actual product name in the cart" + actualProductName);
        assertThat(actualProductName).as("Product in the Cart")
                .isEqualTo(context.getTestState(userPersona + " ProductName"));
        return this;
    }
}
