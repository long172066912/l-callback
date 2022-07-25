package com.wb.base.callback.extend.callback.dispatch.handle;

import com.wb.base.callback.service.callback.dispatch.handle.CallBackFailHandler;
import com.wb.base.callback.service.callback.model.CallBackMessage;
import org.springframework.stereotype.Service;

/**
* @Title: CallBackFailHandlerImpl
* @Description: 玩吧CallBack失败实现
* @author JerryLong
* @date 2022/5/26 5:07 PM
* @version V1.0
*/
@Service
public class CallBackFailHandlerImpl implements CallBackFailHandler {

    @Override
    public void handle(CallBackMessage callBackMessage) {

    }
}
