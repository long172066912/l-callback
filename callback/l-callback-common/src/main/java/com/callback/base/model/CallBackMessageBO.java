package com.callback.base.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by @author 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CallBackMessageBO {
    /**
     * 回调协议
     */
    private CallBackProtocol protocol;
    /**
     * 回调内容
     */
    private Object data;
}
