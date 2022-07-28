package com.callback.base.starter.client;

import java.io.Closeable;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallBackMqProvider
 * @Description: MQ
 * @date 2022/6/1 9:50 AM
 */
public interface CallBackMqProvider extends Closeable {
    /**
     * 启动
     */
    void start();
}
