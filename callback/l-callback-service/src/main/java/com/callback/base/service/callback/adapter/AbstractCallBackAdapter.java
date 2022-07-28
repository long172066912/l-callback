package com.callback.base.service.callback.adapter;

import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.model.CallBackType;

import javax.annotation.PostConstruct;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: AbstractCallBackAdapter
 * @Description: //TODO (用一句话描述该文件做什么)
 * @date 2022/5/27 6:46 PM
 */
public abstract class AbstractCallBackAdapter implements BaseCallBackAdapter {

    @PostConstruct
    public void init() {
        CallBackAdapterFactory.register(this);
    }

    /**
     * 平台
     * @return
     */
    public abstract CallBackPlatformTypeEnums getPlatformType();

    /**
     * 回调方式
     * @return
     */
    public abstract CallBackType getCallBackType();
}
