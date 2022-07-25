package com.wb.base.callback.sdk.consumer;

import com.wb.base.callback.model.consumer.CallBackConsumerMessageBO;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallBackConsumer
 * @Description:
 * @date 2022/6/2 10:03 AM
 */
@FunctionalInterface
public interface CallBackConsumer<T> {

    /**
     * 消费执行
     *
     * @param message
     * @return
     */
    boolean accept(CallBackConsumerMessageBO<T> message);
}
