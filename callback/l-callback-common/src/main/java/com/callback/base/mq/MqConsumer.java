package com.callback.base.mq;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
* @Title: MqConsumer
* @Description: MQ消费接口
* @author JerryLong
* @date 2022/9/6 17:05
* @version V1.0
*/
public interface MqConsumer {
    /**
     * 消费
     * @param group
     * @param topic
     * @param mqConsumerHandler
     * @return
     */
    MqConsumer consume(String group, String topic, MqConsumerHandler mqConsumerHandler);
    /**
     * 关闭
     */
    void close();

    /**
     * 消费处理
     */
    @FunctionalInterface
    public static interface MqConsumerHandler {
        void handle(String msg);
    }
}
