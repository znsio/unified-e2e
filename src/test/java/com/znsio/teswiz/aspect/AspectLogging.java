package com.znsio.teswiz.aspect;

import com.znsio.teswiz.context.SessionContext;
import com.znsio.teswiz.context.TestExecutionContext;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AspectLogging {
    private static final Logger LOGGER = LogManager.getLogger(AspectLogging.class.getName());
    private final TestExecutionContext context;

    public AspectLogging() {
        long threadId = Thread.currentThread().getId();
        context = SessionContext.getTestExecutionContext(threadId);
    }

    @Pointcut("execution(public * *.*.*.businessLayer.*.*.*(..))" +
              "|| execution(public * *.*.*.screen.*.*.*.*(..))" +
              "|| execution(public * *.*.*.entities.*.*(..))" +
              "|| execution(public * *.*.*.runner.*.*(..))" +
              "|| execution(public * *.*.*.steps.*Steps.*(..))")
    public void executionScope() {
    }

    @Before("executionScope()")
    public void beforeAnyMethod(JoinPoint joinPoint) {
        AspectJMethodLoggers.beforeAnyMethod(joinPoint, Level.INFO);
    }

    @After("executionScope()")
    public void afterAnyMethod(JoinPoint joinPoint) {
        AspectJMethodLoggers.afterAnyMethod(joinPoint, Level.INFO);
    }

}
