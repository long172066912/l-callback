package com.wb.base.callback.service.backpressure.statistics;

import com.wb.base.callback.service.backpressure.statistics.model.BackpressureStatisticsStatement;

import java.util.concurrent.TimeUnit;

/**
* @Title: BackPressureStatistics
* @Description: 背压统计接口
* @author JerryLong
* @date 2022/5/30 10:18 AM
* @version V1.0
*/
public interface BackPressureStatistics {

    /**
     * 计算
     * @param key 唯一key
     * @param count 次数
     * @param timer 时间
     * @param unit 时间单位
     */
    void calculate(String key, int count, long timer, TimeUnit unit);

    /**
     * 获取结果
     * @param key
     * @return
     */
    BackpressureStatisticsStatement getValue(String key);
}
