package com.callback.base.starter.client;

import com.callback.base.sdk.client.AbstractCallbackClient;

import java.io.IOException;

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

    @Override
    public void close() throws IOException {
    }
}
