package com.l.rpc.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.l.rpc.model.BaseResponse;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: OptaErrorCode
 * @Description: 业务统一异常
 * @date 2021/3/4 11:12 AM
 */
public class BaseBusinessException extends RuntimeException {
    /**
     * 错误码标识
     */
    private String code;
    /**
     * 提示信息
     */
    private String desc;
    /**
     * 异常数值长度
     */
    private static final Integer CODE_LENGTH = 4;

    private BaseBusinessException(String code, String desc, Object data) {
        super("code : " + code + " desc : " + desc + (null != data ? " data : " + data : ""));
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 构建业务异常
     *
     * @param errorCode
     * @return
     */
    public static BaseBusinessException business(CommonErrCode errorCode) {
        return new BaseBusinessException(getErrorCode(errorCode.getCode()), errorCode.getMsg(), null);
    }

    /**
     * 构建业务异常
     *
     * @param errorCode
     * @param data
     * @return
     */
    public static BaseBusinessException business(CommonErrCode errorCode, Object data) {
        return new BaseBusinessException(getErrorCode(errorCode.getCode()), errorCode.getMsg(), data);
    }

    /**
     * 构建RPC返回的业务异常
     *
     * @param response
     * @return
     */
    public static BaseBusinessException business(BaseResponse response) {
        return new BaseBusinessException(response.getCd(), response.getDesc(), response.getData());
    }

    /**
     * 构建系统异常
     *
     * @param errorCode
     * @return
     */
    public static BaseBusinessException system(SystemCode errorCode) {
        return new BaseBusinessException(errorCode.name(), errorCode.getDesc(), null);
    }

    /**
     * 判断是否是业务异常
     *
     * @return
     */
    @JsonIgnore
    public boolean isBussinessException() {
        return isBussinessException(this.code);
    }

    /**
     * 判断是否是业务异常
     *
     * @param code
     * @return
     */
    @JsonIgnore
    public static boolean isBussinessException(String code) {
        return !SystemCode.isSystemCode(code);
    }

    /**
     * 获取错误码
     *
     * @param code
     * @return
     */
    private static String getErrorCode(int code) {
        //判断code长度是否有4位，不足前面补0
        return polishingCode(code, CODE_LENGTH);
    }

    /**
     * 长度补齐
     *
     * @param code
     * @param length
     * @return
     */
    private static String polishingCode(int code, int length) {
        StringBuffer newCode = new StringBuffer();
        newCode.append(code);
        if (newCode.length() > length) {
            return newCode.toString();
        }
        int i = length - newCode.length();
        //自动补齐
        if (i > 0) {
            for (int j = 0; j < i; j++) {
                newCode.insert(0, "0");
            }
        }
        return newCode.toString();
    }
}
