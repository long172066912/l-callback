package com.wb.base.callback.sdk.client;

import com.wb.base.callback.model.CallBackProtocol;
import com.wb.base.callback.model.CallBackSyncResponse;
import com.wb.base.callback.rpc.client.CallBackRpcClient;
import com.wb.base.callback.rpc.client.model.CallBackMessageDTO;
import com.wb.base.callback.sdk.BaseCallbackClient;
import com.wb.base.callback.sdk.exception.CallBackFailException;
import com.wb.common.WbJSON;
import com.wb.common.exception.WbPlatformException;
import com.wb.log.WbLogger;
import com.wb.log.WbLoggerFactory;
import com.wb.rpc.client.factory.RPCClientFactory;
import org.apache.commons.lang.StringUtils;

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

    private static WbLogger LOGGER = WbLoggerFactory.getLogger(AbstractCallbackClient.class);

    protected static CallBackRpcClient callBackRpcClient = RPCClientFactory.instance().getService(CallBackRpcClient.class);

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
            LOGGER.error("callbackSync callback fail ! params error ! callBackProtocol : {} , data : {}", WbJSON.toJson(callBackProtocol), WbJSON.toJson(data));
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
        } catch (WbPlatformException ex) {
            LOGGER.error("callbackSync callback fail ! WbPlatformException !", ex);
            throw new CallBackFailException();
        } catch (Exception e) {
            LOGGER.error("callbackSync callback fail !", e);
            throw new CallBackFailException();
        }
    }
}
