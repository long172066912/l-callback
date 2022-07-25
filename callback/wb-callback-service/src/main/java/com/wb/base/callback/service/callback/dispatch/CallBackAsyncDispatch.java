package com.wb.base.callback.service.callback.dispatch;

import com.wb.base.callback.service.backpressure.constants.BackPressureEnums;
import com.wb.base.callback.service.callback.model.CallBackMessage;

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
