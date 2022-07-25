package com.wb.base.callback.service.domain.infra.repository;

import com.wb.base.callback.constants.CallBackPlatformTypeEnums;
import com.wb.base.callback.service.backpressure.constants.BackPressureEnums;
import com.wb.base.callback.service.callback.model.CallBackConfig;
import com.wb.base.callback.service.callback.model.CallBackMessage;
import com.wb.base.callback.service.exception.CallBackSaveException;

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
