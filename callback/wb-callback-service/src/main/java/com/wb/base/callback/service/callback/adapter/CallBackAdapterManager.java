package com.wb.base.callback.service.callback.adapter;

import com.wb.base.callback.service.callback.dispatch.handle.model.CallBackStatus;
import com.wb.base.callback.service.callback.model.CallBackMessage;
import com.wb.base.callback.service.exception.CallBackTimeoutException;
import com.wb.base.callback.service.exception.NoAdapterException;
import com.wb.log.WbLogger;
import com.wb.log.WbLoggerFactory;
import org.springframework.stereotype.Service;

/**
* @Title: CallBackAdapterManager
* @Description: 适配器工厂
* @author JerryLong
* @date 2022/5/27 6:42 PM
* @version V1.0
*/
@Service
public class CallBackAdapterManager {

    private static WbLogger LOGGER = WbLoggerFactory.getLogger(CallBackAdapterManager.class);

    /**
     *
     * @param callBackMessage
     * @return
     */
    public CallBackStatus doCallBack(CallBackMessage callBackMessage) throws NoAdapterException, CallBackTimeoutException {
        BaseCallBackAdapter adapter = CallBackAdapterFactory.getAdapter(callBackMessage.getPlatformType(), callBackMessage.getCallBackConfig().getCallBackType());
        if (null == adapter) {
            LOGGER.error("CallBackAdapterManager doCallBack error ! not found adapter ! platform : {} , callbackType : {} , messageId : {}"
                    , callBackMessage.getPlatformType(), callBackMessage.getCallBackConfig().getCallBackType(), callBackMessage.getMessageId());
            throw new NoAdapterException();
        }
        return adapter.callBack(callBackMessage);
    }
}
