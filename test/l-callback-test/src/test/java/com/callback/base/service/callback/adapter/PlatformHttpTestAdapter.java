package com.callback.base.service.callback.adapter;

import com.callback.base.service.callback.dispatch.handle.model.CallBackStatus;
import com.callback.base.service.callback.model.CallBackMessage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.model.CallBackType;
import com.callback.base.model.consumer.CallBackConsumerMessageBO;
import com.callback.base.sdk.consumer.CallBackConsumer;
import com.callback.base.sdk.factory.CallBackConsumerFactory;
import com.callback.base.sdk.utils.ConsumerClassUtils;
import com.callback.base.service.exception.CallBackTimeoutException;
import com.callback.base.utils.SunParameterizedTypeImpl;
import com.l.rpc.json.LJSON;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: PlatformHttpTestAdapter
 * @Description: 平台HTTP回调实现
 * @date 2022/5/31 9:44 AM
 */
@Service
public class PlatformHttpTestAdapter extends AbstractCallBackAdapter {

    @Override
    public CallBackPlatformTypeEnums getPlatformType() {
        return CallBackPlatformTypeEnums.TEST;
    }

    @Override
    public CallBackType getCallBackType() {
        return CallBackType.HTTP;
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
        CallBackConsumerMessageBO message = LJSON.fromJson(LJSON.toJson(data), new TypeReference<CallBackConsumerMessageBO>() {
            @Override
            public Type getType() {
                return SunParameterizedTypeImpl.make(CallBackConsumerMessageBO.class, new Type[]{genericInterfaces}, genericInterfaces);
            }
        });
        consumer.accept(message);
        return CallBackStatus.SUCCESS;
    }
}
