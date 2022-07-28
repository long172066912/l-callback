package com.callback.base.service.domain.impl;

import com.callback.base.constants.CallBackConstants;
import com.callback.base.model.CallBackProtocol;
import com.callback.base.model.CallBackSyncResponse;
import com.callback.base.model.CallBackType;
import com.callback.base.service.backpressure.constants.BackPressureEnums;
import com.callback.base.service.backpressure.logic.BackPressureLogic;
import com.callback.base.service.callback.dispatch.handle.CallBackHandler;
import com.callback.base.service.callback.dispatch.handle.model.CallBackStatus;
import com.callback.base.service.callback.model.CallBackConfig;
import com.callback.base.service.callback.model.CallBackMessage;
import com.callback.base.service.domain.CallBackMessageService;
import com.callback.base.service.domain.infra.repository.CallBackMessageRepository;
import com.callback.base.service.exception.CallBackSaveException;
import com.callback.base.service.exception.ErrorConfigException;
import com.callback.base.service.exception.NoConfigException;
import com.callback.base.service.utils.CallBackConfigUtils;
import com.l.rpc.json.LJSON;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by @author
 */
@Service
@Slf4j
public class CallBackMessageServiceImpl implements CallBackMessageService {

    @Autowired
    private CallBackMessageRepository callBackMessageRepository;
    @Autowired
    private BackPressureLogic backPressureLogic;
    @Autowired
    private CallBackHandler callBackHandler;

    /**
     * 同步回调线程池
     */
    private static ExecutorService syncCallBackExecutorService = ExecutorServiceMetrics.monitor(
            Metrics.globalRegistry,
            new ThreadPoolExecutor(10, 100, 30, TimeUnit.SECONDS, new SynchronousQueue<>(), (r) -> new Thread(r, "回调服务同步回调线程池"), new ThreadPoolExecutor.CallerRunsPolicy())
            , "callback_syncCallBackExecutorService");

    @Override
    public void saveCallBackMessage(CallBackProtocol protocol, Object data) throws NoConfigException {
        this.getCallBackConfigs(protocol).stream().forEach(config -> {
            if (CallBackType.HTTP.equals(config.getCallBackType()) && StringUtils.isBlank(config.getPath())) {
                config.setPath(CallBackConstants.DEFAULT_HTTP_PATH);
            }
            if (null == config.getRetry()) {
                config.setRetry(1);
            }
            if (CallBackType.MQ.equals(config.getCallBackType()) && StringUtils.isBlank(config.getTopic())) {
                config.setTopic(CallBackConstants.DEFAULT_TOPIC_PREFIX + protocol.getPlatformType().name() + "_" + protocol.getBusinessType());
            }
            //查询背压状态
            BackPressureEnums backPressure = backPressureLogic.getBackPressure(CallBackConfigUtils.getCallBackConfigKey(config));
            //构建消息
            CallBackMessage message = CallBackMessage.builder()
                    .callBackConfig(config)
                    .platformType(protocol.getPlatformType())
                    .businessType(protocol.getBusinessType())
                    .messageId(protocol.getMessageId())
                    .message(data)
                    .build();
            log.info("CallBackMessageService saveCallBackMessage ! message : {}", LJSON.toJson(message));
            try {
                callBackMessageRepository.saveCallBackMessage(backPressure, message);
            } catch (CallBackSaveException e) {
                log.warn("CallBackMessageService saveCallBackMessage fail ! protocal : {}, message : {}", LJSON.toJson(protocol), LJSON.toJson(data), e);
                callBackMessageRepository.saveCallBackMessageToDb(backPressure, message);
            }
        });
    }

    @Override
    public List<CallBackSyncResponse> callBackSync(CallBackProtocol protocol, Object data) throws NoConfigException, ErrorConfigException {
        //检测是否有非http方式的
        for (CallBackConfig callBackConfig : this.getCallBackConfigs(protocol)) {
            if (!CallBackType.HTTP.equals(callBackConfig.getCallBackType())) {
                throw new ErrorConfigException();
            }
        }
        return this.getCallBackConfigs(protocol).stream().map(config -> CompletableFuture.supplyAsync(() -> {
            try {
                //构建消息
                CallBackStatus handleRes = callBackHandler.handle(CallBackMessage.builder()
                        .callBackConfig(config)
                        .platformType(protocol.getPlatformType())
                        .businessType(protocol.getBusinessType())
                        .messageId(protocol.getMessageId())
                        .message(data)
                        .build());
                return CallBackSyncResponse.builder().route(config.getRoute()).path(config.getPath()).status(CallBackStatus.SUCCESS.equals(handleRes) ? true : false).build();
            } catch (Exception e) {
                log.error("FriendUserUtil getFriendOnlineState error ! messageId : {} , config : {} , protocol : {} , message : {}", protocol.getMessageId(), LJSON.toJson(config), LJSON.toJson(protocol), LJSON.toJson(data), e);
            }
            return CallBackSyncResponse.builder().route(config.getRoute()).path(config.getPath()).status(false).build();
        }, syncCallBackExecutorService)).collect(Collectors.toList()).stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    /**
     * 获取配置列表
     *
     * @param protocol
     * @return
     * @throws NoConfigException
     */
    private List<CallBackConfig> getCallBackConfigs(CallBackProtocol protocol) throws NoConfigException {
        //获取配置
        List<CallBackConfig> configs = callBackMessageRepository.getConfig(protocol.getPlatformType(), protocol.getBusinessType());
        if (CollectionUtils.isEmpty(configs)) {
            log.warn("CallBackMessageService saveCallBackMessage fail ! configs is empty ! platformType:{},businessType:{}", protocol.getPlatformType(), protocol.getBusinessType());
            throw new NoConfigException();
        }
        return configs;
    }
}
