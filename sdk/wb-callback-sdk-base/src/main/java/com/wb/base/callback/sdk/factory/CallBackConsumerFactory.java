package com.wb.base.callback.sdk.factory;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.wb.base.callback.constants.CallBackConstants;
import com.wb.base.callback.constants.CallBackPlatformTypeEnums;
import com.wb.base.callback.model.consumer.CallBackConsumerMessageBO;
import com.wb.base.callback.sdk.annotations.CallBackConsumerParameter;
import com.wb.base.callback.sdk.client.AbstractCallbackClient;
import com.wb.base.callback.sdk.consumer.CallBackConsumer;
import com.wb.base.callback.sdk.properties.CallBackProperties;
import com.wb.base.callback.sdk.properties.ConsumerMqTopic;
import com.wb.log.WbLogger;
import com.wb.log.WbLoggerFactory;
import org.apache.commons.lang.StringUtils;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallBackConsumerFactory
 * @Description: 回调消费工厂
 * @date 2022/5/31 11:31 AM
 */
public class CallBackConsumerFactory {

    private static WbLogger LOGGER = WbLoggerFactory.getLogger(AbstractCallbackClient.class);

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
        LOGGER.info("new callback register ! platformType : {} , businessType : {} , consumer : {}", platformType, businessType, consumer.getClass().getSimpleName());
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
        LOGGER.info("register callback consumer , platformType : {} , businessType : {} , consumerName : {}", annotation.platformType(), annotation.businessType(), consumer.getClass().getSimpleName());
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
