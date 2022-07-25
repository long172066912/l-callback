package com.wb.base.callback.service.backpressure.statistics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: BackpressureStatisticsStatement
 * @Description: //TODO (用一句话描述该文件做什么)
 * @date 2022/5/30 10:57 AM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackpressureStatisticsStatement {
    /**
     * 统计规则
     */
    private BackpressureRuleCycle backpressureRuleCycle;
    /**
     * 次数
     */
    private Long count;
    /**
     * 计算结果
     */
    private Double value;
}
