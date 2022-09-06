package com.callback.base.extend.callback.adapter.mq.test;

import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.model.CallBackType;
import com.callback.base.model.consumer.CallBackConsumerMessageBO;
import com.callback.base.mq.MqClient;
import com.callback.base.service.callback.adapter.AbstractCallBackAdapter;
import com.callback.base.service.callback.dispatch.handle.model.CallBackStatus;
import com.callback.base.service.callback.model.CallBackMessage;
import com.callback.base.service.exception.CallBackTimeoutException;
import com.l.rpc.json.LJSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: TestMqAdapter
 * @Description: 平台HTTP回调实现
 * @date 2022/5/31 9:44 AM
 */
@Service
@Slf4j
public class TestMqAdapter extends AbstractCallBackAdapter {

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
            boolean r = MqClient.send(callBackMessage.getCallBackConfig().getTopic(),
                    LJSON.toJson(
                            CallBackConsumerMessageBO.builder()
                                    .platformType(callBackMessage.getPlatformType())
                                    .businessType(callBackMessage.getBusinessType())
                                    .messageId(callBackMessage.getMessageId())
                                    .data(callBackMessage.getMessage())
                                    .build()
                    )
            );
            if (!r) {
                log.error("TestMqAdapter send fail ! messageId : {}", callBackMessage.getMessageId());
                return CallBackStatus.FAIL;
            }
            return CallBackStatus.SUCCESS;
        } catch (Exception e) {
            log.warn("TestMqAdapter callback error ! messageId : {}", callBackMessage.getMessageId(), e);
            return CallBackStatus.FAIL;
        }
    }
}
