package com.callback.base.starter.client.provider;

import com.callback.base.mq.MqClient;
import com.callback.base.mq.MqConsumer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.model.consumer.CallBackConsumerMessageBO;
import com.callback.base.sdk.consumer.CallBackConsumer;
import com.callback.base.sdk.factory.CallBackConsumerFactory;
import com.callback.base.sdk.properties.ConsumerMqTopic;
import com.callback.base.sdk.utils.ConsumerClassUtils;
import com.callback.base.starter.client.CallBackMqProvider;
import com.callback.base.utils.SunParameterizedTypeImpl;
import com.l.rpc.json.LJSON;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: DefaultMqProvider
 * @Description: mq实现
 * @date 2022/6/1 9:55 AM
 */
public class DefaultMqProvider implements CallBackMqProvider {

    public DefaultMqProvider(CallBackPlatformTypeEnums platformType, String businessType, ConsumerMqTopic topic) {
        this.platformType = platformType;
        this.businessType = businessType;
        this.topic = topic;
    }

    /**
     * TODO 使用自己的MQ组件
     */
    private MqConsumer mqConsumer;

    private CallBackPlatformTypeEnums platformType;

    private String businessType;

    private ConsumerMqTopic topic;

    @Override
    public void start() {
        mqConsumer = MqClient.consume(
                topic.getGroup(),
                topic.getTopic(),
                value -> {
                    CallBackConsumer<CallBackConsumerMessageBO> consumer = CallBackConsumerFactory.getConsumer(platformType, businessType);
                    Type genericInterfaces = ConsumerClassUtils.getGenericInterfaces(consumer);
                    //通过序列化再反序列化，支持泛型
                    CallBackConsumerMessageBO message = LJSON.fromJson(value, new TypeReference<CallBackConsumerMessageBO>() {
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
        mqConsumer.close();
    }
}
