package com.callback.base.service.backpressure.report;

import java.util.function.Supplier;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: HandleTimerReporter
 * @Description: 处理时间收集上报
 * @date 2022/5/26 4:12 PM
 */
public interface HandleReporter {

    /**
     * 时间统计上报
     * @param configKey
     * @param record
     * @param <T>
     * @return
     */
    <T> T timer(String configKey, Supplier<T> record);
}
