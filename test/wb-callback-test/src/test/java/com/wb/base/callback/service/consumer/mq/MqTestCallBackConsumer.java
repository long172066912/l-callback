package com.wb.base.callback.service.consumer.mq;

import com.wb.base.callback.constants.CallBackPlatformTypeEnums;
import com.wb.base.callback.model.CallBackType;
import com.wb.base.callback.model.consumer.CallBackConsumerMessageBO;
import com.wb.base.callback.sdk.annotations.CallBackConsumerParameter;
import com.wb.base.callback.sdk.consumer.CallBackConsumer;
import com.wb.base.callback.service.model.TestMessage;
import com.wb.common.WbJSON;

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
        System.out.println("MqTestCallBackConsumer 消费 ：" + WbJSON.toJson(message) + " , status : " + message.getData().getStatus());
        return true;
    }
}
