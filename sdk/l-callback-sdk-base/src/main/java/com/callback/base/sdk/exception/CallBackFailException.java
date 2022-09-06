package com.callback.base.sdk.exception;

/**
* @Title: CallBackFailException
* @Description: 回调失败，必须异常捕获
* @author JerryLong
* @date 2022/5/31 10:29 AM
* @version V1.0
*/
public class CallBackFailException extends Throwable {
    public CallBackFailException() {
    }
    public CallBackFailException(String message) {
        super(message);
    }
}
