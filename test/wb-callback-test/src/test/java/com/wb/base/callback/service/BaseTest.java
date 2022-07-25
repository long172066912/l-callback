package com.wb.base.callback.service;

import com.wb.base.callback.model.CallBackType;
import com.wb.base.callback.service.callback.model.CallBackConfig;
import com.wb.base.callback.service.callback.model.CallBackMessage;
import com.wb.base.callback.service.model.TestMessage;
import com.wb.base.callback.utils.CallBackApolloConfigUtils;
import com.wb.common.WbJSON;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: BaseTest
 * @Description: 测试启动类
 * @date 2022/6/1 10:35 AM
 */
@SpringBootTest
public class BaseTest {

    @BeforeAll
    public static void init() {
    }

    @Test
    public void testMockStatic() {
        try (MockedStatic<CallBackApolloConfigUtils> callBackApolloConfigUtilsMockedStatic = Mockito.mockStatic(CallBackApolloConfigUtils.class)) {
            callBackApolloConfigUtilsMockedStatic.when(CallBackApolloConfigUtils::getRetryRateConfig).thenReturn(getRetryRateConfig());
            CallBackApolloConfigUtils.RetryRateData retryRateConfig = CallBackApolloConfigUtils.getRetryRateConfig();
            System.out.println(WbJSON.toJson(retryRateConfig));
        }
    }

    /**
     * kafka队列
     */
    protected static Queue<CallBackMessage> kafkaInnerQueue = new LinkedBlockingDeque<>();

    /**
     * sdk消费时的kafka队列
     */
    protected static Queue<CallBackMessage> kafkaCallbackQueue = new LinkedBlockingDeque<>();

    /**
     * rocketMq队列
     */
    protected static Queue<CallBackMessage> rocketMqInnerQueue = new DelayQueue();

    /**
     * DB存储的list
     */
    protected static List<CallBackMessage> dbList = new CopyOnWriteArrayList<>();

    /**
     * 获取默认Apollo配置
     * @return
     */
    protected List<CallBackConfig> getDefaultCallBackConfigs() {
        List<CallBackConfig> configs = new ArrayList<>();
        configs.add(CallBackConfig.builder().callBackType(CallBackType.HTTP).route("user").path("").retry(1).build());
        configs.add(CallBackConfig.builder().callBackType(CallBackType.MQ).topic("CALLBACK_MQ_TOPIC_TEST").retry(1).build());
        return configs;
    }

    protected static CallBackApolloConfigUtils.RetryRateData getRetryRateConfig() {
        int[] callBackRetryConfigs = new int[]{30, 60, 120, 300, 600, 1800, 3600, 7200, 18000, 86400};
        CallBackApolloConfigUtils.RetryRateData retryRateData = new CallBackApolloConfigUtils.RetryRateData();
        retryRateData.setArr(callBackRetryConfigs);
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < callBackRetryConfigs.length; i++) {
            map.put(callBackRetryConfigs[i], i);
        }
        retryRateData.setMap(map);
        return retryRateData;
    }

    protected TestMessage getTestMessage(){
        return new TestMessage(true,"testMessage");
    }
}
