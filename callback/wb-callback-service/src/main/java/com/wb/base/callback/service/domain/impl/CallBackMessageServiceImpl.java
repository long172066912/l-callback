package com.wb.base.callback.service.domain.impl;

import com.wb.base.callback.constants.CallBackConstants;
import com.wb.base.callback.model.CallBackProtocol;
import com.wb.base.callback.model.CallBackSyncResponse;
import com.wb.base.callback.model.CallBackType;
import com.wb.base.callback.service.backpressure.constants.BackPressureEnums;
import com.wb.base.callback.service.backpressure.logic.BackPressureLogic;
import com.wb.base.callback.service.callback.dispatch.handle.CallBackHandler;
import com.wb.base.callback.service.callback.dispatch.handle.model.CallBackStatus;
import com.wb.base.callback.service.callback.model.CallBackConfig;
import com.wb.base.callback.service.callback.model.CallBackMessage;
import com.wb.base.callback.service.domain.CallBackMessageService;
import com.wb.base.callback.service.domain.infra.repository.CallBackMessageRepository;
import com.wb.base.callback.service.exception.CallBackSaveException;
import com.wb.base.callback.service.exception.ErrorConfigException;
import com.wb.base.callback.service.exception.NoConfigException;
import com.wb.base.callback.service.utils.CallBackConfigUtils;
import com.wb.base.component.monitor.client.WbMonitor;
import com.wb.common.WbJSON;
import com.wb.log.WbLogger;
import com.wb.log.WbLoggerFactory;
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
public class CallBackMessageServiceImpl implements CallBackMessageService {

    private static WbLogger LOGGER = WbLoggerFactory.getLogger(CallBackMessageService.class);

    @Autowired
    private CallBackMessageRepository callBackMessageRepository;
    @Autowired
    private BackPressureLogic backPressureLogic;
    @Autowired
    private CallBackHandler callBackHandler;

    /**
     * 同步回调线程池
     */
    private static ExecutorService syncCallBackExecutorService = WbMonitor.getMonitorExecutorService(
            "callback_sync_executor_service",
            new ThreadPoolExecutor(10, 100, 30, TimeUnit.SECONDS, new SynchronousQueue<>(), (r) -> new Thread(r, "回调服务同步回调线程池"), new ThreadPoolExecutor.CallerRunsPolicy())
    );

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
            LOGGER.info("CallBackMessageService saveCallBackMessage ! message : {}", WbJSON.toJson(message));
            try {
                callBackMessageRepository.saveCallBackMessage(backPressure, message);
            } catch (CallBackSaveException e) {
                LOGGER.warn("CallBackMessageService saveCallBackMessage fail ! protocal : {}, message : {}", WbJSON.toJson(protocol), WbJSON.toJson(data), e);
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
                LOGGER.error("FriendUserUtil getFriendOnlineState error ! messageId : {} , config : {} , protocol : {} , message : {}", protocol.getMessageId(), WbJSON.toJson(config), WbJSON.toJson(protocol), WbJSON.toJson(data), e);
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
            LOGGER.warn("CallBackMessageService saveCallBackMessage fail ! configs is empty ! platformType:{},businessType:{}", protocol.getPlatformType(), protocol.getBusinessType());
            throw new NoConfigException();
        }
        return configs;
    }
}
