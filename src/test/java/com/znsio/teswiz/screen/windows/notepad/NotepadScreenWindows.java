package com.znsio.teswiz.screen.windows.notepad;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.notepad.NotepadScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

public class NotepadScreenWindows
        extends NotepadScreen {
    private static final Logger LOGGER = LogManager.getLogger(NotepadScreenWindows.class.getName());
    private static final By byEditorName = By.name("Text Editor");
    private final Driver driver;
    private final Visual visually;
    private final String SCREEN_NAME = NotepadScreenWindows.class.getSimpleName();

    public NotepadScreenWindows(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public NotepadScreen typeMessage(String message) {
        LOGGER.info(String.format("Typing message: '%s'", message));
        driver.findElement(byEditorName).sendKeys(message);
        visually.checkWindow(SCREEN_NAME, "Typed message in Notepad");
        return this;
    }
}
