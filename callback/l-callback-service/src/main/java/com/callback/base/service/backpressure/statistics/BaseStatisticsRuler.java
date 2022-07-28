package com.callback.base.service.backpressure.statistics;

import com.callback.base.service.backpressure.statistics.model.BackpressureRuleCycle;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: StatisticsRuleNotifier
 * @Description: 统计规则
 * @date 2022/5/30 10:26 AM
 */
public interface BaseStatisticsRuler {

    /**
     * 构建周期规则
     *
     * @return
     */
    BackpressureRuleCycle buildRuleCycle();
}
