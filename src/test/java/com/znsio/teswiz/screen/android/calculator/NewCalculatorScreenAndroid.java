package com.znsio.teswiz.screen.android.calculator;

import com.applitools.eyes.appium.AppiumCheckSettings;
import com.applitools.eyes.appium.Target;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.calculator.NewCalculatorScreen;
import org.openqa.selenium.By;

public class NewCalculatorScreenAndroid
        extends NewCalculatorScreen {
    private final Driver driver;
    private final Visual visually;
    private final String SCREEN_NAME = NewCalculatorScreenAndroid.class.getSimpleName();

    public NewCalculatorScreenAndroid(Driver driver, Visual visually) {
        this.driver = driver;
        this.visually = visually;
    }

    @Override
    public NewCalculatorScreen selectNumber(String number) {
        driver.findElement(By.id("digit_" + number)).click();
        visually.check(SCREEN_NAME, "selectNumber_" + number, Target.region(driver.findElement(By.id("digit_" + number))));
        return this;
    }

    @Override
    public NewCalculatorScreen pressOperation(String operation) {
        String mappedOperation;
        switch (operation.toLowerCase()) {
            case "plus":
                mappedOperation = "op_add";
                break;
            case "subtract":
                mappedOperation = "op_sub";
                break;
            case "multiply":
                mappedOperation = "op_mul";
                break;
            case "divide":
                mappedOperation = "op_div";
                break;
            case "equals":
                mappedOperation = "eq";
                break;
            default:
                throw new RuntimeException("Operation " + operation + " is not supported");
        }
        driver.findElement(By.id(mappedOperation)).click();
        visually.check(SCREEN_NAME, "pressOperation_" + operation, Target.region(driver.findElement(By.id(mappedOperation))));
        return this;
    }

    @Override
    public NewCalculatorScreen launch() {
        visually.check(SCREEN_NAME, "New Calculator Launched", (AppiumCheckSettings) Target.window().fully().ignoreCaret(true));
        return this;
    }
}
