package com.callback.base.sdk.factory;

import com.callback.base.sdk.annotations.CallBackConsumerParameter;
import com.callback.base.sdk.properties.CallBackProperties;
import com.callback.base.sdk.properties.ConsumerMqTopic;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.callback.base.constants.CallBackConstants;
import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.model.consumer.CallBackConsumerMessageBO;
import com.callback.base.sdk.consumer.CallBackConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallBackConsumerFactory
 * @Description: 回调消费工厂
 * @date 2022/5/31 11:31 AM
 */
@Slf4j
public class CallBackConsumerFactory {

    private static Table<CallBackPlatformTypeEnums, String, CallBackConsumer> table = HashBasedTable.create();
    /**
     * 默认配置
     */
    private static CallBackProperties callBackProperties = CallBackProperties.builder()
            .httpUrls(HashBasedTable.create())
            .topics(HashBasedTable.create())
            .build();

    /**
     * 注册消费逻辑
     *
     * @param platformType
     * @param businessType
     * @param consumer
     * @param <T>
     */
    public static synchronized <T> void regsiter(CallBackPlatformTypeEnums platformType, String businessType, CallBackConsumer<T> consumer) {
        log.info("new callback register ! platformType : {} , businessType : {} , consumer : {}", platformType, businessType, consumer.getClass().getSimpleName());
        table.put(platformType, businessType, consumer);
    }

    /**
     * 根据注解注册
     *
     * @param annotation
     * @param consumer
     */
    public static void registerByAnnotation(CallBackConsumerParameter annotation, CallBackConsumer consumer) throws Throwable {
        if (null == annotation || null == annotation.businessType() || null == annotation.platformType() || null == annotation.callbackType()) {
            throw new Throwable("CallBackConsumer 必须使用注解 @CallBackConsumerParameter 标注 Consumer [ 平台 | 业务场景 | 回调方式]");
        }
        log.info("register callback consumer , platformType : {} , businessType : {} , consumerName : {}", annotation.platformType(), annotation.businessType(), consumer.getClass().getSimpleName());
        //将回调消费根据平台+业务场景注册到工厂
        CallBackConsumerFactory.regsiter(annotation.platformType(), annotation.businessType(), consumer);
        switch (annotation.callbackType()) {
            case MQ:
                if (StringUtils.isBlank(annotation.mqGroup())  || StringUtils.isBlank(annotation.mqTopic())) {
                    throw new Throwable("MQ方式 必须指定 [ group | topic]");
                }
                //注册mq
                CallBackConsumerFactory.getCallBackProperties().getTopics().put(annotation.platformType(), annotation.businessType(), ConsumerMqTopic.builder().group(annotation.mqGroup()).topic(annotation.mqTopic()).build());
                break;
            case HTTP:
                //注册http,HTTP方式走默认接口
                CallBackConsumerFactory.getCallBackProperties().getHttpUrls().put(annotation.platformType(), annotation.businessType(), StringUtils.isBlank(annotation.httpPath()) ? CallBackConstants.DEFAULT_HTTP_PATH : annotation.httpPath());
            default:
                break;
        }
    }

    /**
     * 获取消费者
     *
     * @param platformType
     * @param businessType
     * @return
     */
    public static CallBackConsumer<CallBackConsumerMessageBO> getConsumer(CallBackPlatformTypeEnums platformType, String businessType) {
        return table.get(platformType, businessType);
    }

    /**
     * 设置配置信息
     *
     * @param properties
     */
    public static void setCallBackProperties(CallBackProperties properties) {
        callBackProperties = properties;
    }

    /**
     * 获取配置
     *
     * @return
     */
    public static CallBackProperties getCallBackProperties() {
        return callBackProperties;
    }
}
