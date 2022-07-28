package com.callback.base.extend.callback.dispatch.handle;

import com.callback.base.service.callback.dispatch.handle.CallBackRetryHandler;
import com.callback.base.service.callback.dispatch.handle.model.CallBackStatus;
import com.callback.base.service.callback.model.CallBackMessage;
import com.l.rpc.json.LJSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.callback.base.constants.MqTopicEnums.CALL_BACK_RETRY;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: MqRetryHandlerImpl
 * @Description: 重试MQ实现
 * @date 2022/5/27 6:34 PM
 */
@Service
@Slf4j
public class MqRetryHandlerImpl implements CallBackRetryHandler {

    @Override
    public CallBackStatus retry(CallBackMessage callBackMessage, Long delayTime, TimeUnit timeUnit) {
        try {
            Boolean r = MqProducer.send(CALL_BACK_RETRY, LJSON.toJson(callBackMessage), delayTime, timeUnit).get();
            if (!r) {
                log.error("MqRetryHandlerImpl send RocketMq fail ! messageId : {}", callBackMessage.getMessageId());
                return CallBackStatus.FAIL;
            }
            return CallBackStatus.SUCCESS;
        } catch (Exception e) {
            log.error("MqRetryHandlerImpl send RocketMq error ! messageId : {}", callBackMessage.getMessageId(), e);
            return CallBackStatus.FAIL;
        }
    }
}
