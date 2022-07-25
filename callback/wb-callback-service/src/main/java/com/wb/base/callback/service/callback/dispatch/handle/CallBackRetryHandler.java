package com.wb.base.callback.service.callback.dispatch.handle;

import com.wb.base.callback.service.callback.dispatch.handle.model.CallBackStatus;
import com.wb.base.callback.service.callback.model.CallBackMessage;

import java.util.concurrent.TimeUnit;

/**
* @Title: CallBackRetryHandler
* @Description: //TODO (用一句话描述该文件做什么)
* @author JerryLong
* @date 2022/6/6 9:59 AM
* @version V1.0
*/
public interface CallBackRetryHandler {

    /**
     * 执行重试
     *
     * @param callBackMessage
     * @param delayTime
     * @param timeUnit
     * @return
     */
    CallBackStatus retry(CallBackMessage callBackMessage, Long delayTime, TimeUnit timeUnit);
}
