package com.znsio.teswiz.steps;

import com.znsio.teswiz.businessLayer.search.SearchBL;
import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.runner.Runner;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SearchSteps {
    private static final Logger LOGGER = LogManager.getLogger(SearchSteps.class.getName());
    private final TestExecutionContext context;

    public SearchSteps() {
        context = SessionContext.getTestExecutionContext(Thread.currentThread().getId());
        LOGGER.info("context: " + context.getTestName());
    }

    @When("{string} searches for {string}")
    public void searchesFor(String userPersona, String searchFor) {
        Platform onPlatform = Runner.getPlatformForUser(userPersona);
        new SearchBL(userPersona, onPlatform).searchFor(searchFor);
    }
}
