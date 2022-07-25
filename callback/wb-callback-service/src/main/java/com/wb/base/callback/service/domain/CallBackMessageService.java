package com.wb.base.callback.service.domain;


import com.wb.base.callback.model.CallBackProtocol;
import com.wb.base.callback.model.CallBackSyncResponse;
import com.wb.base.callback.service.exception.ErrorConfigException;
import com.wb.base.callback.service.exception.NoConfigException;

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
