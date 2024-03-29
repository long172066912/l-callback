package com.callback.base.starter.client;

import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.sdk.client.AbstractCallbackClient;
import com.callback.base.sdk.consumer.CallBackConsumer;
import com.callback.base.sdk.factory.CallBackConsumerFactory;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallbackClient
 * @Description: 回调客户端
 * @date 2022/5/31 10:26 AM
 */
public class CallbackClient extends AbstractCallbackClient {

    private CallbackClient() {
    }

    public static CallbackClient getInstance() {
        return CallbackClientSingle.getClient();
    }

    private static class CallbackClientSingle {
        private static CallbackClient client = new CallbackClient();

        public static CallbackClient getClient() {
            return client;
        }
    }

    /**
     * 支持动态注入消费逻辑
     *
     * @param supplier 调用逻辑
     * @param platformType 回调平台
     * @param businessType 回调业务场景
     * @param consumer 回调逻辑
     * @param <T>
     * @param <R>
     * @return
     */
    public <T, R> R callAndConsume(Supplier<R> supplier, CallBackPlatformTypeEnums platformType, String businessType, CallBackConsumer<T> consumer) {
        //将回调消费根据平台+业务场景注册到工厂
        CallBackConsumerFactory.regsiter(platformType, businessType, consumer);
        return supplier.get();
    }

    @Override
    public void close() throws IOException {

    }
}
