package com.wb.base.callback.service.callback.dispatch.flow.impl;

import com.wb.base.callback.service.backpressure.report.HandleReporter;
import com.wb.base.callback.service.callback.dispatch.flow.CallBackDispatchFlow;
import com.wb.base.callback.service.callback.dispatch.handle.CallBackFailHandler;
import com.wb.base.callback.service.callback.dispatch.handle.CallBackHandler;
import com.wb.base.callback.service.callback.dispatch.handle.RetryControlHandler;
import com.wb.base.callback.service.callback.dispatch.handle.model.CallBackStatus;
import com.wb.base.callback.service.callback.model.CallBackMessage;
import com.wb.base.callback.service.utils.CallBackConfigUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallBackDispatchFlowImpl
 * @Description: //TODO (用一句话描述该文件做什么)
 * @date 2022/5/26 4:24 PM
 */
@Service
public class CallBackDispatchFlowImpl implements CallBackDispatchFlow {

    @Autowired
    private HandleReporter handleReporter;
    @Autowired
    private RetryControlHandler retryControlHandler;
    @Autowired
    private CallBackHandler callBackHandler;
    @Autowired
    private CallBackFailHandler callBackFailHandler;

    @Override
    public void callBack(CallBackMessage callBackMessage) {
        /**
         * 最外层通过重试控制
         */
        CallBackStatus status = retryControlHandler.retry(() ->
                /**
                 * 执行回调，并进行背压监控
                 */
                handleReporter.timer(
                        CallBackConfigUtils.getCallBackConfigKey(callBackMessage.getCallBackConfig()),
                        () -> callBackHandler.handle(callBackMessage)
                ), callBackMessage
        );
        /**
         * 回调 + 重试 失败
         */
        if (CallBackStatus.FAIL_RETRY_OVER.equals(status)) {
            callBackFailHandler.handle(callBackMessage);
        }
    }
}
