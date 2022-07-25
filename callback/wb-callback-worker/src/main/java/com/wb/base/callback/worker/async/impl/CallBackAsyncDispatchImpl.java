package com.wb.base.callback.worker.async.impl;

import com.wb.base.callback.service.backpressure.constants.BackPressureEnums;
import com.wb.base.callback.service.callback.dispatch.BaseCallBackDispatch;
import com.wb.base.callback.service.callback.dispatch.CallBackAsyncDispatch;
import com.wb.base.callback.worker.async.wrap.CallBackAsyncWrapper;
import com.wb.base.callback.service.callback.model.CallBackMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallBackAsyncDispatchImpl
 * @Description: 异步回调执行调度实现
 * @date 2022/5/26 5:44 PM
 */
@Service
public class CallBackAsyncDispatchImpl implements CallBackAsyncDispatch {

    @Autowired
    private CallBackAsyncWrapper callBackAsyncWrapper;
    @Autowired
    private BaseCallBackDispatch baseCallBackDispatch;
    @Override
    public void asyncCallBack(BackPressureEnums backPressureEnums, CallBackMessage callBackMessage) {
        callBackAsyncWrapper.wrap(
                null == backPressureEnums ? BackPressureEnums.FAST : backPressureEnums, () -> baseCallBackDispatch.callBack(callBackMessage)
        );
    }
}
