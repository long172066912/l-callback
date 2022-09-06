package com.callback.base.extend.callback.adapter.mq.platform;

import com.callback.base.mq.MqClient;
import com.callback.base.mq.MqProducer;
import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.model.CallBackType;
import com.callback.base.model.consumer.CallBackConsumerMessageBO;
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
 * @Title: PlatformHttpAdapter
 * @Description: 平台HTTP回调实现
 * @date 2022/5/31 9:44 AM
 */
@Service
@Slf4j
public class PlatformMqAdapter extends AbstractCallBackAdapter {

    @Override
    public CallBackPlatformTypeEnums getPlatformType() {
        return CallBackPlatformTypeEnums.PLATFORM;
    }

    @Override
    public CallBackType getCallBackType() {
        return CallBackType.MQ;
    }

    @Override
    public CallBackStatus callBack(CallBackMessage callBackMessage) throws CallBackTimeoutException {
        log.info("平台mq方式回调， messageId : {}", callBackMessage.getMessageId());
        try {
            //TODO 使用自己的MQ组件
            Boolean r = MqClient.send(callBackMessage.getCallBackConfig().getTopic(),
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
                log.error("PlatformMqAdapter send fail ! messageId : {}", callBackMessage.getMessageId());
                return CallBackStatus.FAIL;
            }
            return CallBackStatus.SUCCESS;
        } catch (Exception e) {
            log.warn("PlatformMqAdapter callback error ! messageId : {}", callBackMessage.getMessageId(), e);
            return CallBackStatus.FAIL;
        }
    }
}
