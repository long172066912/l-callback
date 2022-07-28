package com.callback.base.rpc.client.model;

import com.callback.base.model.CallBackProtocol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
* @Title: CallBackMessageDTO
* @Description: 回调消息实体
* @author JerryLong
* @date 2022/5/25 4:27 PM
* @version V1.0
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallBackMessageDTO {
    /**
     * 回调协议
     */
    @NotNull
    private CallBackProtocol protocol;
    /**
     * 回调内容
     */
    @NotNull
    private Object data;
}
