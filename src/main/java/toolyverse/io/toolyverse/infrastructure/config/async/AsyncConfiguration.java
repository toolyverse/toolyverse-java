package toolyverse.io.toolyverse.infrastructure.config.async;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfiguration implements AsyncConfigurer {

    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return asyncExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }

    @Slf4j
    private static class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

        private static final int MAX_PARAM_LENGTH = 200;

        @Override
        public void handleUncaughtException(@NonNull Throwable throwable, @NonNull Method method, @NonNull Object... params) {
            log.error("Exception in async method '{}': {}",
                    method.getName(),
                    ExceptionUtils.getRootCauseMessage(throwable),
                    throwable);

            logMethodParameters(method, params);
        }

        private void logMethodParameters(Method method, Object[] params) {
            if (params == null || params.length == 0) {
                log.debug("Method '{}' called with no parameters", method.getName());
                return;
            }

            try {
                String paramString = Arrays.stream(params)
                        .map(this::safeToString)
                        .collect(Collectors.joining(", "));

                log.error("Method '{}' parameters: [{}]", method.getName(), paramString);

            } catch (Exception e) {
                log.warn("Failed to log parameters for method '{}': {}",
                        method.getName(),
                        e.getMessage());
            }
        }

        private String safeToString(Object obj) {
            if (obj == null) {
                return "null";
            }

            try {
                String str = obj.toString();
                if (str.length() > MAX_PARAM_LENGTH) {
                    return str.substring(0, MAX_PARAM_LENGTH) + "... (truncated)";
                }
                return str;
            } catch (Exception e) {
                return obj.getClass().getSimpleName() + " (toString failed)";
            }
        }
    }
}