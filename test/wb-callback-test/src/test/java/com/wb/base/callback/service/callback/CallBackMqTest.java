package com.wb.base.callback.service.callback;

import com.wb.base.callback.constants.CallBackPlatformTypeEnums;
import com.wb.base.callback.model.CallBackProtocol;
import com.wb.base.callback.sdk.annotations.CallBackConsumerParameter;
import com.wb.base.callback.sdk.factory.CallBackConsumerFactory;
import com.wb.base.callback.service.BaseTest;
import com.wb.base.callback.service.callback.adapter.CallBackAdapterFactory;
import com.wb.base.callback.service.callback.adapter.WbPlatformHttpTestAdapter;
import com.wb.base.callback.service.callback.adapter.WbPlatformMqTestAdapter;
import com.wb.base.callback.service.callback.dispatch.BaseCallBackDispatch;
import com.wb.base.callback.service.callback.model.CallBackMessage;
import com.wb.base.callback.service.consumer.mq.MqTestCallBackConsumer;
import com.wb.base.callback.service.domain.CallBackMessageService;
import com.wb.base.callback.service.domain.infra.repository.CallBackMessageRepository;
import com.wb.base.callback.service.exception.CallBackSaveException;
import com.wb.base.callback.service.exception.CallBackTimeoutException;
import com.wb.base.callback.service.exception.NoConfigException;
import com.wb.base.callback.service.model.TestMessage;
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
        CallBackAdapterFactory.register(new WbPlatformHttpTestAdapter());
        CallBackAdapterFactory.register(new WbPlatformMqTestAdapter());
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