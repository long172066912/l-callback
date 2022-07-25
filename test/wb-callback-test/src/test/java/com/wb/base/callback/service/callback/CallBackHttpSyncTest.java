package com.wb.base.callback.service.callback;

import com.wb.base.callback.constants.CallBackPlatformTypeEnums;
import com.wb.base.callback.model.CallBackProtocol;
import com.wb.base.callback.model.CallBackSyncResponse;
import com.wb.base.callback.model.CallBackType;
import com.wb.base.callback.service.BaseTest;
import com.wb.base.callback.service.callback.adapter.CallBackAdapterFactory;
import com.wb.base.callback.service.callback.adapter.WbPlatformHttpTestAdapter;
import com.wb.base.callback.service.callback.adapter.WbPlatformMqTestAdapter;
import com.wb.base.callback.service.callback.dispatch.BaseCallBackDispatch;
import com.wb.base.callback.service.callback.dispatch.CallBackAsyncDispatch;
import com.wb.base.callback.service.callback.model.CallBackConfig;
import com.wb.base.callback.service.domain.CallBackMessageService;
import com.wb.base.callback.service.domain.infra.repository.CallBackMessageRepository;
import com.wb.base.callback.service.exception.ErrorConfigException;
import com.wb.base.callback.service.model.TestMessage;
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
        CallBackAdapterFactory.register(new WbPlatformHttpTestAdapter());
        CallBackAdapterFactory.register(new WbPlatformMqTestAdapter());
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
        CallBackAdapterFactory.register(new WbPlatformHttpTestAdapter());
        CallBackAdapterFactory.register(new WbPlatformMqTestAdapter());
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