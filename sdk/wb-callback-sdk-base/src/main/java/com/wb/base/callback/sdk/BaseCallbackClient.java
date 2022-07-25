package com.wb.base.callback.sdk;

import com.wb.base.callback.model.CallBackProtocol;
import com.wb.base.callback.model.CallBackSyncResponse;
import com.wb.base.callback.sdk.exception.CallBackFailException;

import java.io.Closeable;
import java.util.List;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: WbCallbackClient
 * @Description: 回调客户端接口定义
 * @date 2022/5/30 4:03 PM
 */
public interface BaseCallbackClient extends Closeable {

    /**
     * 发起回调，支持多方回调
     *
     * @param callBackProtocol 回调协议，平台+业务类型，以及消息唯一Id
     * @param data             data数据不能太大
     * @return 返消息唯一Id
     * @throws CallBackFailException
     */
    String callback(CallBackProtocol callBackProtocol, Object data) throws CallBackFailException;

    /**
     * 进行同步回调
     *
     * @param callBackProtocol
     * @param data
     * @return
     * @throws CallBackFailException
     */
    List<CallBackSyncResponse> callbackSync(CallBackProtocol callBackProtocol, Object data) throws CallBackFailException;
}
