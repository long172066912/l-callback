package com.callback.base.service.consumer.mq;

import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.model.CallBackType;
import com.callback.base.model.consumer.CallBackConsumerMessageBO;
import com.callback.base.sdk.annotations.CallBackConsumerParameter;
import com.callback.base.sdk.consumer.CallBackConsumer;
import com.callback.base.service.model.TestMessage;
import com.l.rpc.json.LJSON;

/**
 * @Title: MqTestCallBackConsumer
 * @Description: //TODO (用一句话描述该文件做什么)
 * @author JerryLong
 * @date 2022/6/8 10:02 AM
 * @version V1.0
 */
@CallBackConsumerParameter(
        platformType = CallBackPlatformTypeEnums.TEST,
        businessType = "TEST",
        callbackType = CallBackType.MQ,
        mqGroup = "testGroup",
        mqTopic = "testTopic"
)
public class MqTestCallBackConsumer implements CallBackConsumer<TestMessage> {

    @Override
    public boolean accept(CallBackConsumerMessageBO<TestMessage> message) {
        System.out.println("MqTestCallBackConsumer 消费 ：" + LJSON.toJson(message) + " , status : " + message.getData().getStatus());
        return true;
    }
}
