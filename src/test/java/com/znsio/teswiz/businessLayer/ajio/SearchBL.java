package com.znsio.teswiz.businessLayer.ajio;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.screen.ajio.CartScreen;
import com.znsio.teswiz.screen.ajio.HomeScreen;
import com.znsio.teswiz.screen.ajio.ProductScreen;
import com.znsio.teswiz.screen.ajio.SearchScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchBL {
    private static final Logger LOGGER = LogManager.getLogger(SearchBL.class.getName());
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    public SearchBL(String userPersona, Platform forPlatform) {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = userPersona;
        this.currentPlatform = forPlatform;
        Runner.setCurrentDriverForUser(userPersona, forPlatform, context);
    }

    public SearchBL() {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = SAMPLE_TEST_CONTEXT.ME;
        this.currentPlatform = Runner.getPlatform();
    }

    public SearchBL searchProduct(Map searchData) {
        LOGGER.info("searchProduct" + searchData);
        SearchScreen searchScreen = HomeScreen.get().attachFileToDevice(searchData).searchByImage();
        assertThat(searchScreen.numberOfProductFound()).as("Number of results found for product")
                .isGreaterThan(0);
        searchScreen.selectProduct();
        return this;
    }

    public SearchBL prepareCart() {
        ProductScreen productScreen = ProductScreen.get();
        context.addTestState("productName", productScreen.getProductName());
        LOGGER.info("productName: " + context.getTestState("productName"));
        productScreen.addProductToCart();
        return this;
    }

    public SearchBL verifyCart() {
        String actualProductName = CartScreen.get().getActualProductName();
        LOGGER.info("Actual product name in the cart" + actualProductName);
        assertThat(actualProductName).as("Product in the Cart")
                .isEqualTo(context.getTestState("productName"));
        return this;
    }

    public ProductBL selectFirstItem(String userPersona) {
        ProductScreen productScreen = ProductScreen.get();
        SearchScreen.get().selectFirstItemFromList();
        assertThat(productScreen.isProductBrandNameVisible()).as("Item name is not visible").isTrue();
        context.addTestState(userPersona + " ProductName", productScreen.getProductName());
        return new ProductBL();
    }
}
