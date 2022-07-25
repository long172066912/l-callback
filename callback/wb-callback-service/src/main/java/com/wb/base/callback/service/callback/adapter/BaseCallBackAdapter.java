package com.wb.base.callback.service.callback.adapter;

import com.wb.base.callback.service.callback.dispatch.handle.model.CallBackStatus;
import com.wb.base.callback.service.callback.model.CallBackMessage;
import com.wb.base.callback.service.exception.CallBackTimeoutException;

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
