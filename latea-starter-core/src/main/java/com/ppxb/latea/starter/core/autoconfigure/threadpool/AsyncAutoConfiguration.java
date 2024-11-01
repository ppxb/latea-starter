package com.ppxb.latea.starter.core.autoconfigure.threadpool;

import cn.hutool.core.util.ArrayUtil;
import com.ppxb.latea.starter.core.constant.PropertiesConstants;
import com.ppxb.latea.starter.core.exception.BaseException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.concurrent.Executor;

@Lazy
@AutoConfiguration
@EnableAsync(proxyTargetClass = true)
@ConditionalOnProperty(prefix = "spring.task.execution.extension", name = PropertiesConstants.ENABLED, havingValue = "true", matchIfMissing = true)
public class AsyncAutoConfiguration implements AsyncConfigurer {

    private static final Logger log = LoggerFactory.getLogger(AsyncAutoConfiguration.class);

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public AsyncAutoConfiguration(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    /**
     * 异步任务线程池配置
     */
    @Override
    public Executor getAsyncExecutor() {
        return threadPoolTaskExecutor;
    }

    /**
     * 异步任务执行时的异常处理
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            log.error("Async method {} execution failed with parameters: {}", method.getName(), ArrayUtil
                .isNotEmpty(objects) ? Arrays.toString(objects) : "none", throwable);

            String errorMessage = String.format("Async execution failed - method: %s, message: %s", method
                .getName(), throwable.getMessage());
            throw new BaseException(errorMessage);
        };
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Latea Starter] - Auto Configuration 'AsyncConfigurer' completed initialization.");
    }
}
