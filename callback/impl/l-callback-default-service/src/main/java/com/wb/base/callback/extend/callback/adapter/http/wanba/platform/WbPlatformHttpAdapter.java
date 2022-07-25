package com.wb.base.callback.extend.callback.adapter.http.wanba.platform;

import com.wb.base.callback.constants.CallBackPlatformTypeEnums;
import com.wb.base.callback.model.CallBackType;
import com.wb.base.callback.model.consumer.CallBackConsumerMessageBO;
import com.wb.base.callback.service.callback.adapter.AbstractCallBackAdapter;
import com.wb.base.callback.service.callback.dispatch.handle.model.CallBackStatus;
import com.wb.base.callback.service.callback.model.CallBackMessage;
import com.wb.base.callback.service.exception.CallBackTimeoutException;
import com.wb.base.component.starter.utils.okhttp3.OkHttpClientUtils;
import com.wb.common.WbJSON;
import com.wb.log.WbLogger;
import com.wb.log.WbLoggerFactory;
import okhttp3.Response;
import org.springframework.stereotype.Service;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: WbPlatformHttpAdapter
 * @Description: 玩吧平台HTTP回调实现
 * @date 2022/5/31 9:44 AM
 */
@Service
public class WbPlatformHttpAdapter extends AbstractCallBackAdapter {

    private static WbLogger LOGGER = WbLoggerFactory.getLogger(WbPlatformHttpAdapter.class);

    @Override
    public CallBackPlatformTypeEnums getPlatformType() {
        return CallBackPlatformTypeEnums.WANBA_PLATFORM;
    }

    @Override
    public CallBackType getCallBackType() {
        return CallBackType.HTTP;
    }

    @Override
    public CallBackStatus callBack(CallBackMessage callBackMessage) throws CallBackTimeoutException {
        LOGGER.info("玩吧平台http方式回调， messageId : {}", callBackMessage.getMessageId());
        try {
            CallBackConsumerMessageBO<Object> message = CallBackConsumerMessageBO.builder()
                    .platformType(callBackMessage.getPlatformType())
                    .businessType(callBackMessage.getBusinessType())
                    .messageId(callBackMessage.getMessageId())
                    .data(callBackMessage.getMessage())
                    .build();
            Response response = OkHttpClientUtils.send(callBackMessage.getCallBackConfig().getRoute() + callBackMessage.getCallBackConfig().getPath(), WbJSON.toJson(message));
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
            LOGGER.warn("WbPlatformHttpAdapter callback error ! messageId : {}", callBackMessage.getMessageId(), e);
            return CallBackStatus.FAIL;
        }
    }
}
