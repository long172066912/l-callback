package com.wb.base.callback.service.backpressure.statistics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
* @Title: BackpressureRuleCycle
* @Description: //TODO (用一句话描述该文件做什么)
* @author JerryLong
* @date 2022/5/30 10:42 AM
* @version V1.0
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackpressureRuleCycle {
    /**
     * 时间
     */
    private Integer time;
    /**
     * 单位
     */
    private TimeUnit unit;
    /**
     * 计算规则
     */
    private StatisticsCalculateType calculateType;
}
