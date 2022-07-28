package com.callback.base.starter.controller;

import com.callback.base.constants.CallBackConstants;
import com.callback.base.model.consumer.CallBackConsumerMessageBO;
import com.callback.base.sdk.consumer.CallBackConsumer;
import com.callback.base.sdk.factory.CallBackConsumerFactory;
import com.callback.base.sdk.utils.ConsumerClassUtils;
import com.callback.base.utils.SunParameterizedTypeImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.l.rpc.exception.BaseBusinessException;
import com.l.rpc.exception.SystemCode;
import com.l.rpc.json.LJSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Type;


/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallBackConsumerController
 * @Description: 默认回调消费接口
 * @date 2022/5/31 11:24 AM
 */
@RestController
@Slf4j
public class CallBackConsumerController {

    /**
     * 默认回调消费接口
     *
     * @param callBackMessage
     * @return
     */
    @PostMapping(CallBackConstants.DEFAULT_HTTP_PATH)
    public Boolean accept(@NotNull @RequestBody CallBackConsumerMessageBO callBackMessage) {
        if (null == callBackMessage.getPlatformType()
                || null == callBackMessage.getBusinessType()
                || StringUtils.isBlank(callBackMessage.getMessageId())
                || null == callBackMessage.getData()) {
            throw BaseBusinessException.system(SystemCode.E90003);
        }
        CallBackConsumer<CallBackConsumerMessageBO> consumer = CallBackConsumerFactory.getConsumer(callBackMessage.getPlatformType(), callBackMessage.getBusinessType());
        Type genericInterfaces = ConsumerClassUtils.getGenericInterfaces(consumer);
        //通过序列化再反序列化，支持泛型
        CallBackConsumerMessageBO message = LJSON.fromJson(LJSON.toJson(callBackMessage), new TypeReference<CallBackConsumerMessageBO>() {
            @Override
            public Type getType() {
                return SunParameterizedTypeImpl.make(CallBackConsumerMessageBO.class, new Type[]{genericInterfaces}, genericInterfaces);
            }
        });
        if (null == consumer) {
            log.error(" CallBack Consume error ! Not found Consumer ! message : {}", LJSON.toJson(callBackMessage));
            return false;
        }
        return consumer.accept(message);
    }
}
