package com.wb.base.callback.service.callback.dispatch.handle;

import com.wb.base.callback.service.callback.dispatch.handle.model.CallBackStatus;

/**
* @Title: CallBackRetryFunction
* @Description: 回调重试包装
* @author JerryLong
* @date 2022/5/26 3:39 PM
* @version V1.0
*/
@FunctionalInterface
public interface CallBackHandleFunction {
    /**
     * 执行逻辑
     * @return
     */
    CallBackStatus apply();
}
