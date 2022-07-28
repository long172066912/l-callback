package com.callback.base.model;

/**
* @Title: CallBackType
* @Description: 回调方式枚举
* @author JerryLong
* @date 2022/5/27 2:28 PM
* @version V1.0
*/
public enum CallBackType {
    /**
     * HTTP方式回调，默认
     */
    HTTP,
    /**
     * mq方式回调
     */
    MQ,
    ;
}
