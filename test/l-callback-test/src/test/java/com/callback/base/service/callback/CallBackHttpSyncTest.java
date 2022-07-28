package com.callback.base.service.callback;

import com.callback.base.service.callback.adapter.CallBackAdapterFactory;
import com.callback.base.service.callback.adapter.PlatformHttpTestAdapter;
import com.callback.base.service.callback.adapter.PlatformMqTestAdapter;
import com.callback.base.service.callback.model.CallBackConfig;
import com.callback.base.service.domain.CallBackMessageService;
import com.callback.base.service.domain.infra.repository.CallBackMessageRepository;
import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.model.CallBackProtocol;
import com.callback.base.model.CallBackSyncResponse;
import com.callback.base.model.CallBackType;
import com.callback.base.service.BaseTest;
import com.callback.base.service.callback.dispatch.BaseCallBackDispatch;
import com.callback.base.service.callback.dispatch.CallBackAsyncDispatch;
import com.callback.base.service.exception.ErrorConfigException;
import com.callback.base.service.model.TestMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

class CallBackHttpSyncTest extends BaseTest {

    @Autowired
    private CallBackMessageService callBackMessageService;

    @MockBean
    private CallBackMessageRepository repository;
    @Autowired
    private CallBackAsyncDispatch callBackAsyncDispatch;
    @Autowired
    private BaseCallBackDispatch callBackDispatch;
    /**
     * 同步回调测试-配置异常
     */
    @Test
    void callBackSyncErrorTest() throws Throwable {
        CallBackAdapterFactory.register(new PlatformHttpTestAdapter());
        CallBackAdapterFactory.register(new PlatformMqTestAdapter());
        //mock配置中心配置
        Mockito.doReturn(super.getDefaultCallBackConfigs()).when(repository).getConfig(Mockito.any(), Mockito.any());
        TestMessage testMessage = getTestMessage();
        Assertions.assertThrows(ErrorConfigException.class, () -> callBackMessageService.callBackSync(CallBackProtocol.builder()
                .platformType(CallBackPlatformTypeEnums.TEST)
                .businessType("TEST")
                .messageId(UUID.randomUUID().toString())
                .build(), testMessage));
    }

    /**
     * 同步回调测试-HTTP
     */
    @Test
    void callBackSyncTest() throws Throwable {
        CallBackAdapterFactory.register(new PlatformHttpTestAdapter());
        CallBackAdapterFactory.register(new PlatformMqTestAdapter());
        //mock配置中心配置
        Mockito.doReturn(this.getSyncConfigs()).when(repository).getConfig(Mockito.any(), Mockito.any());
        TestMessage testMessage = getTestMessage();
        List<CallBackSyncResponse> callBackSyncResponses = callBackMessageService.callBackSync(CallBackProtocol.builder()
                .platformType(CallBackPlatformTypeEnums.TEST)
                .businessType("TEST")
                .messageId(UUID.randomUUID().toString())
                .build(), testMessage);
        Assertions.assertEquals(callBackSyncResponses.size(), 2);
        Set<String> collect = this.getSyncConfigs().stream().map(e -> e.getRoute()).collect(Collectors.toSet());
        callBackSyncResponses.stream().forEach(callBackSyncResponse -> {
            Assertions.assertTrue(callBackSyncResponse.getStatus());
            Assertions.assertTrue(collect.contains(callBackSyncResponse.getRoute()));
        });
    }

    /**
     * 获取默认Apollo配置
     * @return
     */
    private List<CallBackConfig> getSyncConfigs() {
        List<CallBackConfig> configs = new ArrayList<>();
        configs.add(CallBackConfig.builder().callBackType(CallBackType.HTTP).route("user").path("").retry(1).build());
        configs.add(CallBackConfig.builder().callBackType(CallBackType.HTTP).route("test").path("/a/b/c").retry(1).build());
        return configs;
    }
}