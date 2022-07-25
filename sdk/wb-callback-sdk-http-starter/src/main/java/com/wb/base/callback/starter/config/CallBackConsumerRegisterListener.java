package com.wb.base.callback.starter.config;

import com.wb.base.callback.sdk.annotations.CallBackConsumerParameter;
import com.wb.base.callback.sdk.consumer.CallBackConsumer;
import com.wb.base.callback.sdk.factory.CallBackConsumerFactory;
import com.wb.log.WbLogger;
import com.wb.log.WbLoggerFactory;
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

    protected static final WbLogger LOGGER = WbLoggerFactory.getLogger(CallBackConsumerRegisterListener.class);

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