package com.callback.base.service.callback.adapter;

import com.callback.base.service.callback.model.CallBackMessage;
import com.callback.base.service.callback.dispatch.handle.model.CallBackStatus;
import com.callback.base.service.exception.CallBackTimeoutException;
import com.callback.base.service.exception.NoAdapterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* @Title: CallBackAdapterManager
* @Description: 适配器工厂
* @author JerryLong
* @date 2022/5/27 6:42 PM
* @version V1.0
*/
@Service
@Slf4j
public class CallBackAdapterManager {

    /**
     *
     * @param callBackMessage
     * @return
     */
    public CallBackStatus doCallBack(CallBackMessage callBackMessage) throws NoAdapterException, CallBackTimeoutException {
        BaseCallBackAdapter adapter = CallBackAdapterFactory.getAdapter(callBackMessage.getPlatformType(), callBackMessage.getCallBackConfig().getCallBackType());
        if (null == adapter) {
            log.error("CallBackAdapterManager doCallBack error ! not found adapter ! platform : {} , callbackType : {} , messageId : {}"
                    , callBackMessage.getPlatformType(), callBackMessage.getCallBackConfig().getCallBackType(), callBackMessage.getMessageId());
            throw new NoAdapterException();
        }
        return adapter.callBack(callBackMessage);
    }
}
