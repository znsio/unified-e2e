package com.znsio.teswiz.businessLayer.interactiveCalculatorCLI;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.entities.SAMPLE_TEST_CONTEXT;
import com.znsio.teswiz.entities.TEST_CONTEXT;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.tools.cmd.AsyncCommandLineExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;
import org.jetbrains.annotations.NotNull;

import static org.assertj.core.api.Assertions.assertThat;

public class InteractiveCalculatorCLIBL {
    private static final Logger LOGGER = LogManager.getLogger(InteractiveCalculatorCLIBL.class.getName());
    private final TestExecutionContext context;
    private final SoftAssertions softly;
    private final String currentUserPersona;
    private final Platform currentPlatform;

    public InteractiveCalculatorCLIBL() {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        softly = Runner.getSoftAssertion(threadId);
        this.currentUserPersona = SAMPLE_TEST_CONTEXT.ME;
        this.currentPlatform = Runner.getPlatform();
    }

    public InteractiveCalculatorCLIBL launchInteractiveCLIForCalculator() {
        String expectedResponse = """
                ----------------------------------------------------
                Welcome to the Simple Command Line Calculator
                Please choose an option:
                1. Add
                2. Subtract
                3. Exit
                4. Log Errors
                ----------------------------------------------------""";

        String command = "./src/test/resources/tools/calculator.sh";
        int timeoutInSeconds = 2;
        String actualResponse = runCommand(command, timeoutInSeconds);
        LOGGER.info("Actual response: %n%s".formatted(actualResponse));
        assertThat(actualResponse).as("Output of command: '%s' is incorrect".formatted(command)).isEqualTo(expectedResponse);
        return this;
    }

    public InteractiveCalculatorCLIBL add(int number1, int number2) {
        String expectedResponse = """
                Addition:
                
                Enter first number:""";

        String command = "1";
        int timeoutInSeconds = 2;
        String actualResponse = runCommand(command, timeoutInSeconds);
        LOGGER.info("Actual response: %n%s".formatted(actualResponse));
        assertThat(actualResponse).as("Output of command: '%s' is incorrect".formatted(command)).isEqualTo(expectedResponse);

        expectedResponse = """
                Enter second number:""";

        command = String.valueOf(number1);
        actualResponse = runCommand(command, timeoutInSeconds);
        LOGGER.info("Actual response: %n%s".formatted(actualResponse));
        assertThat(actualResponse).as("Output of command: '%s' is incorrect".formatted(command)).isEqualTo(expectedResponse);

        expectedResponse = """
                [32mResult: 67[0m
                ----------------------------------------------------
                Welcome to the Simple Command Line Calculator
                Please choose an option:
                1. Add
                2. Subtract
                3. Exit
                4. Log Errors
                ----------------------------------------------------""";

        command = String.valueOf(number2);
        actualResponse = runCommand(command, timeoutInSeconds);
        LOGGER.info("Actual response: %n%s".formatted(actualResponse));
        assertThat(actualResponse).as("Output of command: '%s' is incorrect".formatted(command)).isEqualTo(expectedResponse);
        return this;
    }

    public InteractiveCalculatorCLIBL subtract(int number1, int number2) {
        String expectedResponse = """
                Subtraction:
                
                Enter first number:""";

        String command = "2";
        int timeoutInSeconds = 2;
        String actualResponse = runCommand(command, timeoutInSeconds);
        LOGGER.info("Actual response: %n%s".formatted(actualResponse));
        assertThat(actualResponse).as("Output of command: '%s' is incorrect".formatted(command)).isEqualTo(expectedResponse);

        expectedResponse = """
                Enter second number:""";

        command = String.valueOf(number1);
        actualResponse = runCommand(command, timeoutInSeconds);
        LOGGER.info("Actual response: %n%s".formatted(actualResponse));
        assertThat(actualResponse).as("Output of command: '%s' is incorrect".formatted(command)).isEqualTo(expectedResponse);

        expectedResponse = """
                [32mResult: 19[0m
                ----------------------------------------------------
                Welcome to the Simple Command Line Calculator
                Please choose an option:
                1. Add
                2. Subtract
                3. Exit
                4. Log Errors
                ----------------------------------------------------""";

        command = String.valueOf(number2);
        actualResponse = runCommand(command, timeoutInSeconds);
        LOGGER.info("Actual response: %n%s".formatted(actualResponse));
        assertThat(actualResponse).as("Output of command: '%s' is incorrect".formatted(command)).isEqualTo(expectedResponse);
        return this;
    }

    public InteractiveCalculatorCLIBL seeInvalidMessages() {
        String command = "4";
        String expectedResponse = """
                Logging error to stderr in red.
                ----------------------------------------------------
                Welcome to the Simple Command Line Calculator
                Please choose an option:
                1. Add
                2. Subtract
                3. Exit
                4. Log Errors
                ----------------------------------------------------""";
        String actualResponse = runCommand(command, 5);
        LOGGER.info("Actual response: %n%s".formatted(actualResponse));
        softly.assertThat(actualResponse).as("Output of command: '" + command + "' is incorrect").isEqualTo(expectedResponse);
        return this;
    }

    public InteractiveCalculatorCLIBL closeCalculatorCLI() {
        String command = "3";
        String expectedResponse = "Exiting the calculator. Goodbye!";
        String actualResponse = runCommand(command, 5);
        LOGGER.info("Actual response: %n%s".formatted(actualResponse));
        softly.assertThat(actualResponse).as("Output of command: '" + command + "' is incorrect").isEqualTo(expectedResponse);
        return this;
    }

    private @NotNull String runCommand(String command, int timeoutInSeconds) {
        AsyncCommandLineExecutor asyncCommandLineExecutor = (AsyncCommandLineExecutor) context.getTestState(TEST_CONTEXT.ASYNC_COMMAND_LINE_EXECUTOR);
        AsyncCommandLineExecutor.CommandResult result = asyncCommandLineExecutor.sendCommand(command, timeoutInSeconds);
        LOGGER.info("%n--> AsyncCommandLineExecutor output begin: Command: '%s'%n%s%n-->  AsyncCommandLineExecutor output end%n".formatted(command, result.getOutput()));
        return result.getOutput();
    }
}
