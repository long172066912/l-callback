package com.callback.base.starter.client;

import com.google.common.collect.Table;
import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.sdk.annotations.CallBackConsumerParameter;
import com.callback.base.sdk.client.AbstractCallbackClient;
import com.callback.base.sdk.consumer.CallBackConsumer;
import com.callback.base.sdk.factory.CallBackConsumerFactory;
import com.callback.base.sdk.properties.ConsumerMqTopic;
import com.callback.base.starter.client.provider.DefaultMqProvider;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private WorkerRunner workerRunner;

    public static CallbackClient getInstance() {
        return CallbackClientSingle.getClient();
    }

    /**
     * 启动消费
     * @param consumers
     * @throws Throwable
     */
    public void startConsume(List<CallBackConsumer> consumers) throws Throwable {
        if (CollectionUtils.isEmpty(consumers)) {
            throw new Throwable("未找到可用的 CallBackConsumer ，请实现 CallBackConsumer ，并使用注解 @CallBackConsumerParameter 标注 Consumer [ 平台 | 业务场景 | 回调方式]");
        }
        for (CallBackConsumer consumer : consumers) {
            CallBackConsumerFactory.registerByAnnotation(consumer.getClass().getAnnotation(CallBackConsumerParameter.class), consumer);
        }
        //启动worker
        workerRunner = new WorkerRunner();
        workerRunner.run(CallBackConsumerFactory.getCallBackProperties().getTopics());
    }

    @Override
    public void close() throws IOException {
        workerRunner.close();
    }

    /**
     * 单例
     */
    private static class CallbackClientSingle {

        private static CallbackClient client = new CallbackClient();

        protected static CallbackClient getClient() {
            return client;
        }
    }

    /**
     * 内部MQ worker启动类
     */
    private static class WorkerRunner implements Closeable {

        private List<CallBackMqProvider> mqProviders = new ArrayList<>();

        /**
         * 启动消费者，并将消息通过Consumer执行
         *
         * @param topics
         */
        protected void run(Table<CallBackPlatformTypeEnums, String, ConsumerMqTopic> topics) {
            topics.rowKeySet().stream().forEach(platformType ->
                    topics.row(platformType).entrySet().stream().forEach(entry -> {
                                DefaultMqProvider mqProvider = new DefaultMqProvider(platformType, entry.getKey(), entry.getValue());
                                mqProviders.add(mqProvider);
                                mqProvider.start();
                            }
                    )
            );
        }

        @Override
        public void close() throws IOException {
            mqProviders.stream().forEach(callBackMqProvider -> {
                try {
                    callBackMqProvider.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
