package com.wb.base.callback.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wb.base.callback.constants.CallBackPlatformTypeEnums;
import com.wb.base.callback.service.callback.model.CallBackConfig;
import com.wb.common.WbJSON;
import com.wb.config.WbConfigs;
import com.wb.log.WbLogger;
import com.wb.log.WbLoggerFactory;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallBackApolloConfigUtils
 * @Description: //TODO (用一句话描述该文件做什么)
 * @date 2022/5/27 4:38 PM
 */
public class CallBackApolloConfigUtils {

    private static WbLogger LOGGER = WbLoggerFactory.getLogger(CallBackApolloConfigUtils.class);

    /**
     * 本地环境变量，为了支持本地调试
     */
    public static final String APOLLO_CONFIGSERVICE = "APOLLO_CONFIGSERVICE";

    private static final boolean IS_K_8_S = System.getenv("ISK8S") != null;

    private static final String CONFIG_PREFIX = "callback-";

    static {
        init();
    }

    /**
     * 配置环境变量
     *
     * @return
     */
    public static void init() {
        if (!IS_K_8_S && StringUtils.isBlank(System.getenv(APOLLO_CONFIGSERVICE))) {
            System.setProperty("apollo.configService", "http://apollo-configservice.wb-dev.com:30002/");
            System.setProperty("apollo.app.id", "callback");
            System.setProperty("app.id", "callback");
        }
        //增加速率监听，只要变更就重置
        WbConfigs.getJson("CallBackRetryConfig").addChangeListener(changeEvent -> retryRateData = null);
    }

    /**
     * 根据业务与平台获取回调配置
     *
     * @param platformTypeEnums
     * @param businessTypeEnums
     * @return
     */
    public static List<CallBackConfig> getCallBackConfig(CallBackPlatformTypeEnums platformTypeEnums, String businessTypeEnums) {
        try {
            return WbConfigs.getProperties(CONFIG_PREFIX + platformTypeEnums.name()).getObj(businessTypeEnums, new TypeReference<List<CallBackConfig>>() {});
        } catch (Exception e) {
            LOGGER.warn("CallBackApolloConfigUtils getCallBackConfig error !", e);
            return null;
        }
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
            int[] callBackRetryConfigs = Optional.ofNullable(WbConfigs.getJson("CallBackRetryConfig").getObj(RetryRateConfig.class)).map(e -> e.getRetryInterval()).orElse(DEFAULT_RETRY_RATE_CONFIG);
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
        System.out.println(WbJSON.toJson(getCallBackConfig(CallBackPlatformTypeEnums.WANBA_PLATFORM, "AUDIT_USER_IMAGE")));
        System.out.println(WbJSON.toJson(getCallBackConfig(CallBackPlatformTypeEnums.TEST, "TEST_HTTP")));
    }
}
