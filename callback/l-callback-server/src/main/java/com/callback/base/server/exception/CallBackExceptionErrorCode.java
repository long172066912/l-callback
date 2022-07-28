package com.callback.base.server.exception;

import com.l.rpc.exception.CommonErrCode;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: CallBackExceptionErrorCode
 * @Description: //TODO (用一句话描述该文件做什么)
 * @date 2022/5/27 6:21 PM
 */
public enum CallBackExceptionErrorCode implements CommonErrCode {
    /**
     * 无回调配置
     */
    NO_CONFIG(1, "回调失败，无回调配置"),
    /**
     * 回调配置错误
     */
    ERROR_CONFIG(2, "回调失败，配置错误"),
    ;
    /**
     * 错误码，1~9999
     */
    private int code;
    /**
     * 错误信息
     */
    private String msg;

    CallBackExceptionErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}