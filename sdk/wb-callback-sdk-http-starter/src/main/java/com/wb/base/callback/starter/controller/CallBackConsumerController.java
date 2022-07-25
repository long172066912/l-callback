package com.wb.base.callback.starter.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wb.base.callback.constants.CallBackConstants;
import com.wb.base.callback.model.consumer.CallBackConsumerMessageBO;
import com.wb.base.callback.sdk.consumer.CallBackConsumer;
import com.wb.base.callback.sdk.factory.CallBackConsumerFactory;
import com.wb.base.callback.sdk.utils.ConsumerClassUtils;
import com.wb.base.callback.utils.SunParameterizedTypeImpl;
import com.wb.common.WbJSON;
import com.wb.common.exception.WbPlatformException;
import com.wb.common.exception.WbPlatformSystemCode;
import com.wb.log.WbLogger;
import com.wb.log.WbLoggerFactory;
import org.apache.commons.lang.StringUtils;
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
public class CallBackConsumerController {

    private static WbLogger LOGGER = WbLoggerFactory.getLogger(CallBackConsumerController.class);

    /***
     * 默认回调消费接口
     *
     * @wbParam callBackMessage 描述 {@link CallBackConsumerMessageBO }
     * @nullable
     * @status false
     * @author JerryLong
     * @date 2022/5/31
     * @ready
     *
     * @return 返回信息
     */
    @PostMapping(CallBackConstants.DEFAULT_HTTP_PATH)
    public Boolean accept(@NotNull @RequestBody CallBackConsumerMessageBO callBackMessage) {
        if (null == callBackMessage.getPlatformType()
                || null == callBackMessage.getBusinessType()
                || StringUtils.isBlank(callBackMessage.getMessageId())
                || null == callBackMessage.getData()) {
            throw WbPlatformException.system(WbPlatformSystemCode.E90003);
        }
        CallBackConsumer<CallBackConsumerMessageBO> consumer = CallBackConsumerFactory.getConsumer(callBackMessage.getPlatformType(), callBackMessage.getBusinessType());
        Type genericInterfaces = ConsumerClassUtils.getGenericInterfaces(consumer);
        //通过序列化再反序列化，支持泛型
        CallBackConsumerMessageBO message = WbJSON.fromJson(WbJSON.toJson(callBackMessage), new TypeReference<CallBackConsumerMessageBO>() {
            @Override
            public Type getType() {
                return SunParameterizedTypeImpl.make(CallBackConsumerMessageBO.class, new Type[]{genericInterfaces}, genericInterfaces);
            }
        });
        if (null == consumer) {
            LOGGER.error(" CallBack Consume error ! Not found Consumer ! message : {}", WbJSON.toJson(callBackMessage));
            return false;
        }
        return consumer.accept(message);
    }
}
