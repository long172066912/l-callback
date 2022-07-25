package com.wb.base.callback.service.callback.dispatch.handle.impl;

import com.wb.base.callback.service.callback.dispatch.handle.CallBackHandleFunction;
import com.wb.base.callback.service.callback.dispatch.handle.CallBackRetryHandler;
import com.wb.base.callback.service.callback.dispatch.handle.model.CallBackStatus;
import com.wb.base.callback.service.callback.dispatch.handle.RetryControlHandler;
import com.wb.base.callback.service.callback.infra.RetryRateConfigRepository;
import com.wb.base.callback.service.callback.model.CallBackMessage;
import com.wb.base.callback.service.exception.RetryOverException;
import com.wb.log.WbLogger;
import com.wb.log.WbLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RetryControlHandlerImpl
 * @Description: 玩吧回调重试实现
 * @date 2022/5/26 5:37 PM
 */
@Service
public class RetryControlHandlerImpl implements RetryControlHandler {

    private static WbLogger LOGGER = WbLoggerFactory.getLogger(RetryControlHandler.class);

    @Autowired
    private RetryRateConfigRepository retryRateConfigRepository;
    @Autowired
    private CallBackRetryHandler callBackRetryHandler;

    @Override
    public CallBackStatus retry(CallBackHandleFunction callBackHandleFunction, CallBackMessage callBackMessage) {
        switch (callBackHandleFunction.apply()) {
            case SUCCESS:
                return CallBackStatus.SUCCESS;
            case FAIL_NO_RETRY:
                return CallBackStatus.FAIL;
            case FAIL:
            default:
                LOGGER.warn("callBackHandle fail ! messageId : {} , retryTime : {}", callBackMessage.getMessageId(), callBackMessage.getRetryTime());
                try {
                    if (null == callBackMessage.getRetryTime()) {
                        callBackMessage.setRetryTime(0);
                    }
                    int nextRetryTime = retryRateConfigRepository.getNextRetryTime(callBackMessage.getRetryTime());
                    long delayTime = nextRetryTime - callBackMessage.getRetryTime();
                    callBackMessage.setRetryTime(nextRetryTime);
                    //开始重试
                    return callBackRetryHandler.retry(callBackMessage, delayTime, TimeUnit.SECONDS);
                } catch (RetryOverException e) {
                    LOGGER.warn("callBackHandle fail ! retry over ! messageId : {} , retryTime : {}", callBackMessage.getMessageId(), callBackMessage.getRetryTime());
                } catch (Exception ex) {
                    LOGGER.error("callBackHandle fail ! retry error ! messageId : {} , retryTime : {}", callBackMessage.getMessageId(), callBackMessage.getRetryTime(), ex);
                }
                //返回已重试结束，失败
                return CallBackStatus.FAIL_RETRY_OVER;
        }
    }
}
