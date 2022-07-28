package com.callback.base.extend.backpressure;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.callback.base.service.backpressure.constants.BackPressureEnums;
import com.callback.base.service.backpressure.logic.AbstractBackPressureLogic;
import com.callback.base.service.backpressure.statistics.BackPressureStatistics;
import com.callback.base.service.backpressure.statistics.rule.SecendAvgStatisticsRuler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: BackPressureLogicImpl
 * @Description: 背压逻辑实现
 * @date 2022/5/26 6:29 PM
 */
@Service
@Slf4j
public class BackPressureLogicImpl extends AbstractBackPressureLogic {

    public BackPressureLogicImpl() {
        super(SecendAvgStatisticsRuler.getInstanceStatistics());
    }

    public BackPressureLogicImpl(BackPressureStatistics statistics){
        super(statistics);
    }

    /**
     * 本地缓存，有效期60秒
     */
    public static Cache<String, BackPressureEnums> localCache = Caffeine.newBuilder()
            .initialCapacity(10)
            .maximumSize(2000)
            //在写入后开始计时，在指定的时间后过期。
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();

    /**
     * 获取背压状态
     * 先从本地缓存（10）秒查询结果
     * 如果没查到则从从计算结果中查询，redis查询(去掉redis，直接走本地方式，依赖分布式的负载均衡)，
     * @param configKey
     * @return
     */
    @Override
    public BackPressureEnums getBackPressure(String configKey) {
        return localCache.get(configKey, e -> super.getDefaultAvgCalculateValue(configKey));
    }
}
