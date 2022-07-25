package com.wb.base.callback.sdk.properties;

import com.wb.base.callback.constants.CallBackPlatformTypeEnums;
import com.google.common.collect.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallBackProperties
 * @Description: 回调配置
 * @date 2022/5/31 11:02 AM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallBackProperties {
    /**
     * 如果配置了callBackType为MQ方式，还需要配置topic地址，未配置则统一走 CallBackConstants.DEFAULT_TOPIC_PREFIX + protocol.getPlatformType().name() + "_" + protocol.getBusinessType().name()
     */
    private Table<CallBackPlatformTypeEnums, String, String> httpUrls;
    /**
     * 如果配置了callBackType为MQ方式，还需要配置topic地址，未配置则统一走 CallBackConstants.DEFAULT_TOPIC_PREFIX + protocol.getPlatformType().name() + "_" + protocol.getBusinessType().name()
     */
    private Table<CallBackPlatformTypeEnums, String, ConsumerMqTopic> topics;
}
