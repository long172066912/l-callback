package com.callback.base.service.callback.adapter;

import com.callback.base.service.callback.model.CallBackMessage;
import com.callback.base.service.callback.dispatch.handle.model.CallBackStatus;
import com.callback.base.service.exception.CallBackTimeoutException;

/**
* @Title: BaseCallBackAdapter
* @Description: 适配器顶级接口
* @author JerryLong
* @date 2022/5/26 3:14 PM
* @version V1.0
*/
public interface BaseCallBackAdapter {

    /**
     * 进行回调
     * @param callBackMessage
     * @return CallBackStatus
     * @throws CallBackTimeoutException
     */
    CallBackStatus callBack(CallBackMessage callBackMessage) throws CallBackTimeoutException;
}
