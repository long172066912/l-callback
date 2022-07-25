package com.wb.base.callback.service.backpressure.logic;

import com.wb.base.callback.service.backpressure.constants.BackPressureEnums;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: BackPressureLogic
 * @Description: 背压状态查询相关
 * @date 2022/5/26 4:17 PM
 */
public interface BackPressureLogic {

    /**
     * 查询消息背压状态
     * 多级缓存，local+redis
     *
     * @param configKey
     * @return
     */
    BackPressureEnums getBackPressure(String configKey);
}
