package com.znsio.teswiz.screen.android.vodqa;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.vodqa.DragAndDropScreen;
import io.appium.java_client.AppiumBy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

public class DragAndDropScreenAndroid extends DragAndDropScreen {

    private static final String SCREEN_NAME = DragAndDropScreenAndroid.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);
    private final Driver driver;
    private final Visual visually;
    private final By byCircleDroppedAccessibilityId = AppiumBy.accessibilityId("success");
    private final By byDraggableObjectAccessibilityId = AppiumBy.accessibilityId("dragMe");
    private final By byDropZoneAccessibilityId = AppiumBy.accessibilityId("dropzone");


    public DragAndDropScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public boolean isMessageVisible() {
        driver.waitTillElementIsPresent(byCircleDroppedAccessibilityId);
        visually.checkWindow(SCREEN_NAME, "Circle Dropped");
        return driver.findElement(byCircleDroppedAccessibilityId).isDisplayed();
    }

    @Override
    public DragAndDropScreen dragAndDropCircleObject() {
        driver.waitTillElementIsVisible(byDraggableObjectAccessibilityId);
        visually.checkWindow(SCREEN_NAME, "Drag and Drop screen");
        driver.dragAndDrop(byDraggableObjectAccessibilityId, byDropZoneAccessibilityId);
        return this;
    }
}
