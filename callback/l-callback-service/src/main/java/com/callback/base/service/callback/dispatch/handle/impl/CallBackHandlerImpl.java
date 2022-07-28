package com.callback.base.service.callback.dispatch.handle.impl;

import com.callback.base.service.callback.adapter.CallBackAdapterManager;
import com.callback.base.service.callback.model.CallBackMessage;
import com.callback.base.service.callback.dispatch.handle.CallBackHandler;
import com.callback.base.service.callback.dispatch.handle.model.CallBackStatus;
import com.callback.base.service.exception.CallBackTimeoutException;
import com.callback.base.service.exception.NoAdapterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @Title: CallBackHandlerImpl
* @Description: 回调处理
* @author JerryLong
* @date 2022/5/27 5:36 PM
* @version V1.0
*/
@Service
@Slf4j
public class CallBackHandlerImpl implements CallBackHandler {

    @Autowired
    private CallBackAdapterManager callBack;

    @Override
    public CallBackStatus handle(CallBackMessage callBackMessage) {
        //通过BaseCallBackAdapter进行调度
        try {
            return callBack.doCallBack(callBackMessage);
        } catch (CallBackTimeoutException e) {
            log.warn("CallBackHandler fail ! timeout ! messageId : {}", callBackMessage.getMessageId(), e);
            return CallBackStatus.FAIL;
        } catch (NoAdapterException e) {
            log.error("CallBackHandler error ! NoAdapterException ! messageId : {}", callBackMessage.getMessageId(), e);
            return CallBackStatus.FAIL_NO_RETRY;
        } catch (Exception e) {
            log.error("CallBackHandler error ! messageId : {}", callBackMessage.getMessageId(), e);
            return CallBackStatus.FAIL;
        }
    }
}
