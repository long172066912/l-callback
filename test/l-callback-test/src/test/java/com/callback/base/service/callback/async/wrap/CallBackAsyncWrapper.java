package com.callback.base.service.callback.async.wrap;

import com.callback.base.service.backpressure.constants.BackPressureEnums;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallBackAsyncWrapper
 * @Description: 异步包装
 * @date 2022/5/26 5:52 PM
 */
@Component
public class CallBackAsyncWrapper {
    /**
     * 快线程池
     */
    private static ExecutorService fastExecutorService = ExecutorServiceMetrics.monitor(
            Metrics.globalRegistry,
            new ThreadPoolExecutor(20, 100, 30, TimeUnit.SECONDS, new SynchronousQueue<>(), (r) -> new Thread(r, "回调服务处理线程池-快"), new ThreadPoolExecutor.CallerRunsPolicy()),
            "callback_fast_executor_service"
    );

    /**
     * 慢线程池
     */
    private static ExecutorService slowExecutorService = ExecutorServiceMetrics.monitor(
            Metrics.globalRegistry,
            new ThreadPoolExecutor(5, 10, 30, TimeUnit.SECONDS, new SynchronousQueue<>(), (r) -> new Thread(r, "回调服务处理线程池-慢"), new ThreadPoolExecutor.CallerRunsPolicy()),
            "callback_fast_executor_service"
    );

    /**
     * 对任务进行异步包装
     *
     * @param backPressureEnums
     * @param runnable
     */
    public void wrap(BackPressureEnums backPressureEnums, Runnable runnable) {
        switch (backPressureEnums){
            case SLOW:
                slowExecutorService.execute(runnable);
                return;
            case FAST:
            default:
                fastExecutorService.execute(runnable);
                return;
        }
    }
}
