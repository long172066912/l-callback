package com.callback.base.service.callback;

import com.callback.base.service.callback.adapter.CallBackAdapterFactory;
import com.callback.base.service.callback.adapter.PlatformHttpTestAdapter;
import com.callback.base.service.callback.adapter.PlatformMqTestAdapter;
import com.callback.base.service.callback.dispatch.handle.model.CallBackStatus;
import com.callback.base.service.callback.model.CallBackMessage;
import com.callback.base.service.domain.CallBackMessageService;
import com.callback.base.service.domain.infra.repository.CallBackMessageRepository;
import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.extend.callback.adapter.http.test.TestHttpAdapter;
import com.callback.base.model.CallBackProtocol;
import com.callback.base.service.BaseTest;
import com.callback.base.service.callback.dispatch.BaseCallBackDispatch;
import com.callback.base.service.callback.dispatch.handle.CallBackRetryHandler;
import com.callback.base.service.consumer.http.HttpTestCallBackConsumer;
import com.callback.base.service.exception.CallBackSaveException;
import com.callback.base.service.exception.CallBackTimeoutException;
import com.callback.base.service.exception.NoConfigException;
import com.callback.base.service.model.TestMessage;
import com.callback.base.utils.CallBackApolloConfigUtils;
import com.l.rpc.exception.BaseBusinessException;
import com.l.rpc.exception.SystemCode;
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
            throw BaseBusinessException.system(SystemCode.E90003);
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