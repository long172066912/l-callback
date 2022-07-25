package com.wb.base.callback.service.callback;

import com.wb.base.callback.constants.CallBackPlatformTypeEnums;
import com.wb.base.callback.extend.callback.adapter.http.test.TestHttpAdapter;
import com.wb.base.callback.model.CallBackProtocol;
import com.wb.base.callback.service.BaseTest;
import com.wb.base.callback.service.callback.adapter.CallBackAdapterFactory;
import com.wb.base.callback.service.callback.adapter.WbPlatformHttpTestAdapter;
import com.wb.base.callback.service.callback.adapter.WbPlatformMqTestAdapter;
import com.wb.base.callback.service.callback.dispatch.BaseCallBackDispatch;
import com.wb.base.callback.service.callback.dispatch.handle.CallBackRetryHandler;
import com.wb.base.callback.service.callback.dispatch.handle.model.CallBackStatus;
import com.wb.base.callback.service.callback.model.CallBackMessage;
import com.wb.base.callback.service.consumer.http.HttpTestCallBackConsumer;
import com.wb.base.callback.service.domain.CallBackMessageService;
import com.wb.base.callback.service.domain.infra.repository.CallBackMessageRepository;
import com.wb.base.callback.service.exception.CallBackSaveException;
import com.wb.base.callback.service.exception.CallBackTimeoutException;
import com.wb.base.callback.service.exception.NoConfigException;
import com.wb.base.callback.service.model.TestMessage;
import com.wb.base.callback.utils.CallBackApolloConfigUtils;
import com.wb.common.exception.WbPlatformException;
import com.wb.common.exception.WbPlatformSystemCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

class CallBackHttpRetryTest extends BaseTest {

    @Autowired
    private CallBackMessageService callBackMessageService;

    @MockBean
    private CallBackMessageRepository repository;
    @Autowired
    private BaseCallBackDispatch callBackDispatch;
    @MockBean
    private CallBackRetryHandler callBackRetryHandler;
    @MockBean
    private HttpTestCallBackConsumer httpTestCallBackConsumer;
    @MockBean
    private TestHttpAdapter testHttpAdapter;

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
    void callBackErrorTest() throws Throwable {
        AtomicInteger i = new AtomicInteger();
        //mock重试逻辑
        Mockito.doAnswer(e -> {
            System.out.println("执行重试");
            kafkaInnerQueue.add(e.getArgument(0));
            i.getAndIncrement();
            return CallBackStatus.SUCCESS;
        }).when(callBackRetryHandler).retry(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doAnswer(e -> {
            throw WbPlatformException.system(WbPlatformSystemCode.E90003);
        }).when(httpTestCallBackConsumer).accept(Mockito.any());
        //开始消费
        //mock配置中心静态返回
        try (MockedStatic<CallBackApolloConfigUtils> callBackApolloConfigUtilsMockedStatic = Mockito.mockStatic(CallBackApolloConfigUtils.class)) {
            callBackApolloConfigUtilsMockedStatic.when(CallBackApolloConfigUtils::getRetryRateConfig).thenReturn(super.getRetryRateConfig());
            while (kafkaInnerQueue.size() > 0) {
                CallBackMessage message = kafkaInnerQueue.poll();
                callBackDispatch.callBack(message);
            }
        }
        Assertions.assertEquals(i.get() / 2, super.getRetryRateConfig().getArr().length);
    }
}