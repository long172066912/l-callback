package com.callback.base.mq;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
* @Title: MqClient
* @Description: MqClient
* @author JerryLong
* @date 2022/9/6 17:07
* @version V1.0
*/
public class MqClient {

    private final MqConsumer mqConsumer;
    private final MqProducer mqProducer;

    public MqClient(){
        this.mqConsumer = new MqConsumer() {

            @Override
            public MqConsumer consume(String group, String topic, MqConsumerHandler mqConsumerHandler) {
                return null;
            }

            @Override
            public void close() {

            }
        };
        this.mqProducer = new MqProducer() {
            @Override
            public boolean send(String topic, String msg) {
                return false;
            }

            @Override
            public boolean send(String topic, String msg, Long delayTime, TimeUnit timeUnit) {
                return true;
            }
        };
    }

    public static MqClient getInstance() {
        return MqClientSingle.getMqClient();
    }

    private static class MqClientSingle {
        private static final MqClient mqClient = new MqClient();
        public static MqClient getMqClient(){
            return mqClient;
        }
    }

    public MqClient(MqConsumer mqConsumer, MqProducer mqProducer){
        this.mqConsumer = mqConsumer;
        this.mqProducer = mqProducer;
    }

    /**
     * 发送消息
     * @param topic
     * @param msg
     * @return
     */
    public static boolean send(String topic, String msg) {
        return getInstance().mqProducer.send(topic, msg);
    }

    /**
     * 发送延迟消息
     * @param topic
     * @param msg
     * @param delayTime
     * @param timeUnit
     * @return
     */
    public static boolean send(String topic, String msg, Long delayTime, TimeUnit timeUnit) {
        return getInstance().mqProducer.send(topic, msg, delayTime, timeUnit);
    }

    /**
     * 消费消息
     * @param group
     * @param topic
     * @param mqConsumerHandler
     * @return
     */
    public static MqConsumer consume(String group, String topic, MqConsumer.MqConsumerHandler mqConsumerHandler) {
        return getInstance().mqConsumer.consume(group, topic, mqConsumerHandler);
    }
}
