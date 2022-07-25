package com.wb.base.callback.worker;

import com.wb.base.callback.constants.CallBackPlatformTypeEnums;
import com.wb.base.callback.constants.MqTopicEnums;
import com.wb.base.callback.extend.infra.CallBackMessageDaoRepository;
import com.wb.base.callback.service.backpressure.constants.BackPressureEnums;
import com.wb.base.callback.service.callback.dispatch.BaseCallBackDispatch;
import com.wb.base.callback.service.callback.dispatch.CallBackAsyncDispatch;
import com.wb.base.callback.service.callback.model.CallBackConfig;
import com.wb.base.callback.service.callback.model.CallBackMessage;
import com.wb.base.callback.worker.utils.CallBackAsyncExecutorUtils;
import com.wb.common.WbJSON;
import com.wb.kafka.WkConsumer;
import com.wb.log.WbLogger;
import com.wb.log.WbLoggerFactory;
import com.wb.mq.MessageQueueConsumer;
import com.wb.mq.config.enums.MessageQueueGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by @author
 */
@Component
public class CallBackWorker implements ApplicationRunner {

    private static final WbLogger LOGGER = WbLoggerFactory.getLogger(CallBackWorker.class);

    @Autowired
    private CallBackAsyncDispatch callBackAsyncDispatch;
    @Autowired
    private BaseCallBackDispatch callBackDispatch;
    @Autowired
    private CallBackMessageDaoRepository callBackMessageDaoRepository;

    @Override
    public void run(ApplicationArguments args) {
        LOGGER.info("callback worker start...");
        //kafka快慢队列
        new WkConsumer(MqTopicEnums.KAFKA_FAST_CALLBACK.getGroupName(), MqTopicEnums.KAFKA_FAST_CALLBACK.getTopic(), value -> callBackAsyncDispatch.asyncCallBack(BackPressureEnums.FAST, WbJSON.fromJson(value, CallBackMessage.class)));
        new WkConsumer(MqTopicEnums.KAFKA_SLOW_CALLBACK.getGroupName(), MqTopicEnums.KAFKA_SLOW_CALLBACK.getTopic(), value -> callBackAsyncDispatch.asyncCallBack(BackPressureEnums.SLOW, WbJSON.fromJson(value, CallBackMessage.class)));

        //rocketMq重试
        new MessageQueueConsumer(MessageQueueGroup.CALL_BACK_RETRY, value -> callBackDispatch.callBack(WbJSON.fromJson(value, CallBackMessage.class)));

        //DB循环修补，1秒查一次
        CallBackAsyncExecutorUtils.submitScheduledTask(() -> {
            Optional.ofNullable(callBackMessageDaoRepository.getCallBackMessageByDb()).ifPresent(entity -> {
                try {
                    LOGGER.info("CallBackWorker dbScheduled callback ! id : {}, messageId : {}", entity.getId(), entity.getMessgeId());
                    callBackAsyncDispatch.asyncCallBack(
                            BackPressureEnums.valueOf(entity.getBackPressure()),
                            CallBackMessage.builder()
                                    .platformType(CallBackPlatformTypeEnums.valueOf(entity.getPlatformType()))
                                    .businessType(entity.getBusinessType())
                                    .messageId(entity.getMessgeId())
                                    .callBackConfig(WbJSON.fromJson(entity.getConfig(), CallBackConfig.class))
                                    .message(WbJSON.fromJson(entity.getData(), Object.class))
                                    .build()
                    );
                } finally {
                    LOGGER.info("CallBackWorker dbScheduled clean ! id : {}, messageId : {}", entity.getId(), entity.getMessgeId());
                    callBackMessageDaoRepository.cleanDbMessageById(entity.getId());
                }
            });
        }, 1, 1, TimeUnit.SECONDS);
    }
}
