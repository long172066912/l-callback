package com.callback.base.sdk.annotations;

import com.callback.base.constants.CallBackConstants;
import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.model.CallBackType;

import java.lang.annotation.*;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallBackConsumer
 * @Description: 自定义消费者注解
 * @date 2022/6/2 10:11 AM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface CallBackConsumerParameter {
    /**
     * 平台
     */
    CallBackPlatformTypeEnums platformType();

    /**
     * 业务场景
     */
    String businessType();

    /**
     * 回调方式
     *
     * @return
     */
    CallBackType callbackType() default CallBackType.HTTP;

    /**
     * http方式的接口地址
     * @return
     */
    String httpPath() default CallBackConstants.DEFAULT_HTTP_PATH;

    /**
     * mq方式的group
     * @return
     */
    String mqGroup() default "";

    /**
     * mq方式的topic
     * @return
     */
    String mqTopic() default "";
}
