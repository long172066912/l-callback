package com.callback.base.service.callback.adapter;

import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.model.CallBackType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallBackAdapterManager
 * @Description: 适配器工厂
 * @date 2022/5/27 6:42 PM
 */
public class CallBackAdapterFactory {
    /**
     * 实现集合
     */
    private static Map<String, BaseCallBackAdapter> map = new ConcurrentHashMap<>();

    public static void register(AbstractCallBackAdapter baseCallBackAdapter) {
        map.put(buildKey(baseCallBackAdapter.getPlatformType(), baseCallBackAdapter.getCallBackType()), baseCallBackAdapter);
    }

    public static BaseCallBackAdapter getAdapter(CallBackPlatformTypeEnums platformType, CallBackType callBackType) {
        return map.get(buildKey(platformType, callBackType));
    }

    private static String buildKey(CallBackPlatformTypeEnums platformType, CallBackType callBackType) {
        return platformType.name() + "_" + callBackType.name();
    }
}
