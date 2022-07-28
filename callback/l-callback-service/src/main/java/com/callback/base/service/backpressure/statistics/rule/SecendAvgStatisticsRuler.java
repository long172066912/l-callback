package com.callback.base.service.backpressure.statistics.rule;

import com.callback.base.service.backpressure.statistics.BaseStatisticsRuler;
import com.callback.base.service.backpressure.statistics.DefaultBackPressureStatistics;
import com.callback.base.service.backpressure.statistics.model.BackpressureRuleCycle;
import com.callback.base.service.backpressure.statistics.model.StatisticsCalculateType;

import java.util.concurrent.TimeUnit;

/**
* @Title: SecendAvgStatisticsRuler
* @Description: 60秒计算平均值
* @author JerryLong
* @date 2022/5/30 10:45 AM
* @version V1.0
*/
public class SecendAvgStatisticsRuler implements BaseStatisticsRuler {

    private SecendAvgStatisticsRuler(){}

    public static DefaultBackPressureStatistics getInstanceStatistics(){
        return Single.getStatistics();
    }

    private static class Single {
        private static DefaultBackPressureStatistics statistics = new DefaultBackPressureStatistics(new SecendAvgStatisticsRuler());
        public static DefaultBackPressureStatistics getStatistics(){
            return statistics;
        }
    }

    @Override
    public BackpressureRuleCycle buildRuleCycle() {
        return BackpressureRuleCycle.builder().time(60).unit(TimeUnit.SECONDS).calculateType(StatisticsCalculateType.AVG).build();
    }
}
