package com.callback.base.worker;

import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.constants.MqTopicEnums;
import com.callback.base.extend.infra.CallBackMessageDaoRepository;
import com.callback.base.service.backpressure.constants.BackPressureEnums;
import com.callback.base.service.callback.dispatch.BaseCallBackDispatch;
import com.callback.base.service.callback.dispatch.CallBackAsyncDispatch;
import com.callback.base.service.callback.model.CallBackConfig;
import com.callback.base.service.callback.model.CallBackMessage;
import com.callback.base.worker.utils.CallBackAsyncExecutorUtils;
import com.l.rpc.json.LJSON;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CallBackWorker implements ApplicationRunner {

    @Autowired
    private CallBackAsyncDispatch callBackAsyncDispatch;
    @Autowired
    private BaseCallBackDispatch callBackDispatch;
    @Autowired
    private CallBackMessageDaoRepository callBackMessageDaoRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("callback worker start...");
        //kafka快慢队列,TODO 引入自己的kafka消费组件
        new MqConsumer(MqTopicEnums.KAFKA_FAST_CALLBACK.getGroupName(), MqTopicEnums.KAFKA_FAST_CALLBACK.getTopic(), value -> callBackAsyncDispatch.asyncCallBack(BackPressureEnums.FAST, LJSON.fromJson(value, CallBackMessage.class)));
        new MqConsumer(MqTopicEnums.KAFKA_SLOW_CALLBACK.getGroupName(), MqTopicEnums.KAFKA_SLOW_CALLBACK.getTopic(), value -> callBackAsyncDispatch.asyncCallBack(BackPressureEnums.SLOW, LJSON.fromJson(value, CallBackMessage.class)));

        //重试 TODO 引入自己的重试消费
        new MqConsumer(MqTopicEnums.CALL_BACK_RETRY, MqTopicEnums.CALL_BACK_RETRY.getTopic(), value -> callBackDispatch.callBack(LJSON.fromJson(value, CallBackMessage.class)));

        //DB循环修补，1秒查一次
        CallBackAsyncExecutorUtils.submitScheduledTask(() -> {
            Optional.ofNullable(callBackMessageDaoRepository.getCallBackMessageByDb()).ifPresent(entity -> {
                try {
                    log.info("CallBackWorker dbScheduled callback ! id : {}, messageId : {}", entity.getId(), entity.getMessgeId());
                    callBackAsyncDispatch.asyncCallBack(
                            BackPressureEnums.valueOf(entity.getBackPressure()),
                            CallBackMessage.builder()
                                    .platformType(CallBackPlatformTypeEnums.valueOf(entity.getPlatformType()))
                                    .businessType(entity.getBusinessType())
                                    .messageId(entity.getMessgeId())
                                    .callBackConfig(LJSON.fromJson(entity.getConfig(), CallBackConfig.class))
                                    .message(LJSON.fromJson(entity.getData(), Object.class))
                                    .build()
                    );
                } finally {
                    log.info("CallBackWorker dbScheduled clean ! id : {}, messageId : {}", entity.getId(), entity.getMessgeId());
                    callBackMessageDaoRepository.cleanDbMessageById(entity.getId());
                }
            });
        }, 1, 1, TimeUnit.SECONDS);
    }
}
