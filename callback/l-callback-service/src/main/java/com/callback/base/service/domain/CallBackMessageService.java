package com.callback.base.service.domain;


import com.callback.base.model.CallBackProtocol;
import com.callback.base.model.CallBackSyncResponse;
import com.callback.base.service.exception.ErrorConfigException;
import com.callback.base.service.exception.NoConfigException;

import java.util.List;

/**
 * Created by @author
 */
public interface CallBackMessageService {
    /**
     * 保存消息
     *
     * @param protocol
     * @param data
     * @throws NoConfigException
     */
    void saveCallBackMessage(CallBackProtocol protocol, Object data) throws NoConfigException;

    /**
     * 同步回调，并返回结果
     *
     * @param protocol
     * @param data
     * @return
     * @throws NoConfigException
     * @throws ErrorConfigException
     */
    List<CallBackSyncResponse> callBackSync(CallBackProtocol protocol, Object data) throws NoConfigException, ErrorConfigException;
}
