package com.ppxb.latea.starter.core.autoconfigure.threadpool;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.task")
public class ThreadPoolExtensionProperties {

    /**
     * 异步任务扩展配置属性
     */
    private ExecutorExtensionProperties execution = new ExecutorExtensionProperties();

    /**
     * 调度任务扩展配置属性
     */
    private SchedulerExtensionProperties scheduling = new SchedulerExtensionProperties();

    public ExecutorExtensionProperties getExecution() {
        return execution;
    }

    public void setExecution(ExecutorExtensionProperties execution) {
        this.execution = execution;
    }

    public SchedulerExtensionProperties getScheduling() {
        return scheduling;
    }

    public void setScheduling(SchedulerExtensionProperties scheduling) {
        this.scheduling = scheduling;
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
