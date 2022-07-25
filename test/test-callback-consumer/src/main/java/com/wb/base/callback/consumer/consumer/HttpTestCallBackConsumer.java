package com.wb.base.callback.consumer.consumer;

import com.wb.base.callback.constants.CallBackPlatformTypeEnums;
import com.wb.base.callback.consumer.model.TestMessage;
import com.wb.base.callback.model.CallBackType;
import com.wb.base.callback.model.consumer.CallBackConsumerMessageBO;
import com.wb.base.callback.sdk.annotations.CallBackConsumerParameter;
import com.wb.base.callback.sdk.consumer.CallBackConsumer;
import com.wb.rpc.json.WbJSON;
import org.springframework.stereotype.Service;

/**   
* @Title: HttpTestCallBackConsumer 
* @Description: //TODO (用一句话描述该文件做什么) 
* @author JerryLong  
* @date 2022/6/8 10:09 AM 
* @version V1.0    
*/
@CallBackConsumerParameter(
        platformType = CallBackPlatformTypeEnums.TEST,
        businessType = "TEST_HTTP",
        callbackType = CallBackType.HTTP
)
@Service
public class HttpTestCallBackConsumer implements CallBackConsumer<TestMessage> {

    @Override
    public boolean accept(CallBackConsumerMessageBO<TestMessage> message) {
        System.out.println("HttpTestCallBackConsumer 同步回调 消费 ：" + WbJSON.toJson(message) + " , status : " + message.getData().getStatus());
        return true;
    }
}
