package com.wb.base.callback.service.callback.async.wrap;

import com.wb.base.callback.service.backpressure.constants.BackPressureEnums;
import com.wb.base.component.monitor.client.WbMonitor;
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
    private static ExecutorService fastExecutorService = WbMonitor.getMonitorExecutorService(
            "callback_fast_executor_service",
            new ThreadPoolExecutor(20, 100, 30, TimeUnit.SECONDS, new SynchronousQueue<>(), (r) -> new Thread(r, "回调服务处理线程池-快"), new ThreadPoolExecutor.CallerRunsPolicy())
    );

    /**
     * 慢线程池
     */
    private static ExecutorService slowExecutorService = WbMonitor.getMonitorExecutorService(
            "callback_fast_executor_service",
            new ThreadPoolExecutor(5, 10, 30, TimeUnit.SECONDS, new SynchronousQueue<>(), (r) -> new Thread(r, "回调服务处理线程池-慢"), new ThreadPoolExecutor.CallerRunsPolicy())
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
