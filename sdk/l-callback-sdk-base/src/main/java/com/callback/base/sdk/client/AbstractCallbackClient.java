package com.callback.base.sdk.client;

import com.callback.base.model.CallBackProtocol;
import com.callback.base.model.CallBackSyncResponse;
import com.callback.base.rpc.client.CallBackRpcClient;
import com.callback.base.rpc.client.model.CallBackMessageDTO;
import com.callback.base.sdk.BaseCallbackClient;
import com.callback.base.sdk.exception.CallBackFailException;
import com.l.rpc.exception.BaseBusinessException;
import com.l.rpc.factory.RpcFactory;
import com.l.rpc.json.LJSON;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallbackClient
 * @Description: 回调客户端
 * @date 2022/5/31 10:26 AM
 */
public abstract class AbstractCallbackClient implements BaseCallbackClient {

    private static Logger LOGGER = LoggerFactory.getLogger(AbstractCallbackClient.class);

    protected static CallBackRpcClient callBackRpcClient = RpcFactory.getInstance().getService(CallBackRpcClient.class);

    @Override
    public String callback(CallBackProtocol callBackProtocol, Object data) throws CallBackFailException {
        this.check(callBackProtocol, data);
        return this.doCallback(callBackProtocol, data, () -> {
            callBackRpcClient.callback(
                    CallBackMessageDTO.builder()
                            .protocol(callBackProtocol)
                            .data(data)
                            .build()
            );
            return callBackProtocol.getMessageId();
        });
    }

    /**
     * 进行同步回调
     *
     * @param callBackProtocol
     * @param data
     * @return
     */
    @Override
    public List<CallBackSyncResponse> callbackSync(CallBackProtocol callBackProtocol, Object data) throws CallBackFailException {
        this.check(callBackProtocol, data);
        return this.doCallback(callBackProtocol, data, () -> callBackRpcClient.callbackSync(
                CallBackMessageDTO.builder()
                        .protocol(callBackProtocol)
                        .data(data)
                        .build()
        ));
    }

    /**
     * 检测参数
     *
     * @param callBackProtocol
     * @param data
     * @throws CallBackFailException
     */
    private void check(CallBackProtocol callBackProtocol, Object data) throws CallBackFailException {
        if (null == callBackProtocol
                || null == callBackProtocol.getPlatformType()
                || null == callBackProtocol.getBusinessType()
                || StringUtils.isBlank(callBackProtocol.getMessageId())
                || null == data
        ) {
            LOGGER.error("callbackSync callback fail ! params error ! callBackProtocol : {} , data : {}", LJSON.toJson(callBackProtocol), LJSON.toJson(data));
            throw new CallBackFailException();
        }
    }

    /**
     * 处理回调
     *
     * @param callBackProtocol
     * @param data
     * @param supplier
     * @param <T>
     * @return
     * @throws CallBackFailException
     */
    private <T> T doCallback(CallBackProtocol callBackProtocol, Object data, Supplier<T> supplier) throws CallBackFailException {
        try {
            return supplier.get();
        } catch (BaseBusinessException ex) {
            LOGGER.error("callbackSync callback fail ! BaseBusinessException !", ex);
            throw new CallBackFailException();
        } catch (Exception e) {
            LOGGER.error("callbackSync callback fail !", e);
            throw new CallBackFailException();
        }
    }
}
