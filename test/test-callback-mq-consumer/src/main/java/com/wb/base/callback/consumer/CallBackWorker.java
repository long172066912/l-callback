package com.wb.base.callback.consumer;

import com.wb.base.callback.consumer.consumer.MqTestCallBackConsumer;
import com.wb.base.callback.starter.client.CallbackClient;
import com.wb.log.WbLogger;
import com.wb.log.WbLoggerFactory;
import lombok.SneakyThrows;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Created by @author
 */
@Component
public class CallBackWorker implements ApplicationRunner {

    private static final WbLogger LOGGER = WbLoggerFactory.getLogger(CallBackWorker.class);

    @SneakyThrows
    @Override
    public void run(ApplicationArguments args) {
        LOGGER.info("callback mq consumer start...");
        CallbackClient.getInstance().startConsume(Arrays.asList(new MqTestCallBackConsumer()));
    }
}
