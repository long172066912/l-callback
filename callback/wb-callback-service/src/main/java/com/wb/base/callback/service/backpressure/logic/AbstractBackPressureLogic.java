package com.wb.base.callback.service.backpressure.logic;

import com.wb.base.callback.service.backpressure.constants.BackPressureEnums;
import com.wb.base.callback.service.backpressure.statistics.BackPressureStatistics;
import com.wb.log.WbLogger;
import com.wb.log.WbLoggerFactory;

import java.util.Optional;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: AbstractBackPressureLogic
 * @Description: 对背压操作的抽象，定义默认计算实现
 * @date 2022/5/30 11:02 AM
 */
public abstract class AbstractBackPressureLogic implements BackPressureLogic {

    private static WbLogger LOGGER = WbLoggerFactory.getLogger(BackPressureLogic.class);
    /**
     * 慢的默认标准定义
     */
    private static final double SLOW_AVG_TIME = 50;
    /**
     * 计算至少需要10次调用
     */
    private static final int CALCULATE_MIN_COUNT = 10;

    private BackPressureStatistics statistics;

    public AbstractBackPressureLogic(BackPressureStatistics statistics) {
        this.statistics = statistics;
    }

    /**
     * 获取统计计算结果，先直接计算，超过50ms就认定为慢
     *
     * @param key
     * @return
     */
    protected BackPressureEnums getDefaultAvgCalculateValue(String key) {
        if (null == statistics) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("BackPressureLogic getBackPressureCalculateValue error ! statistics is null ! key : {}", key);
            }
            return BackPressureEnums.FAST;
        }
        return Optional.ofNullable(statistics.getValue(key)).map(statement -> {
            if (statement.getCount() < CALCULATE_MIN_COUNT) {
                return BackPressureEnums.FAST;
            }
            return statement.getValue() < SLOW_AVG_TIME ? BackPressureEnums.FAST : BackPressureEnums.SLOW;
        }).orElse(BackPressureEnums.FAST);
    }
}
