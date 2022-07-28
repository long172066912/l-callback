package com.callback.base.model.consumer;

import com.callback.base.constants.CallBackPlatformTypeEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @Title: CallBackConsumerMessageBO
* @Description: 回调消费
* @author JerryLong
* @date 2022/5/30 4:11 PM
* @version V1.0
*/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CallBackConsumerMessageBO<T> {
    /**
     * 平台类型
     */
    private CallBackPlatformTypeEnums platformType;
    /**
     * 业务场景
     */
    private String businessType;
    /**
     * 消息唯一Key
     */
    private String messageId;
    /**
     * 回调消息内容，通过json序列化
     */
    private T data;
}
