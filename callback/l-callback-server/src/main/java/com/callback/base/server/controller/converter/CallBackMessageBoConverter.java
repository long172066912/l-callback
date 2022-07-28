package com.callback.base.server.controller.converter;

import com.callback.base.model.CallBackMessageBO;
import com.callback.base.rpc.client.model.CallBackMessageDTO;

/**
* @Title: CallBackMessageBoConverter
* @Description: //TODO (用一句话描述该文件做什么)
* @author JerryLong
* @date 2022/5/26 2:57 PM
* @version V1.0
*/
public class CallBackMessageBoConverter {

    public static CallBackMessageBO convert(CallBackMessageDTO dto) {
        return CallBackMessageBO.builder().protocol(dto.getProtocol()).data(dto.getData()).build();
    }
}
