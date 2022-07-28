package com.callback.base.service.callback;

import com.callback.base.service.callback.adapter.CallBackAdapterFactory;
import com.callback.base.service.callback.adapter.PlatformMqTestAdapter;
import com.callback.base.service.callback.model.CallBackMessage;
import com.callback.base.service.domain.CallBackMessageService;
import com.callback.base.service.domain.infra.repository.CallBackMessageRepository;
import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.model.CallBackProtocol;
import com.callback.base.sdk.annotations.CallBackConsumerParameter;
import com.callback.base.sdk.factory.CallBackConsumerFactory;
import com.callback.base.service.BaseTest;
import com.callback.base.service.callback.adapter.PlatformHttpTestAdapter;
import com.callback.base.service.callback.dispatch.BaseCallBackDispatch;
import com.callback.base.service.consumer.mq.MqTestCallBackConsumer;
import com.callback.base.service.exception.CallBackSaveException;
import com.callback.base.service.exception.CallBackTimeoutException;
import com.callback.base.service.exception.NoConfigException;
import com.callback.base.service.model.TestMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;

class CallBackMqTest extends BaseTest {

    @Autowired
    private CallBackMessageService callBackMessageService;

    @MockBean
    private CallBackMessageRepository repository;
    @Autowired
    private BaseCallBackDispatch callBackDispatch;

    @BeforeEach
    public void befor() throws CallBackSaveException, NoConfigException, CallBackTimeoutException {
        CallBackAdapterFactory.register(new PlatformHttpTestAdapter());
        CallBackAdapterFactory.register(new PlatformMqTestAdapter());
        kafkaInnerQueue.clear();
        //mock配置中心配置
        Mockito.doReturn(super.getDefaultCallBackConfigs()).when(repository).getConfig(Mockito.any(), Mockito.any());
        //重写实现，直接保存到kafkaQueue中
        Mockito.doAnswer(e -> kafkaInnerQueue.add(e.getArgument(1, CallBackMessage.class))).when(repository).saveCallBackMessage(Mockito.any(), Mockito.any());
        TestMessage testMessage = getTestMessage();
        callBackMessageService.saveCallBackMessage(CallBackProtocol.builder()
                .platformType(CallBackPlatformTypeEnums.TEST)
                .businessType("TEST")
                .messageId(UUID.randomUUID().toString())
                .build(), testMessage);
        //断言：kafka队列中已经有数据
        Assertions.assertEquals(kafkaInnerQueue.size() == 2, true);
    }

    /**
     * 回调测试-HTTP
     */
    @Test
    void callBackTest() throws Throwable {
        //替换处理方式为MQ
//        CallbackClient.getInstance().startConsume(Arrays.asList(new MqTestCallBackConsumer()));
        MqTestCallBackConsumer mqTestCallBackConsumer = new MqTestCallBackConsumer();
        CallBackConsumerFactory.registerByAnnotation(mqTestCallBackConsumer.getClass().getAnnotation(CallBackConsumerParameter.class), mqTestCallBackConsumer);
        //开始消费
        callBackDispatch.callBack(kafkaInnerQueue.poll());
        callBackDispatch.callBack(kafkaInnerQueue.poll());
    }
}