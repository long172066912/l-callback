package com.wb.base.callback.starter.provider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wb.base.callback.constants.CallBackPlatformTypeEnums;
import com.wb.base.callback.model.consumer.CallBackConsumerMessageBO;
import com.wb.base.callback.sdk.consumer.CallBackConsumer;
import com.wb.base.callback.sdk.factory.CallBackConsumerFactory;
import com.wb.base.callback.sdk.properties.ConsumerMqTopic;
import com.wb.base.callback.sdk.utils.ConsumerClassUtils;
import com.wb.base.callback.starter.CallBackMqProvider;
import com.wb.base.callback.utils.SunParameterizedTypeImpl;
import com.wb.common.WbJSON;
import com.wb.kafka.WkConsumer;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: WbKafkaProvider
 * @Description: 玩吧kafka实现
 * @date 2022/6/1 9:55 AM
 */
public class WbKafkaProvider implements CallBackMqProvider {

    public WbKafkaProvider(CallBackPlatformTypeEnums platformType, String businessType, ConsumerMqTopic topic) {
        this.platformType = platformType;
        this.businessType = businessType;
        this.topic = topic;
    }

    private WkConsumer wkConsumer;

    private CallBackPlatformTypeEnums platformType;

    private String businessType;

    private ConsumerMqTopic topic;

    @Override
    public void start() {
        wkConsumer = new WkConsumer(
                topic.getGroup(),
                topic.getTopic(),
                value -> {
                    CallBackConsumer<CallBackConsumerMessageBO> consumer = CallBackConsumerFactory.getConsumer(platformType, businessType);
                    Type genericInterfaces = ConsumerClassUtils.getGenericInterfaces(consumer);
                    //通过序列化再反序列化，支持泛型
                    CallBackConsumerMessageBO message = WbJSON.fromJson(value, new TypeReference<CallBackConsumerMessageBO>() {
                        @Override
                        public Type getType() {
                            return SunParameterizedTypeImpl.make(CallBackConsumerMessageBO.class, new Type[]{genericInterfaces}, genericInterfaces);
                        }
                    });
                    consumer.accept(message);
                }
        );
    }

    @Override
    public void close() throws IOException {
        wkConsumer.disableShutdownHook();
    }
}
