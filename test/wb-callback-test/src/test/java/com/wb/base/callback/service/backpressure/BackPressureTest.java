package com.wb.base.callback.service.backpressure;

import com.wb.base.callback.constants.CallBackPlatformTypeEnums;
import com.wb.base.callback.extend.backpressure.BackPressureLogicImpl;
import com.wb.base.callback.service.BaseTest;
import com.wb.base.callback.service.backpressure.constants.BackPressureEnums;
import com.wb.base.callback.service.backpressure.logic.BackPressureLogic;
import com.wb.base.callback.service.backpressure.report.HandleReporter;
import com.wb.base.callback.service.backpressure.report.impl.HandleReporterImpl;
import com.wb.base.callback.service.backpressure.rule.TestSecendAvgStatisticsRuler;
import com.wb.base.callback.service.backpressure.statistics.DefaultBackPressureStatistics;
import com.wb.base.callback.service.backpressure.statistics.model.BackpressureStatisticsStatement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class BackPressureTest extends BaseTest {

    private HandleReporter handleReporter = new HandleReporterImpl(TestSecendAvgStatisticsRuler.getInstanceStatistics());

    private BackPressureLogic backPressureLogic = new BackPressureLogicImpl(TestSecendAvgStatisticsRuler.getInstanceStatistics());

    private DefaultBackPressureStatistics statistics = TestSecendAvgStatisticsRuler.getInstanceStatistics();

    @Test
    void statisticsTest() {
        String key = CallBackPlatformTypeEnums.WANBA_PLATFORM.name() + "_" + "AUDIT_USER_IMAGE";
        statistics.calculate(key, 1, 10L, TimeUnit.MILLISECONDS);
        statistics.calculate(key, 1, 20L, TimeUnit.MILLISECONDS);
        statistics.calculate(key, 1, 30L, TimeUnit.MILLISECONDS);
        BackpressureStatisticsStatement value = statistics.getValue(key);
        Assertions.assertEquals(value.getCount(), 0);
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        value = statistics.getValue(key);
        Assertions.assertEquals(value.getCount(), 3);
        Assertions.assertEquals(value.getValue(), 20);
    }

    @Test
    void reportFastTest() {
        String key = CallBackPlatformTypeEnums.WANBA_PLATFORM.name() + "_" + "AUDIT_USER_IMAGE";
        handleReporter.timer(key, () -> {
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        });
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(backPressureLogic.getBackPressure(key), BackPressureEnums.FAST);
    }

    @Test
    void reportSlowTest() {
        String key = CallBackPlatformTypeEnums.WANBA_PLATFORM.name() + "_" + "AUDIT_USER_IMAGE";
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                handleReporter.timer(key, () -> {
                    try {
                        Thread.sleep(60L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return 1;
                });
            }).start();
        }
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(backPressureLogic.getBackPressure(key), BackPressureEnums.SLOW);
    }
}
