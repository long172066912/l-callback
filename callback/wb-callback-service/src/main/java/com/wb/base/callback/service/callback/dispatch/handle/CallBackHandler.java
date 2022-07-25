package com.wb.base.callback.service.callback.dispatch.handle;

import com.wb.base.callback.service.callback.dispatch.handle.model.CallBackStatus;
import com.wb.base.callback.service.callback.model.CallBackMessage;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallBackHandler
 * @Description: 回调处理器
 * @date 2022/5/26 3:24 PM
 */
public interface CallBackHandler {

    /**
     * 执行消息回调
     * @param callBackMessage
     * @return
     */
    CallBackStatus handle(CallBackMessage callBackMessage);
}
