package com.callback.base.extend.callback.adapter.http.platform;

import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.model.CallBackType;
import com.callback.base.model.consumer.CallBackConsumerMessageBO;
import com.callback.base.service.callback.adapter.AbstractCallBackAdapter;
import com.callback.base.service.callback.dispatch.handle.model.CallBackStatus;
import com.callback.base.service.callback.model.CallBackMessage;
import com.callback.base.service.exception.CallBackTimeoutException;
import com.l.rpc.json.LJSON;
import com.l.rpc.utils.okhttp3.OkHttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.stereotype.Service;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: PlatformHttpAdapter
 * @Description: 平台HTTP回调实现
 * @date 2022/5/31 9:44 AM
 */
@Service
@Slf4j
public class PlatformHttpAdapter extends AbstractCallBackAdapter {

    @Override
    public CallBackPlatformTypeEnums getPlatformType() {
        return CallBackPlatformTypeEnums.PLATFORM;
    }

    @Override
    public CallBackType getCallBackType() {
        return CallBackType.HTTP;
    }

    @Override
    public CallBackStatus callBack(CallBackMessage callBackMessage) throws CallBackTimeoutException {
        log.info("平台http方式回调， messageId : {}", callBackMessage.getMessageId());
        try {
            CallBackConsumerMessageBO<Object> message = CallBackConsumerMessageBO.builder()
                    .platformType(callBackMessage.getPlatformType())
                    .businessType(callBackMessage.getBusinessType())
                    .messageId(callBackMessage.getMessageId())
                    .data(callBackMessage.getMessage())
                    .build();
            Response response = OkHttpClientUtils.send(callBackMessage.getCallBackConfig().getRoute() + callBackMessage.getCallBackConfig().getPath(), LJSON.toJson(message));
            if (null == response || !response.isSuccessful()) {
                return CallBackStatus.FAIL;
            }
            String trueStr = "false";
            if (trueStr.equals(response.body().string())) {
                return CallBackStatus.FAIL_NO_RETRY;
            } else {
                return CallBackStatus.SUCCESS;
            }
        } catch (Exception e) {
            log.warn("PlatformHttpAdapter callback error ! messageId : {}", callBackMessage.getMessageId(), e);
            return CallBackStatus.FAIL;
        }
    }
}
