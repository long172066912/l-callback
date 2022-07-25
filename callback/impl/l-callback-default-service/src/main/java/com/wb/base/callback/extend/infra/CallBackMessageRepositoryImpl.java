package com.wb.base.callback.extend.infra;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.wb.base.callback.constants.CallBackPlatformTypeEnums;
import com.wb.base.callback.constants.MqTopicEnums;
import com.wb.base.callback.extend.infra.mapper.CallBackMessageMapper;
import com.wb.base.callback.extend.infra.model.CallBackMessageDO;
import com.wb.base.callback.service.backpressure.constants.BackPressureEnums;
import com.wb.base.callback.service.callback.model.CallBackConfig;
import com.wb.base.callback.service.callback.model.CallBackMessage;
import com.wb.base.callback.service.domain.infra.repository.CallBackMessageRepository;
import com.wb.base.callback.service.exception.CallBackSaveException;
import com.wb.base.callback.utils.CallBackApolloConfigUtils;
import com.wb.common.WbEnv;
import com.wb.common.WbJSON;
import com.wb.kafka.WkProducer;
import com.wb.log.WbLogger;
import com.wb.log.WbLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by @author
 */
@Repository
public class CallBackMessageRepositoryImpl implements CallBackMessageRepository, CallBackMessageDaoRepository {

    private static WbLogger LOGGER = WbLoggerFactory.getLogger(CallBackMessageRepository.class);

    /**
     * 本地缓存，有效期60秒
     */
    public static Cache<String, List<CallBackConfig>> localCache = Caffeine.newBuilder()
            .initialCapacity(10)
            .maximumSize(2000)
            //在写入后开始计时，在指定的时间后过期。
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();

    @Autowired
    private CallBackMessageMapper callBackMessageMapper;

    @Override
    public void saveCallBackMessage(BackPressureEnums backPressureEnums, CallBackMessage callBackMessage) throws CallBackSaveException {
        boolean r = false;
        switch (backPressureEnums) {
            case SLOW:
                try {
                    r = WkProducer.send(MqTopicEnums.KAFKA_SLOW_CALLBACK.getTopic(), WbJSON.toJson(callBackMessage)).get();
                } catch (Exception e) {
                    LOGGER.error("saveCallBackMessage error ! FAST ! callBackMessage : {}", WbJSON.toJson(callBackMessage));
                }
                break;
            case FAST:
            default:
                try {
                    r = WkProducer.send(MqTopicEnums.KAFKA_FAST_CALLBACK.getTopic(), WbJSON.toJson(callBackMessage)).get();
                } catch (Exception e) {
                    LOGGER.error("saveCallBackMessage error ! SLOW ! callBackMessage : {}", WbJSON.toJson(callBackMessage));
                }
                break;
        }
        if (!r) {
            LOGGER.error("saveCallBackMessage fail ! backPressureEnums : {} , callBackMessage : {}", backPressureEnums, WbJSON.toJson(callBackMessage));
            throw new CallBackSaveException();
        }
    }

    @Override
    public void saveCallBackMessageToDb(BackPressureEnums backPressureEnums, CallBackMessage callBackMessage) {
        try {
            callBackMessageMapper.save(
                    CallBackMessageDO.builder()
                            .platformType(callBackMessage.getPlatformType().name())
                            .businessType(callBackMessage.getBusinessType())
                            .backPressure(backPressureEnums.name())
                            .messgeId(callBackMessage.getMessageId())
                            .config(WbJSON.toJson(callBackMessage.getCallBackConfig()))
                            .data(WbJSON.toJson(callBackMessage.getMessage()))
                            .build()
            );
        } catch (Exception e) {
            LOGGER.error("CallBackMessageRepository saveCallBackMessageToDb error ! backPressureEnums : {}, callBackMessage : {}", backPressureEnums, WbJSON.toJson(callBackMessage), e);
        }
    }

    @Override
    public CallBackMessageDO getCallBackMessageByDb() {
        return callBackMessageMapper.selectOne();
    }

    @Override
    public void cleanDbMessageById(long id) {
        callBackMessageMapper.deleteById(id);
    }

    @Override
    public List<CallBackConfig> getConfig(CallBackPlatformTypeEnums platformType, String businessType) {
        if (WbEnv.isOnline() || WbEnv.isTest()) {
            return localCache.get(platformType.name() + businessType, e -> CallBackApolloConfigUtils.getCallBackConfig(platformType, businessType));
        } else {
            return CallBackApolloConfigUtils.getCallBackConfig(platformType, businessType);
        }
    }
}
