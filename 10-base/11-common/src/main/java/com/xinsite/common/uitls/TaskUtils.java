package com.xinsite.common.uitls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 异步任务管理器
 */
public class TaskUtils {
    private static final Logger logger = LoggerFactory.getLogger(TaskUtils.class);

    /**
     * 当前对象实例
     */
    private static TaskUtils task = null;

    /**
     * 操作延迟10毫秒
     */
    private final int OPERATE_DELAY_TIME = 10;

    /**
     * 异步操作任务调度线程池
     */
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /**
     * 获取当前对象实例 多线程安全单例模式(使用双重同步锁)
     */
    public static synchronized TaskUtils getInstance() {
        if (task == null) {
            synchronized (TaskUtils.class) {
                if (task == null) task = new TaskUtils();
            }
        }
        return task;
    }


    /**
     * 执行任务
     *
     * @param task 任务
     */
    public void execute(TimerTask task) {
        executor.schedule(task, OPERATE_DELAY_TIME, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) {
        TaskUtils.getInstance().execute(new TimerTask() {
            @Override
            public void run() {

            }
        });
    }

    /**
     * 停止任务线程池
     */
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(120, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    if (!executor.awaitTermination(120, TimeUnit.SECONDS)) {
                        logger.info("Pool did not terminate");
                    }
                }
            } catch (InterruptedException ie) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
