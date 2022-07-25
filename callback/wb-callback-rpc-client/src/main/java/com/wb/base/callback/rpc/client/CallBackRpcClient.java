package com.wb.base.callback.rpc.client;

import com.wb.base.callback.model.CallBackSyncResponse;
import com.wb.base.callback.rpc.client.model.CallBackMessageDTO;
import com.wb.rpc.client.annotation.RPCClient;
import com.wb.rpc.client.annotation.RPCMethod;

import java.util.List;

/**
 * Created by @author
 */
@RPCClient("pf-callback-server")
public interface CallBackRpcClient {

    /**
     * 消息回调，根据配置，可回调多个业务
     *
     * @param callBackMessage
     */
    @RPCMethod("/callback/message/callback")
    void callback(CallBackMessageDTO callBackMessage);

    /**
     * 同步回调，将阻塞，直到超时
     *
     * @param callBackMessage
     * @return
     */
    @RPCMethod("/callback/message/callbackSync")
    List<CallBackSyncResponse> callbackSync(CallBackMessageDTO callBackMessage);
}
