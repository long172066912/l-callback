package com.callback.base.worker.utils;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: AsyncExecutorUtils
 * @Description: 线程池帮助类，内部提供单例线程池
 * @date 2020/11/12 12:09 PM
 */
@Slf4j
public class CallBackAsyncExecutorUtils {

    /**
     * @param
     * @author JerryLong
     * @Title:
     * @Description: SingleScheduledExecutorService
     * @return 返回类型
     * @throws
     * @date 2020/12/7 10:30 AM
     */
    private static class SingleScheduledExecutorService {
        public static ScheduledExecutorService scheduledExecutorService = ExecutorServiceMetrics.monitor(
                Metrics.globalRegistry, new ScheduledThreadPoolExecutor(2, (r) -> new Thread(r, "CallBack-AsyncExecutorUtils-Scheduled-Thread"))
                ,"callback_scheduledExecutorService");
    }

    /**
     * @param
     * @return 返回类型
     * @throws
     * @Title:
     * @Description: 获取单例定时任务线程池
     * @author JerryLong
     * @date 2020/12/7 10:31 AM
     */
    public static ScheduledExecutorService getScheduledExecutorService() {
        return SingleScheduledExecutorService.scheduledExecutorService;
    }

    /**
     * @param
     * @return 返回类型
     * @throws
     * @Title: submitScheduledTask
     * @Description: 提交异步定时任务
     * @author JerryLong
     * @date 2020/11/16 5:26 PM
     */
    public static void submitScheduledTask(Runnable task, long initialDelay, long delay, TimeUnit unit) {
        getScheduledExecutorService().scheduleAtFixedRate(() -> {
            try {
                task.run();
            } catch (Exception e) {
                log.error("CallBack AsyncExecutorUtils ScheduledTask error !", e);
            }
        }, initialDelay, delay, unit);
    }
}