package com.wb.base.callback.service.callback.infra;

import com.wb.base.callback.service.callback.dispatch.handle.model.CallBackStatus;
import com.wb.base.callback.service.callback.model.CallBackMessage;

/**
* @Title: RetryRepository
* @Description: //TODO (用一句话描述该文件做什么)
* @author JerryLong
* @date 2022/5/27 6:22 PM
* @version V1.0
*/
public interface RetryRepository {

    /**
     * 重试，默认实现放到RocketMq
     * @param callBackMessage
     * @return
     */
    CallBackStatus retry(CallBackMessage callBackMessage);
}
