package com.wb.base.callback.service.callback.dispatch.handle.impl;

import com.wb.base.callback.service.callback.adapter.CallBackAdapterManager;
import com.wb.base.callback.service.callback.dispatch.handle.CallBackHandler;
import com.wb.base.callback.service.callback.dispatch.handle.model.CallBackStatus;
import com.wb.base.callback.service.callback.model.CallBackMessage;
import com.wb.base.callback.service.exception.CallBackTimeoutException;
import com.wb.base.callback.service.exception.NoAdapterException;
import com.wb.log.WbLogger;
import com.wb.log.WbLoggerFactory;
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
public class CallBackHandlerImpl implements CallBackHandler {

    private static WbLogger LOGGER = WbLoggerFactory.getLogger(CallBackHandler.class);

    @Autowired
    private CallBackAdapterManager callBack;

    @Override
    public CallBackStatus handle(CallBackMessage callBackMessage) {
        //通过BaseCallBackAdapter进行调度
        try {
            return callBack.doCallBack(callBackMessage);
        } catch (CallBackTimeoutException e) {
            LOGGER.warn("CallBackHandler fail ! timeout ! messageId : {}", callBackMessage.getMessageId(), e);
            return CallBackStatus.FAIL;
        } catch (NoAdapterException e) {
            LOGGER.error("CallBackHandler error ! NoAdapterException ! messageId : {}", callBackMessage.getMessageId(), e);
            return CallBackStatus.FAIL_NO_RETRY;
        } catch (Exception e) {
            LOGGER.error("CallBackHandler error ! messageId : {}", callBackMessage.getMessageId(), e);
            return CallBackStatus.FAIL;
        }
    }
}
