package com.wb.base.callback.service.callback;

import com.wb.base.callback.constants.CallBackPlatformTypeEnums;
import com.wb.base.callback.model.CallBackProtocol;
import com.wb.base.callback.service.BaseTest;
import com.wb.base.callback.service.callback.model.CallBackMessage;
import com.wb.base.callback.service.domain.CallBackMessageService;
import com.wb.base.callback.service.domain.infra.repository.CallBackMessageRepository;
import com.wb.base.callback.service.exception.CallBackSaveException;
import com.wb.base.callback.service.exception.NoConfigException;
import com.wb.base.callback.service.model.TestMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;

class CallBackDomainTest extends BaseTest {

    @Autowired
    private CallBackMessageService callBackMessageService;

    @MockBean
    private CallBackMessageRepository repository;

    /**
     * 回调存储测试
     * @throws CallBackSaveException
     * @throws NoConfigException
     */
    @Test
    void callBackSaveKafkaTest() throws CallBackSaveException, NoConfigException {
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
        kafkaInnerQueue.clear();
    }

    @Test
    void callBackSaveMysqlTest() throws CallBackSaveException, NoConfigException {
        //mock配置中心配置
        Mockito.doReturn(super.getDefaultCallBackConfigs()).when(repository).getConfig(Mockito.any(), Mockito.any());
        //重写实现，直接保存到kafkaQueue中
        Mockito.doAnswer(e -> {
            throw new CallBackSaveException();
        }).when(repository).saveCallBackMessage(Mockito.any(), Mockito.any());
        //重写实现，直接保存到mysqlMap中
        Mockito.doAnswer(e -> dbList.add(e.getArgument(1, CallBackMessage.class))).when(repository).saveCallBackMessageToDb(Mockito.any(), Mockito.any());
        TestMessage testMessage = getTestMessage();
        callBackMessageService.saveCallBackMessage(CallBackProtocol.builder()
                .platformType(CallBackPlatformTypeEnums.TEST)
                .businessType("TEST")
                .messageId(UUID.randomUUID().toString())
                .build(), testMessage);
        //断言：kafka队列中没有数据，MySQL中有数据
        Assertions.assertEquals(kafkaInnerQueue.size() == 0, true);
        Assertions.assertEquals(dbList.size() == 2, true);
        kafkaInnerQueue.clear();
        dbList.clear();
    }
}