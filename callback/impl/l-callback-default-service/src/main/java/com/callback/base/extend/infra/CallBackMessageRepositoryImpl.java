package com.callback.base.extend.infra;

import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.constants.MqTopicEnums;
import com.callback.base.extend.infra.mapper.CallBackMessageMapper;
import com.callback.base.extend.infra.model.CallBackMessageDO;
import com.callback.base.service.backpressure.constants.BackPressureEnums;
import com.callback.base.service.callback.model.CallBackConfig;
import com.callback.base.service.callback.model.CallBackMessage;
import com.callback.base.service.domain.infra.repository.CallBackMessageRepository;
import com.callback.base.service.exception.CallBackSaveException;
import com.callback.base.utils.CallBackApolloConfigUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.l.rpc.json.LJSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by @author
 */
@Repository
@Slf4j
public class CallBackMessageRepositoryImpl implements CallBackMessageRepository, CallBackMessageDaoRepository {

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
                    r = MqProducer.send(MqTopicEnums.KAFKA_SLOW_CALLBACK.getTopic(), LJSON.toJson(callBackMessage)).get();
                } catch (Exception e) {
                    log.error("saveCallBackMessage error ! FAST ! callBackMessage : {}", LJSON.toJson(callBackMessage));
                }
                break;
            case FAST:
            default:
                try {
                    r = MqProducer.send(MqTopicEnums.KAFKA_FAST_CALLBACK.getTopic(), LJSON.toJson(callBackMessage)).get();
                } catch (Exception e) {
                    log.error("saveCallBackMessage error ! SLOW ! callBackMessage : {}", LJSON.toJson(callBackMessage));
                }
                break;
        }
        if (!r) {
            log.error("saveCallBackMessage fail ! backPressureEnums : {} , callBackMessage : {}", backPressureEnums, LJSON.toJson(callBackMessage));
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
                            .config(LJSON.toJson(callBackMessage.getCallBackConfig()))
                            .data(LJSON.toJson(callBackMessage.getMessage()))
                            .build()
            );
        } catch (Exception e) {
            log.error("CallBackMessageRepository saveCallBackMessageToDb error ! backPressureEnums : {}, callBackMessage : {}", backPressureEnums, LJSON.toJson(callBackMessage), e);
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
        return localCache.get(platformType.name() + businessType, e -> CallBackApolloConfigUtils.getCallBackConfig(platformType, businessType));
    }
}
