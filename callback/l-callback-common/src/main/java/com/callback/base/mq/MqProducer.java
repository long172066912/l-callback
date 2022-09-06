package com.callback.base.mq;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
* @Title: MqProducer
* @Description: MQ处理接口
* @author JerryLong
* @date 2022/9/6 17:03
* @version V1.0
*/
public interface MqProducer {
    /**
     * 发消息
     * @param topic
     * @param msg
     * @return
     */
    boolean send(String topic, String msg);

    /**
     * 发送延迟消息
     * @param topic
     * @param msg
     * @param delayTime
     * @param timeUnit
     * @return
     */
    boolean send(String topic, String msg, Long delayTime, TimeUnit timeUnit);
}
