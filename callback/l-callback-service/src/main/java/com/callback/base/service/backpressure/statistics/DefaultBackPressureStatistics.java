package com.callback.base.service.backpressure.statistics;

import com.callback.base.service.backpressure.statistics.model.BackpressureRuleCycle;
import com.callback.base.service.backpressure.statistics.model.BackpressureStatisticsStatement;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.Scheduler;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: DefaultBackPressureStatistics
 * @Description: 默认统计实现
 * @date 2022/5/30 10:50 AM
 */
public class DefaultBackPressureStatistics extends AbstractBackPressureStatistics {

    private BackPressureStatisticsWrapper statisticsWrapper;

    public DefaultBackPressureStatistics(BaseStatisticsRuler ruler) {
        super(ruler);
        statisticsWrapper = new BackPressureStatisticsWrapper(new BackPressureStatisticsStore(ruler.buildRuleCycle()));
    }

    @Override
    public void calculate(String key, int count, long timer, TimeUnit unit) {
        //时间单位转为毫秒
        statisticsWrapper.increment(key, count, unit.toMillis(timer));
    }

    @Override
    public BackpressureStatisticsStatement getValue(String key) {
        BackpressureStatisticsStatement avgValue = statisticsWrapper.getAvgValue(key);
        avgValue.setBackpressureRuleCycle(super.getStatisticsRuler().buildRuleCycle());
        return avgValue;
    }

    /**
     * @author JerryLong
     * @version V1.0
     * @Title: DefaultBackPressureStatistics
     * @Description: 统计包装，对key根据rule进行处理
     * @date 2022/5/30 10:51 AM
     */
    private static class BackPressureStatisticsWrapper {

        private BackPressureStatisticsStore statisticsStore;

        BackPressureStatisticsWrapper(BackPressureStatisticsStore statisticsStore) {
            this.statisticsStore = statisticsStore;
        }

        public void increment(String key, int count, double time) {
            statisticsStore.increment(key, count, time);
        }

        public BackpressureStatisticsStatement getAvgValue(String key) {
            return Optional.of(statisticsStore.getStatisticsSumData(key)).map(data ->
                    BackpressureStatisticsStatement.builder().count(data.count.get()).value(data.time.get() / data.count.get()).build()
            ).get();
        }
    }

    /**
     * @author JerryLong
     * @version V1.0
     * @Title: DefaultBackPressureStatistics
     * @Description: 统计存储
     * @date 2022/5/30 10:51 AM
     */
    private static class BackPressureStatisticsStore {

        private BackpressureRuleCycle ruleCycle;

        BackPressureStatisticsStore(BackpressureRuleCycle ruleCycle) {
            this.ruleCycle = ruleCycle;
            localCache = Caffeine.newBuilder()
                    .initialCapacity(2)
                    .maximumSize(10)
                    //在写入后开始计时，在指定的时间后过期。
                    .expireAfterWrite(ruleCycle.getTime(), ruleCycle.getUnit())
                    .scheduler(Scheduler.forScheduledExecutorService(Executors.newScheduledThreadPool(1)))
                    .removalListener((String k, Map<String, StatisticsSumData> v, RemovalCause cause) -> {
                        this.snapshootaDatas = v;
                    })
                    .build();
        }

        /**
         * 本地缓存
         */
        private Cache<String, Map<String, StatisticsSumData>> localCache;
        /**
         * 快照
         */
        private Map<String, StatisticsSumData> snapshootaDatas;

        private static final String CACHE_KEY = "STATISTICS_LOCAL_KEY";

        /**
         * 自增，从本地缓存中
         *
         * @param key
         * @param count
         * @param time
         */
        public void increment(String key, int count, double time) {
            Map<String, StatisticsSumData> cacheDatas = getDatas();
            StatisticsSumData statisticsSumData = cacheDatas.computeIfAbsent(key, e -> this.getDefaultData());
            statisticsSumData.count.addAndGet(count);
            statisticsSumData.time.addAndGet(time);
        }

        public StatisticsSumData getStatisticsSumData(String key) {
            return Optional.ofNullable(snapshootaDatas).map(e -> e.getOrDefault(key, this.getDefaultData())).orElse(this.getDefaultData());
        }

        private Map<String, StatisticsSumData> getDatas() {
            return localCache.get(CACHE_KEY, e -> new ConcurrentHashMap<>(8));
        }

        private StatisticsSumData getDefaultData() {
            return new StatisticsSumData(new AtomicLong(0), new AtomicDouble(0));
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class StatisticsSumData {
        /**
         * 总次数
         */
        private AtomicLong count;
        /**
         * 总时间
         */
        private AtomicDouble time;
    }
}
