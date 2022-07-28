package com.callback.base.starter.config;

import com.callback.base.sdk.annotations.CallBackConsumerParameter;
import com.callback.base.sdk.consumer.CallBackConsumer;
import com.callback.base.sdk.factory.CallBackConsumerFactory;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: MsFunctionOpsPushListener
 * @Description: 将所有Consumer实现拿到，注册到sdk容器中
 * @date 2022/4/13 11:24 AM
 */
@Component
public class CallBackConsumerRegisterListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private List<CallBackConsumer> consumers;

    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Override
    public void onApplicationEvent(ContextRefreshedEvent ev) {
        for (CallBackConsumer consumer : consumers) {
            CallBackConsumerFactory.registerByAnnotation(consumer.getClass().getAnnotation(CallBackConsumerParameter.class), consumer);
        }
    }
}