package com.callback.base.constants;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: MqTopicEnums
 * @Description: MQ队列管理
 * @date 2022/5/26 6:53 PM
 */
public enum MqTopicEnums {
    /**
     * kafka，快队列
     */
    KAFKA_FAST_CALLBACK("callback_message_consumer", "callback_fast"),
    /**
     * kafka，慢队列
     */
    KAFKA_SLOW_CALLBACK("callback_message_consumer", "callback_slow"),
    /**
     * 重试
     */
    CALL_BACK_RETRY("callback_message_retry", "callback_retry")
    ;

    MqTopicEnums(String groupName, String topic) {
        this.groupName = groupName;
        this.topic = topic;
    }

    /**
     * 消费group
     */
    private String groupName;
    /**
     * 消费topic
     */
    private String topic;

    public String getGroupName() {
        return groupName;
    }

    public String getTopic() {
        return topic;
    }
}
