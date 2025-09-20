package toolyverse.io.toolyverse.infrastructure.config.aspect;

import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import toolyverse.io.toolyverse.infrastructure.exception.BusinessException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

@Aspect
@Component
public class AopLoggingHandler {

    private static final String BASE_PACKAGE = "toolyverse.io.toolyverse";
    private static final String UNKNOWN_LOC = "Unknown location";
    private static final int MAX_LOG_LENGTH = 2000;

    @Pointcut(
            "within(toolyverse.io.toolyverse..*) && " +
                    "(within(@org.springframework.stereotype.Component *) || " +
                    " within(@org.springframework.stereotype.Service *) || " +
                    " within(@org.springframework.stereotype.Repository *) || " +
                    " within(@org.springframework.stereotype.Controller *)) " +
                    " && !@within(toolyverse.io.toolyverse.infrastructure.config.aspect.ExcludeFromAspect)"
    )
    public void applicationPackagePointcut() {
    }

    @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint jp, Throwable e) {
        var logger = LoggerFactory.getLogger(jp.getSignature().getDeclaringTypeName());
        var stackElem = findAppElement(e.getStackTrace());
        var clickableLoc = stackElem.map(this::buildClickableLocation).orElse(UNKNOWN_LOC);

        logger.error(
                "Exception in {} - Root cause: '{}'",
                clickableLoc,  // e.g. com.example.MyClass.myMethod(MyClass.java:123)
                getRootCauseMessage(e)
        );

        if (logger.isDebugEnabled() && shouldLogStackTrace(e)) {
            logger.debug("Full stack trace:", e);
        }
    }

    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        var logger = LoggerFactory.getLogger(pjp.getSignature().getDeclaringTypeName());
        long startTime = System.nanoTime();

        if (logger.isDebugEnabled()) {
            var stackElem = findAppElement(Thread.currentThread().getStackTrace());
            var clickableLoc = stackElem.map(this::buildClickableLocation).orElse(UNKNOWN_LOC);

            logger.debug(
                    "Enter: {} with args: {}",
                    clickableLoc,
                    formatArgs(pjp)
            );
        }

        var result = pjp.proceed();

        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;

        if (logger.isDebugEnabled()) {
            logger.debug(
                    "Exit: {} with result: {} (took {} ms)",
                    pjp.getSignature().getName(),
                    formatResult(result),
                    String.format("%.1f", executionTime / 1_000_000.0)
            );
        }
        return result;
    }

    /**
     * Limits loggable exception stack traces to those that don't explicitly opt out (via isLogStackTrace()).
     */
    private boolean shouldLogStackTrace(Throwable e) {
        if (e instanceof BusinessException cex) {
            return false;
        }
        // Fallback if the exception class has isLogStackTrace() but doesn't implement those interfaces
        try {
            Method method = e.getClass().getMethod("isLogStackTrace");
            return !(boolean) method.invoke(e);
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Finds the first stack-trace element that belongs to our BASE_PACKAGE.
     */
    private Optional<StackTraceElement> findAppElement(StackTraceElement[] stack) {
        return Arrays.stream(stack)
                .filter(el -> el.getClassName().startsWith(BASE_PACKAGE))
                .filter(el -> !el.getClassName().contains("exception.helper"))
                .findFirst();
    }

    /**
     * Convert StackTraceElement to standard "java stack trace" format:
     * fully.qualified.ClassName.methodName(FileName.java:NN)
     */
    private String buildClickableLocation(StackTraceElement ste) {
        return ste.getClassName() + "." + ste.getMethodName() +
                "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")";
    }


    private String formatResult(Object result) {
        return result == null ? "null" : result.getClass().getSimpleName() + " instance";
    }

    /**
     * Displays method arguments, but caps total length at 1000 characters.
     */
    private String formatArgs(JoinPoint jp) {
        if (ArrayUtils.isEmpty(jp.getArgs())) {
            return "[]";
        }
        if (jp.getSignature() instanceof MethodSignature ms) {
            var names = ms.getParameterNames();
            if (names != null && names.length == jp.getArgs().length) {
                // Build "paramName=paramValue" for each arg
                String joinedArgs = IntStream.range(0, names.length)
                        .mapToObj(i -> names[i] + "=" + jp.getArgs()[i])
                        .collect(Collectors.joining(", ", "[", "]"));

                // Truncate if it exceeds MAX_LOG_LENGTH
                if (joinedArgs.length() > MAX_LOG_LENGTH) {
                    joinedArgs = joinedArgs.substring(0, MAX_LOG_LENGTH) + "...(truncated)";
                }
                return joinedArgs;
            }
        }
        // Fallback: just show arg count if we can't match param names
        return "[" + jp.getArgs().length + " args]";
    }
}
