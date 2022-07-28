package com.callback.base.service.callback.model;

import com.callback.base.constants.CallBackPlatformTypeEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @Title: CallBackMessage
* @Description: 回调处理消息结构体
* @author JerryLong
* @date 2022/5/27 3:25 PM
* @version V1.0
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CallBackMessage {
    /**
     * 回调配置
     */
    private CallBackConfig callBackConfig;
    /**
     * 回调平台
     */
    private CallBackPlatformTypeEnums platformType;
    /**
     * 回调业务
     */
    private String businessType;
    /**
     * 消息ID
     */
    private String messageId;
    /**
     * 重试时间档次
     */
    private Integer retryTime;
    /**
     * 回调消息
     */
    private Object message;
}
