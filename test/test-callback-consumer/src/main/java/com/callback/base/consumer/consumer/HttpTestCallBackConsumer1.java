package com.callback.base.consumer.consumer;

import com.callback.base.consumer.model.TestMessage;
import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.model.CallBackType;
import com.callback.base.model.consumer.CallBackConsumerMessageBO;
import com.callback.base.sdk.annotations.CallBackConsumerParameter;
import com.callback.base.sdk.consumer.CallBackConsumer;
import com.l.rpc.json.LJSON;
import org.springframework.stereotype.Service;

/**   
* @Title: HttpTestCallBackConsumer1 
* @Description: //TODO (用一句话描述该文件做什么) 
* @author JerryLong  
* @date 2022/6/10 10:14 AM
* @version V1.0    
*/
@CallBackConsumerParameter(
        platformType = CallBackPlatformTypeEnums.TEST,
        businessType = "TEST",
        callbackType = CallBackType.HTTP
)
@Service
public class HttpTestCallBackConsumer1 implements CallBackConsumer<TestMessage> {

    @Override
    public boolean accept(CallBackConsumerMessageBO<TestMessage> message) {
        System.out.println("HttpTestCallBackConsumer 同步回调 消费 ：" + LJSON.toJson(message) + " , status : " + message.getData().getStatus());
        return true;
    }
}
