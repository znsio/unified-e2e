package com.znsio.teswiz.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;

class AspectJMethodLoggerTest {
    private static final Logger LOGGER = LogManager.getLogger(AspectJMethodLoggerTest.class.getName());
    private static final String className = AspectJMethodLoggerTest.class.getSimpleName();
    private static final String LOG_DIR = "./target/testLogs";

    private Object[] params;

    @BeforeClass
    public static void setupBefore() {
        LOGGER.info("Using LOG_DIR: " + System.getProperty("LOG_DIR"));
    }

    @Test
    void validateLoggerWithNoArguments() {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        String methodName = stackTraceElement.getMethodName();
        int lineNumber = stackTraceElement.getLineNumber();
        String enteringMethodPrefix = String.format("Entering method: '%s.%s:%d'", className, methodName, lineNumber);
        params = new Object[]{};
        Assertions.assertThat(enteringMethodPrefix).isEqualTo(AspectJMethodLoggers.generateBeforeMethodAspectJLogger(className, methodName, lineNumber, params));
    }

    @Test
    void validateLoggerWithNullArguments() {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        String methodName = stackTraceElement.getMethodName();
        int lineNumber = stackTraceElement.getLineNumber();
        String enteringMethodPrefix = String.format("Entering method: '%s.%s:%d'", className, methodName, lineNumber);
        params = new Object[]{null};
        Assertions.assertThat(String.format("%s%n\tParam-0: Value: \"null\"", enteringMethodPrefix)).isEqualTo(AspectJMethodLoggers.generateBeforeMethodAspectJLogger(className, methodName, lineNumber, params));
    }

    @Test
    void validateLoggerWithStringArguments() {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        String methodName = stackTraceElement.getMethodName();
        int lineNumber = stackTraceElement.getLineNumber();
        String enteringMethodPrefix = String.format("Entering method: '%s.%s:%d'", className, methodName, lineNumber);
        params = new Object[]{"StringParam"};
        Assertions.assertThat(String.format("%s%n\tParam-0: Type: 'String', Value: \"StringParam\"", enteringMethodPrefix)).isEqualTo(AspectJMethodLoggers.generateBeforeMethodAspectJLogger(className, methodName, lineNumber, params));
    }

    @Test
    void validateLoggerWithArrayArguments() {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        String methodName = stackTraceElement.getMethodName();
        int lineNumber = stackTraceElement.getLineNumber();
        String enteringMethodPrefix = String.format("Entering method: '%s.%s:%d'", className, methodName, lineNumber);
        params = new Object[]{new int[]{1, 2, 3}};
        Assertions.assertThat(String.format("%s\n\tParam-0: Type: 'int[]', Value: \"[1, 2, 3]\"", enteringMethodPrefix)).isEqualTo(AspectJMethodLoggers.generateBeforeMethodAspectJLogger(className, methodName, lineNumber, params));
    }

    @Test
    void validateLoggerWithCollectionsArguments() {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        String methodName = stackTraceElement.getMethodName();
        int lineNumber = stackTraceElement.getLineNumber();
        String enteringMethodPrefix = String.format("Entering method: '%s.%s:%d'", className, methodName, lineNumber);
        params = new Object[]{new ArrayList<String>() {{
            add("Array");
            add("List");
        }}, new HashMap<String, Integer>() {{
            put("Hash", 1);
            put("Map", 2);
        }}};
        Assertions.assertThat(String.format("%s\n\tParam-0: Type: '', Value: \"[Array, List]\"\n\tParam-1: Type: '', Value: \"{Hash=1, Map=2}\"", enteringMethodPrefix)).isEqualTo(AspectJMethodLoggers.generateBeforeMethodAspectJLogger(className, methodName, lineNumber, params));
    }

    @Test
    void validateLoggerWithDifferentCombinationArguments() {
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        String methodName = stackTraceElement.getMethodName();
        int lineNumber = stackTraceElement.getLineNumber();
        String enteringMethodPrefix = String.format("Entering method: '%s.%s:%d'", className, methodName, lineNumber);
        params = new Object[]{null, "StringParam", 1, new String[]{"String", "Array"}, new ArrayList<String>() {{
            add("Array");
            add("List");
        }}, new HashMap<String, Integer>() {{
            put("Hash", 1);
            put("Map", 2);
        }}};
        Assertions.assertThat(String.format("%s\n\tParam-0: Value: \"null\"\n\tParam-1: Type: 'String', Value: \"StringParam\"\n\tParam-2: Type: 'Integer', Value: \"1\"\n\tParam-3: Type: 'String[]', Value: \"[String, Array]\"\n\tParam-4: Type: '', Value: \"[Array, List]\"\n\tParam-5: Type: '', Value: \"{Hash=1, Map=2}\"", enteringMethodPrefix)).isEqualTo(AspectJMethodLoggers.generateBeforeMethodAspectJLogger(className, methodName, lineNumber, params));
    }

    @Test
    void exitMethodLoggerValidation() {
        String methodName = new Throwable().getStackTrace()[0].getMethodName();
        String exitMethodPrefix = String.format("Exiting method: '%s.%s'", className, methodName);
        Assertions.assertThat(exitMethodPrefix).isEqualTo(AspectJMethodLoggers.generateAfterMethodAspectJLogger(className, methodName));
    }
}
