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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 线程池自动配置类，提供异步任务和定时任务的线程池配置。
 *
 * <p>该配置类提供了两种线程池的配置：
 * <ul>
 *     <li>异步任务线程池 - 用于处理 @Async 注解标记的异步方法</li>
 *     <li>定时任务线程池 - 用于处理 @Scheduled 注解标记的定时任务</li>
 * </ul>
 *
 * <p>配置示例：
 * <blockquote><pre>
 * spring:
 *   task:
 *     execution:
 *       pool:
 *         core-size: 8
 *         max-size: 16
 *       extension:
 *         enabled: true
 *         rejected-policy: CALLER_RUNS
 *     scheduling:
 *       extension:
 *         enabled: true
 *         rejected-policy: DISCARD
 * </pre></blockquote>
 *
 * @author ppxb
 * @see org.springframework.scheduling.annotation.EnableAsync
 * @see org.springframework.scheduling.annotation.EnableScheduling
 * @see org.springframework.boot.task.ThreadPoolTaskExecutorCustomizer
 * @see org.springframework.boot.task.ThreadPoolTaskSchedulerCustomizer
 * @since 1.0.0
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
            executor.setCorePoolSize(corePoolSize);
            executor.setMaxPoolSize(maxPoolSize);
            executor.setRejectedExecutionHandler(
                    properties.getExecution()
                            .getRejectedPolicy()
                            .getRejectedExecutionHandler());
            log.debug("[Latea Starter] - Auto Configuration 'TaskExecutor' completed initialization.");
        };
    }

    /**
     * 定时任务线程池配置类，用于自定义 Spring 的 TaskScheduler 配置。
     *
     * <p>该配置类主要提供以下功能：
     * <ul>
     *     <li>配置定时任务线程池的拒绝策略</li>
     *     <li>自定义定时任务线程池的行为</li>
     * </ul>
     *
     * <p>配置示例：
     * <blockquote><pre>
     * spring:
     *   task:
     *     scheduling:
     *       extension:
     *         enabled: true
     *         rejected-policy: DISCARD
     * </pre></blockquote>
     *
     * <p>当 <code>spring.task.scheduling.extension.enabled=true</code> 时（默认为true），该配置生效。
     *
     * @author ppxb
     * @see org.springframework.scheduling.annotation.EnableScheduling
     * @see org.springframework.boot.task.ThreadPoolTaskSchedulerCustomizer
     * @see ThreadPoolExecutorRejectedPolicy
     * @since 1.0.0
     */
    @EnableScheduling
    @Configuration(proxyBeanMethods = false)// FIXME: 可能会出现问题
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
                        properties.getScheduling()
                                .getRejectedPolicy()
                                .getRejectedExecutionHandler());
                log.debug("[Latea Starter] - Auto Configuration 'TaskScheduler' completed initialization.");
            };
        }
    }
}
