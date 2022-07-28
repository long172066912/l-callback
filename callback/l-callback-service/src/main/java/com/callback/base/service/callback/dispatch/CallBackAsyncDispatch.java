package com.callback.base.service.callback.dispatch;

import com.callback.base.service.callback.model.CallBackMessage;
import com.callback.base.service.backpressure.constants.BackPressureEnums;

/**
* @Title: CallBackAsyncDispatch
* @Description: 异步调度，推荐，支持背压
* @author JerryLong
* @date 2022/5/26 5:43 PM
* @version V1.0
*/
public interface CallBackAsyncDispatch {
    /**
     * 对回调的异步包装
     * @param backPressureEnums
     * @param callBackMessage
     */
    void asyncCallBack(BackPressureEnums backPressureEnums, CallBackMessage callBackMessage);
}
