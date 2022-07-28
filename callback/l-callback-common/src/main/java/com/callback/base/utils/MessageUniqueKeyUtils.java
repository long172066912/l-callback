package com.callback.base.utils;

import java.util.UUID;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: MessageUniqueKeyUtils
 * @Description: 消息唯一key生成帮助类
 * @date 2022/5/25 4:52 PM
 */
public class MessageUniqueKeyUtils {

    /**
     * 创建消息唯一key
     *
     * @return
     */
    public static String createMessageId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
