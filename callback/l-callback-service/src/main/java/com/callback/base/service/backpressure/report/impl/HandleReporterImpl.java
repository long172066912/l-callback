package com.callback.base.service.backpressure.report.impl;

import com.callback.base.service.backpressure.report.HandleReporter;
import com.callback.base.service.backpressure.statistics.BackPressureStatistics;
import com.callback.base.service.backpressure.statistics.rule.SecendAvgStatisticsRuler;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: HandleReporterImpl
 * @Description: //TODO (用一句话描述该文件做什么)
 * @date 2022/5/26 5:29 PM
 */
@Service
public class HandleReporterImpl implements HandleReporter {

    public HandleReporterImpl(){
        this.pressureStatistics = SecendAvgStatisticsRuler.getInstanceStatistics();
    }

    public HandleReporterImpl(BackPressureStatistics statistics){
        this.pressureStatistics = statistics;
    }

    private BackPressureStatistics pressureStatistics;

    @Override
    public <T> T timer(String configKey, Supplier<T> record) {
        long start = System.currentTimeMillis();
        try {
            return record.get();
        } finally {
            long time = System.currentTimeMillis() - start;
            pressureStatistics.calculate(configKey, 1, time, TimeUnit.MILLISECONDS);
        }
    }
}
