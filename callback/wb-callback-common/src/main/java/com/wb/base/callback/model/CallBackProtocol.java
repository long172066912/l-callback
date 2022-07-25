package com.wb.base.callback.model;

import com.wb.base.callback.constants.CallBackPlatformTypeEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @Title: CallBackMessageDTO
* @Description: 回调协议
* @author JerryLong
* @date 2022/5/25 4:27 PM
* @version V1.0
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallBackProtocol {
    /**
     * 回调平台
     */
    private CallBackPlatformTypeEnums platformType;
    /**
     * 回调业务
     */
    private String businessType;
    /**
     * 消息唯一key
     */
    private String messageId;
}
