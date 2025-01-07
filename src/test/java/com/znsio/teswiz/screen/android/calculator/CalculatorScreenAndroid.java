package com.znsio.teswiz.screen.android.calculator;

import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.calculator.CalculatorScreen;
import org.openqa.selenium.By;

import static com.znsio.teswiz.tools.Wait.waitFor;

public class CalculatorScreenAndroid
        extends CalculatorScreen {
    private final Driver driver;
    private final Visual visually;
    private final String SCREEN_NAME = CalculatorScreenAndroid.class.getSimpleName();

    public CalculatorScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public CalculatorScreen handlePopupIfPresent() {
        waitFor(1);
        visually.checkWindow(SCREEN_NAME, "Calculator launched");
        boolean isUpgradeAppNotificationElement = driver.isElementPresent(
                By.id("android:id/button1"));
        if (isUpgradeAppNotificationElement) {
            driver.findElement(By.id("android:id/button1")).click();
            waitFor(1);
        }
        boolean isClingElementPresent = driver.isElementPresent(
                By.id("com.android2.calculator3:id/cling_dismiss"));
        if (isClingElementPresent) {
            driver.findElementById("com.android2.calculator3:id/cling_dismiss").click();
            waitFor(1);
        }
        visually.checkWindow(SCREEN_NAME, "Calculator popup handled");
        return this;
    }

    @Override
    public CalculatorScreen selectNumber(String number) {
        driver.findElement(By.id("digit" + number)).click();
        visually.checkWindow(SCREEN_NAME, "Entered number " + number);
        return this;
    }

    @Override
    public CalculatorScreen pressOperation(String operation) {
        String mappedOperation;
        switch (operation.toLowerCase()) {
            case "plus":
                mappedOperation = "plus";
                break;
            case "subtract":
                mappedOperation = "minus";
                break;
            case "multiply":
                mappedOperation = "times";
                break;
            case "divide":
                mappedOperation = "divide";
                break;
            case "equals":
                mappedOperation = "equal";
                break;
            default:
                throw new RuntimeException("Operation " + operation + " is not supported");
        }
        driver.findElement(By.id(mappedOperation)).click();
        return this;
    }
}
