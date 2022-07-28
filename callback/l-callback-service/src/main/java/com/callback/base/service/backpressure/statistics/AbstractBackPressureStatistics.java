package com.callback.base.service.backpressure.statistics;

/**
* @Title: AbstractBackPressureStatistics
* @Description: //TODO (用一句话描述该文件做什么)
* @author JerryLong
* @date 2022/5/30 10:53 AM
* @version V1.0
*/
public abstract class AbstractBackPressureStatistics implements BackPressureStatistics {

    private BaseStatisticsRuler ruler;

    AbstractBackPressureStatistics(BaseStatisticsRuler ruler){
        this.ruler = ruler;
    }

    /**
     * 获取规则
     * @return
     */
    protected BaseStatisticsRuler getStatisticsRuler() {
        return ruler;
    }
}
