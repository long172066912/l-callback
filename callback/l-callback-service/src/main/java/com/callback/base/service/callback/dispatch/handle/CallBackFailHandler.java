package com.callback.base.service.callback.dispatch.handle;

import com.callback.base.service.callback.model.CallBackMessage;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallBackFailHandler
 * @Description: 回调失败处理器
 * @date 2022/5/26 3:22 PM
 */
public interface CallBackFailHandler {

    /**
     * 回调失败处理
     *
     * @param callBackMessage
     */
    void handle(CallBackMessage callBackMessage);
}
