package com.ppxb.latea.starter.core.autoconfigure.threadpool;

import com.ppxb.latea.starter.core.constant.PropertiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.task.ThreadPoolTaskExecutorCustomizer;
import org.springframework.boot.task.ThreadPoolTaskSchedulerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 线程池自动配置
 */
@Lazy
@AutoConfiguration
@EnableConfigurationProperties(ThreadPoolExtensionProperties.class)
public class ThreadPoolAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolAutoConfiguration.class);

    @Value("${spring.task.execution.pool.core-size:#{T(java.lang.Runtime).getRuntime().availableProcessors() + 1}}")
    private int corePoolSize;

    @Value("${spring.task.execution.pool.max-size:#{T(java.lang.Runtime).getRuntime().availableProcessors() * 2}}")
    private int maxPoolSize;

    /**
     * 异步任务线程池配置
     */
    @Bean
    @ConditionalOnProperty(
            prefix = "string.task.execution.extension",
            name = PropertiesConstants.ENABLED,
            havingValue = "true",
            matchIfMissing = true
    )
    public ThreadPoolTaskExecutorCustomizer threadPoolTaskExecutorCustomizer(ThreadPoolExtensionProperties properties) {
        return executor -> {
            // 核心（最小）线程数
            executor.setCorePoolSize(corePoolSize);
            // 最大线程数
            executor.setMaxPoolSize(maxPoolSize);
            executor.setRejectedExecutionHandler(
                    properties.getExecutor()
                            .getRejectedPolicy()
                            .getRejectedExecutionHandler());
            log.debug("[Latea Starter] - Auto Configuration 'TaskExecutor' completed initialization.");
        };
    }

    /**
     * 定时任务线程池配置
     */
    @EnableScheduling
    @ConditionalOnProperty(
            prefix = "spring.task.scheduling.extension",
            name = PropertiesConstants.ENABLED,
            havingValue = "true",
            matchIfMissing = true
    )
    public static class TaskSchedulerConfiguration {

        @Bean
        public ThreadPoolTaskSchedulerCustomizer threadPoolTaskSchedulerCustomizer(ThreadPoolExtensionProperties properties) {
            return executor -> {
                executor.setRejectedExecutionHandler(
                        properties.getScheduler()
                                .getRejectedPolicy()
                                .getRejectedExecutionHandler());
                log.debug("[Latea Starter] - Auto Configuration 'TaskScheduler' completed initialization.");
            };
        }
    }
}
