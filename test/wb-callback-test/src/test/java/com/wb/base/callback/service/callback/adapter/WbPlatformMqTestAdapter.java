package com.wb.base.callback.service.callback.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wb.base.callback.constants.CallBackPlatformTypeEnums;
import com.wb.base.callback.model.CallBackType;
import com.wb.base.callback.model.consumer.CallBackConsumerMessageBO;
import com.wb.base.callback.sdk.consumer.CallBackConsumer;
import com.wb.base.callback.sdk.factory.CallBackConsumerFactory;
import com.wb.base.callback.sdk.utils.ConsumerClassUtils;
import com.wb.base.callback.service.callback.dispatch.handle.model.CallBackStatus;
import com.wb.base.callback.service.callback.model.CallBackMessage;
import com.wb.base.callback.service.exception.CallBackTimeoutException;
import com.wb.base.callback.utils.SunParameterizedTypeImpl;
import com.wb.common.WbJSON;
import com.wb.log.WbLogger;
import com.wb.log.WbLoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: WbPlatformHttpAdapter
 * @Description: 玩吧平台HTTP回调实现
 * @date 2022/5/31 9:44 AM
 */
@Service
public class WbPlatformMqTestAdapter extends AbstractCallBackAdapter {

    private static WbLogger LOGGER = WbLoggerFactory.getLogger(WbPlatformMqTestAdapter.class);

    @Override
    public CallBackPlatformTypeEnums getPlatformType() {
        return CallBackPlatformTypeEnums.TEST;
    }

    @Override
    public CallBackType getCallBackType() {
        return CallBackType.MQ;
    }

    @Override
    public CallBackStatus callBack(CallBackMessage callBackMessage) throws CallBackTimeoutException {
        CallBackConsumerMessageBO data = CallBackConsumerMessageBO.builder()
                .platformType(callBackMessage.getPlatformType())
                .businessType(callBackMessage.getBusinessType())
                .messageId(callBackMessage.getMessageId())
                .data(callBackMessage.getMessage())
                .build();
        CallBackConsumer<CallBackConsumerMessageBO> consumer = CallBackConsumerFactory.getConsumer(callBackMessage.getPlatformType(), callBackMessage.getBusinessType());
        Type genericInterfaces = ConsumerClassUtils.getGenericInterfaces(consumer);
        CallBackConsumerMessageBO message = WbJSON.fromJson(WbJSON.toJson(data), new TypeReference<CallBackConsumerMessageBO>() {
            @Override
            public Type getType() {
                return SunParameterizedTypeImpl.make(CallBackConsumerMessageBO.class, new Type[]{genericInterfaces}, genericInterfaces);
            }
        });
        consumer.accept(message);
        return CallBackStatus.SUCCESS;
    }
}
