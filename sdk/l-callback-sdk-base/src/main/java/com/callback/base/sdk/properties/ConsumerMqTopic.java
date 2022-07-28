package com.callback.base.sdk.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @Title: KafkaTopic
* @Description: //TODO (用一句话描述该文件做什么)
* @author JerryLong
* @date 2022/6/2 10:39 AM
* @version V1.0
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerMqTopic {
    /**
     * group
     */
    private String group;
    /**
     * topic
     */
    private String topic;
}