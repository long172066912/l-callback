package com.callback.base.service.backpressure.rule;

import com.callback.base.service.backpressure.statistics.BaseStatisticsRuler;
import com.callback.base.service.backpressure.statistics.DefaultBackPressureStatistics;
import com.callback.base.service.backpressure.statistics.model.BackpressureRuleCycle;
import com.callback.base.service.backpressure.statistics.model.StatisticsCalculateType;

import java.util.concurrent.TimeUnit;

/**
* @Title: SecendAvgStatisticsRuler
* @Description: 测试2秒计算周期
* @author JerryLong
* @date 2022/5/30 10:45 AM
* @version V1.0
*/
public class TestSecendAvgStatisticsRuler implements BaseStatisticsRuler {

    private TestSecendAvgStatisticsRuler(){}

    public static DefaultBackPressureStatistics getInstanceStatistics(){
        return Single.getStatistics();
    }

    private static class Single {
        private static DefaultBackPressureStatistics statistics = new DefaultBackPressureStatistics(new TestSecendAvgStatisticsRuler());
        public static DefaultBackPressureStatistics getStatistics(){
            return statistics;
        }
    }

    @Override
    public BackpressureRuleCycle buildRuleCycle() {
        return BackpressureRuleCycle.builder().time(2).unit(TimeUnit.SECONDS).calculateType(StatisticsCalculateType.AVG).build();
    }
}
