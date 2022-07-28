package com.callback.base.consumer;

import com.callback.base.consumer.consumer.MqTestCallBackConsumer;
import com.callback.base.starter.client.CallbackClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Created by @author
 */
@Component
@Slf4j
public class CallBackWorker implements ApplicationRunner {

    @SneakyThrows
    @Override
    public void run(ApplicationArguments args) {
        log.info("callback mq consumer start...");
        CallbackClient.getInstance().startConsume(Arrays.asList(new MqTestCallBackConsumer()));
    }
}
