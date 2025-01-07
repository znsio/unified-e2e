package com.znsio.teswiz.listener;

import com.epam.reportportal.cucumber.ScenarioReporter;
import com.epam.reportportal.utils.MemoizingSupplier;
import com.epam.ta.reportportal.ws.model.StartTestItemRQ;
import com.znsio.teswiz.runner.AppiumDriverManager;
import com.znsio.teswiz.runner.AppiumServerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CucumberScenarioReporterListener extends ScenarioReporter {

    private static final Logger LOGGER = LogManager.getLogger(CucumberScenarioReporterListener.class.getName());
    private static final String DUMMY_ROOT_SUITE_NAME = "My Tests";
    private static final String RP_STORY_TYPE = "SUITE";
    public AppiumServerManager appiumServerManager;
    public AppiumDriverManager appiumDriverManager;

    private static final Map<String, String> MIME_TYPES_EXTENSIONS =
        new HashMap() {
            {
                this.put("image/bmp", "bmp");
                this.put("image/gif", "gif");
                this.put("image/jpeg", "jpg");
                this.put("image/png", "png");
                this.put("image/svg+xml", "svg");
                this.put("video/ogg", "ogg");
            }
        };

    public CucumberScenarioReporterListener() throws Exception {
        LOGGER.info("CucumberScenarioReporterListener");
        appiumServerManager = new AppiumServerManager();
        appiumDriverManager = new AppiumDriverManager();
    }

    @Override
    protected void startRootItem() {
        this.rootSuiteId = new MemoizingSupplier(() -> {
            StartTestItemRQ rq = new StartTestItemRQ();
            rq.setName(DUMMY_ROOT_SUITE_NAME);
            rq.setStartTime(Calendar.getInstance().getTime());
            rq.setType(RP_STORY_TYPE);
            return this.getLaunch().startTestItem(rq);
        });
    }
}
