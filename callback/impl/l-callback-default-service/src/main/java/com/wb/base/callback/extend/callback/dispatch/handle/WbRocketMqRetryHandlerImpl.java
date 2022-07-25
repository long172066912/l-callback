package com.wb.base.callback.extend.callback.dispatch.handle;

import com.wb.base.callback.service.callback.dispatch.handle.CallBackRetryHandler;
import com.wb.base.callback.service.callback.dispatch.handle.model.CallBackStatus;
import com.wb.base.callback.service.callback.model.CallBackMessage;
import com.wb.common.WbJSON;
import com.wb.log.WbLogger;
import com.wb.log.WbLoggerFactory;
import com.wb.mq.MessageQueueProducer;
import com.wb.mq.config.enums.MessageQueueTopic;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**   
* @Title: WbRocketMqRetryControlHandlerImpl 
* @Description: 玩吧重试MQ实现
* @author JerryLong  
* @date 2022/5/27 6:34 PM 
* @version V1.0    
*/
@Service
public class WbRocketMqRetryHandlerImpl implements CallBackRetryHandler {

    private static WbLogger LOGGER = WbLoggerFactory.getLogger(WbRocketMqRetryHandlerImpl.class);
    
    @Override
    public CallBackStatus retry(CallBackMessage callBackMessage, Long delayTime, TimeUnit timeUnit) {
        try {
            Boolean r = MessageQueueProducer.send(MessageQueueTopic.CALL_BACK_RETRY, WbJSON.toJson(callBackMessage), delayTime, timeUnit).get();
            if (!r) {
                LOGGER.error("WbRocketMqRetryControlHandlerImpl send RocketMq fail ! messageId : {}", callBackMessage.getMessageId());
                return CallBackStatus.FAIL;
            }
            return CallBackStatus.SUCCESS;
        } catch (Exception e) {
            LOGGER.error("WbRocketMqRetryControlHandlerImpl send RocketMq error ! messageId : {}", callBackMessage.getMessageId(), e);
            return CallBackStatus.FAIL;
        }
    }
}
