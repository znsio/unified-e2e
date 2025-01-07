package com.znsio.teswiz.tools.cmd;

import com.znsio.teswiz.context.TestExecutionContext;
import com.znsio.teswiz.entities.TEST_CONTEXT;
import com.znsio.teswiz.exceptions.CommandLineExecutorException;
import com.znsio.teswiz.runner.Runner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class AsyncCommandLineExecutor {
    private final Process process;
    private final PrintWriter commandWriter;
    private final BufferedReader outputReader;
    private static final Logger LOGGER = LogManager.getLogger(AsyncCommandLineExecutor.class.getName());
    private final TestExecutionContext context;

    public static class CommandResult {
        private final String output;

        public CommandResult(String output) {
            this.output = output;
        }

        public String getOutput() {
            return output;
        }
    }

    public AsyncCommandLineExecutor() {
        long threadId = Thread.currentThread().getId();
        this.context = Runner.getTestExecutionContext(threadId);
        this.context.addTestState(TEST_CONTEXT.CLI_COMMAND_NUMBER, 1);
        String os = Runner.OS_NAME.toLowerCase();

        ProcessBuilder processBuilder = os.contains("win")
                                        ? new ProcessBuilder("cmd.exe")
                                        : new ProcessBuilder("bash");

        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new CommandLineExecutorException("Unable to start the AsyncCommandLineExecutor", e);
        }

        // Initialize writers and readers
        commandWriter = new PrintWriter(process.getOutputStream(), true);
        outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

    public CommandResult sendCommand(String command, int timeoutInSeconds) {
        updateCurrentCommandNumber();
        final String platformCommand = Runner.IS_WINDOWS
                                       ? "/c " + command
                                       : command;

        LOGGER.info("Executing command: '%s', with timeout: '%d' seconds".formatted(platformCommand, timeoutInSeconds));

        StringBuilder output = new StringBuilder();

        // Send the command
        commandWriter.println(platformCommand);
        commandWriter.flush();

        try {
            // Wait for the specified timeout
            TimeUnit.SECONDS.sleep(timeoutInSeconds);

            // Read any accumulated output after the timeout
            while (outputReader.ready()) {  // only read if data is available
                String line = outputReader.readLine();
                if (line != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.debug("Command '%s' execution was interrupted.".formatted(command));
        } catch (IOException e) {
            LOGGER.error("Error reading stdout: %s".formatted(e.getMessage()), e.getCause());
            throw new CommandLineExecutorException("Error reading stdout", e);
        }

        return new CommandResult(output.toString().trim());
    }

    public void close() {
        try {
            if (process.isAlive()) {
                process.destroy();
            }
            commandWriter.close();
            outputReader.close();
        } catch (IOException e) {
            LOGGER.debug("Error closing resources: " + e.getMessage());
        }
    }

    private void updateCurrentCommandNumber() {
        int commmandNumber = (int) context.getTestState(TEST_CONTEXT.CLI_COMMAND_NUMBER);
        LOGGER.info("AsyncCommandLineExecutor: Running command # " + commmandNumber++);
        context.addTestState(TEST_CONTEXT.CLI_COMMAND_NUMBER, commmandNumber);
    }

}
