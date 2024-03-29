package com.callback.base.service.domain.infra.repository;

import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.service.backpressure.constants.BackPressureEnums;
import com.callback.base.service.callback.model.CallBackConfig;
import com.callback.base.service.callback.model.CallBackMessage;
import com.callback.base.service.exception.CallBackSaveException;

import java.util.List;

/**
 * Created by @author
 */
public interface CallBackMessageRepository {
    /**
     * 中转存储回调数据
     * @param backPressureEnums
     * @param callBackMessage
     * @throws CallBackSaveException
     */
    void saveCallBackMessage(BackPressureEnums backPressureEnums, CallBackMessage callBackMessage) throws CallBackSaveException;

    /**
     * DB持久化
     *
     * @param backPressureEnums
     * @param callBackMessage
     */
    void saveCallBackMessageToDb(BackPressureEnums backPressureEnums, CallBackMessage callBackMessage);

    /**
     * 获取配置，每个业务至少回调一个地址
     *
     * @param platformType
     * @param businessType
     * @return
     */
    List<CallBackConfig> getConfig(CallBackPlatformTypeEnums platformType, String businessType);
}
