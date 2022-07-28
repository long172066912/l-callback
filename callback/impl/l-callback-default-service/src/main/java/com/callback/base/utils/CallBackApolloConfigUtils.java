package com.callback.base.utils;

import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.service.callback.model.CallBackConfig;
import com.l.rpc.json.LJSON;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallBackApolloConfigUtils
 * @Description: //TODO (用一句话描述该文件做什么)
 * @date 2022/5/27 4:38 PM
 */
public class CallBackApolloConfigUtils {

    /**
     * 根据业务与平台获取回调配置
     *
     * @param platformTypeEnums
     * @param businessTypeEnums
     * @return
     */
    public static List<CallBackConfig> getCallBackConfig(CallBackPlatformTypeEnums platformTypeEnums, String businessTypeEnums) {
        //TODO 使用自己的配置中心实现
        return null;
    }

    /**
     * 获取重试速率配置
     *
     * @return
     */
    public static RetryRateData getRetryRateConfig() {
        if (null != retryRateData) {
            return retryRateData;
        }
        synchronized (CallBackApolloConfigUtils.class) {
            if (null != retryRateData) {
                return retryRateData;
            }
            int[] callBackRetryConfigs = DEFAULT_RETRY_RATE_CONFIG;
            retryRateData = new RetryRateData();
            retryRateData.setArr(callBackRetryConfigs);
            Map<Integer, Integer> map = new HashMap<>(16);
            for (int i = 0; i < callBackRetryConfigs.length; i++) {
                map.put(callBackRetryConfigs[i], i);
            }
            retryRateData.setMap(map);
        }
        return retryRateData;
    }

    private static final int[] DEFAULT_RETRY_RATE_CONFIG = new int[]{30, 60, 120, 300, 600, 1800, 3600, 7200, 18000, 86400};

    @Data
    private static class RetryRateConfig {
        /**
         * 重试间隔
         */
        private int[] retryInterval;
    }

    private static RetryRateData retryRateData = null;

    @Data
    public static class RetryRateData {
        /**
         * map
         */
        private Map<Integer, Integer> map;
        /**
         * arr
         */
        private int[] arr;
    }

    public static void main(String[] args) {
        System.out.println(LJSON.toJson(getCallBackConfig(CallBackPlatformTypeEnums.PLATFORM, "DEFAULT")));
        System.out.println(LJSON.toJson(getCallBackConfig(CallBackPlatformTypeEnums.TEST, "TEST_HTTP")));
    }
}
