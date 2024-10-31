package com.ppxb.latea.starter.core.autoconfigure.threadpool;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.task")
public class ThreadPoolExtensionProperties {

    /**
     * 异步任务扩展配置属性
     */
    private ExecutorExtensionProperties executor = new ExecutorExtensionProperties();

    /**
     * 调度任务扩展配置属性
     */
    private SchedulerExtensionProperties scheduler = new SchedulerExtensionProperties();

    public ExecutorExtensionProperties getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorExtensionProperties executor) {
        this.executor = executor;
    }

    public SchedulerExtensionProperties getScheduler() {
        return scheduler;
    }

    public void setScheduler(SchedulerExtensionProperties scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * 异步任务扩展配置属性
     */
    public static class ExecutorExtensionProperties {

        /**
         * 拒绝策略
         */
        private ThreadPoolExecutorRejectedPolicy rejectedPolicy = ThreadPoolExecutorRejectedPolicy.CALLER_RUNS;

        public ThreadPoolExecutorRejectedPolicy getRejectedPolicy() {
            return rejectedPolicy;
        }

        public void setRejectedPolicy(ThreadPoolExecutorRejectedPolicy rejectedPolicy) {
            this.rejectedPolicy = rejectedPolicy;
        }
    }

    /**
     * 调度任务扩展配置属性
     */
    public static class SchedulerExtensionProperties {

        /**
         * 拒绝策略
         */
        private ThreadPoolExecutorRejectedPolicy rejectedPolicy = ThreadPoolExecutorRejectedPolicy.CALLER_RUNS;

        public ThreadPoolExecutorRejectedPolicy getRejectedPolicy() {
            return rejectedPolicy;
        }

        public void setRejectedPolicy(ThreadPoolExecutorRejectedPolicy rejectedPolicy) {
            this.rejectedPolicy = rejectedPolicy;
        }
    }
}
