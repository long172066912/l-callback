package com.wb.base.callback.service.callback.dispatch;

import com.wb.base.callback.service.callback.model.CallBackMessage;

/**
* @Title: BaseCallBackDispatch
* @Description: 调度器顶级接口，将以此抽象出流程编排，以及各种handle
* @author JerryLong
* @date 2022/5/26 3:16 PM
* @version V1.0
*/
public interface BaseCallBackDispatch {

    /**
     * 执行回调
     * @param callBackMessage
     */
    void callBack(CallBackMessage callBackMessage);
}
