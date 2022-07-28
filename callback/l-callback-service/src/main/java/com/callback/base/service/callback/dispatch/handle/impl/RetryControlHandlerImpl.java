package com.callback.base.service.callback.dispatch.handle.impl;

import com.callback.base.service.callback.infra.RetryRateConfigRepository;
import com.callback.base.service.callback.model.CallBackMessage;
import com.callback.base.service.callback.dispatch.handle.CallBackHandleFunction;
import com.callback.base.service.callback.dispatch.handle.CallBackRetryHandler;
import com.callback.base.service.callback.dispatch.handle.RetryControlHandler;
import com.callback.base.service.callback.dispatch.handle.model.CallBackStatus;
import com.callback.base.service.exception.RetryOverException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RetryControlHandlerImpl
 * @Description: 回调重试实现
 * @date 2022/5/26 5:37 PM
 */
@Service
@Slf4j
public class RetryControlHandlerImpl implements RetryControlHandler {

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
                log.warn("callBackHandle fail ! messageId : {} , retryTime : {}", callBackMessage.getMessageId(), callBackMessage.getRetryTime());
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
                    log.warn("callBackHandle fail ! retry over ! messageId : {} , retryTime : {}", callBackMessage.getMessageId(), callBackMessage.getRetryTime());
                } catch (Exception ex) {
                    log.error("callBackHandle fail ! retry error ! messageId : {} , retryTime : {}", callBackMessage.getMessageId(), callBackMessage.getRetryTime(), ex);
                }
                //返回已重试结束，失败
                return CallBackStatus.FAIL_RETRY_OVER;
        }
    }
}
