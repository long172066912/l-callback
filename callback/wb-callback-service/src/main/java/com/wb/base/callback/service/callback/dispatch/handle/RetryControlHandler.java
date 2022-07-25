package com.wb.base.callback.service.callback.dispatch.handle;

import com.wb.base.callback.service.callback.dispatch.handle.model.CallBackStatus;
import com.wb.base.callback.service.callback.model.CallBackMessage;

/**
* @Title: RetryControlHandler
* @Description: 重试控制处理器
* @author JerryLong
* @date 2022/5/26 3:24 PM
* @version V1.0
*/
public interface RetryControlHandler {

    /**
     * 消息重试
     * @param callBackHandleFunction
     * @param callBackMessage
     * @return
     */
    CallBackStatus retry(CallBackHandleFunction callBackHandleFunction, CallBackMessage callBackMessage);
}
