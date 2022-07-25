package com.wb.base.callback.extend.callback.adapter.mq.test;

import com.wb.base.callback.constants.CallBackPlatformTypeEnums;
import com.wb.base.callback.model.CallBackType;
import com.wb.base.callback.model.consumer.CallBackConsumerMessageBO;
import com.wb.base.callback.service.callback.adapter.AbstractCallBackAdapter;
import com.wb.base.callback.service.callback.dispatch.handle.model.CallBackStatus;
import com.wb.base.callback.service.callback.model.CallBackMessage;
import com.wb.base.callback.service.exception.CallBackTimeoutException;
import com.wb.common.WbJSON;
import com.wb.kafka.WkProducer;
import com.wb.log.WbLogger;
import com.wb.log.WbLoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: WbPlatformHttpAdapter
 * @Description: 玩吧平台HTTP回调实现
 * @date 2022/5/31 9:44 AM
 */
@Service
public class TestMqAdapter extends AbstractCallBackAdapter {

    private static WbLogger LOGGER = WbLoggerFactory.getLogger(TestMqAdapter.class);

    @Override
    public CallBackPlatformTypeEnums getPlatformType() {
        return CallBackPlatformTypeEnums.TEST;
    }

    @Override
    public CallBackType getCallBackType() {
        return CallBackType.MQ;
    }

    @Override
    public CallBackStatus callBack(CallBackMessage callBackMessage) throws CallBackTimeoutException {
        try {
            Boolean r = WkProducer.send(callBackMessage.getCallBackConfig().getTopic(),
                    WbJSON.toJson(
                            CallBackConsumerMessageBO.builder()
                                    .platformType(callBackMessage.getPlatformType())
                                    .businessType(callBackMessage.getBusinessType())
                                    .messageId(callBackMessage.getMessageId())
                                    .data(callBackMessage.getMessage())
                                    .build()
                    )
            ).get();
            if (!r) {
                LOGGER.error("WbPlatformMqAdapter send fail ! messageId : {}", callBackMessage.getMessageId());
                return CallBackStatus.FAIL;
            }
            return CallBackStatus.SUCCESS;
        } catch (Exception e) {
            LOGGER.warn("WbPlatformHttpAdapter callback error ! messageId : {}", callBackMessage.getMessageId(), e);
            return CallBackStatus.FAIL;
        }
    }
}
