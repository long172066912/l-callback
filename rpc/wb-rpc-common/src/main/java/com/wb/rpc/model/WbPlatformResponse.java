package com.wb.rpc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wb.common.exception.WbCommonErrCode;
import com.wb.common.exception.WbPlatformException;
import com.wb.common.exception.WbPlatformSystemCode;
import com.wb.rpc.exception.WbPlatformSystemCode;
import lombok.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: WbPlatformResponse
 * @Description: 玩吧平台统一返回包装
 * @date 2021/9/27 2:23 PM
 */
@Builder
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WbPlatformResponse<T> implements WbBaseResponse {
    /**
     * 状态码
     */
    private String cd;
    /**
     * 提示信息
     */
    private String desc;
    /**
     * 返回对象
     */
    private T data;

    /**
     * 判断是否成功
     *
     * @return
     */
    @JsonIgnore
    @Override
    public boolean isSuccess() {
        return isSuccess(this.cd);
    }

    @JsonIgnore
    public static boolean isSuccess(String cd) {
        return StringUtils.isNotBlank(cd) && (WbPlatformSystemCode.SUCCESS.name().equals(cd) || "0".equals(cd));
    }

    public WbPlatformResponse(WbPlatformSystemCode commonCode, T data) {
        this.cd = commonCode.name();
        this.desc = commonCode.getDesc();
        this.data = data;
    }

    public WbPlatformResponse(WbPlatformSystemCode commonCode, String desc, T data) {
        this.cd = commonCode.name();
        this.desc = desc;
        this.data = data;
    }

    public WbPlatformResponse(String serverErrorCodePrefix, WbPlatformException errorCode, T data) {
        this.cd = serverErrorCodePrefix + errorCode.getCode();
        this.desc = errorCode.getDesc();
        this.data = data;
    }

    public WbPlatformResponse(String serverErrorCodePrefix, WbCommonErrCode errorCode, T data) {
        this.cd = serverErrorCodePrefix + errorCode.getCode();
        this.desc = errorCode.getMsg();
        this.data = data;
    }

    /**
     * 返回成功信息
     *
     * @param data 总得返个什么
     * @return
     */
    public static <T extends Object> WbPlatformResponse<T> success(T data) {
        return new WbPlatformResponse(WbPlatformSystemCode.SUCCESS, data);
    }

    /**
     * 返回失败信息，为什么会执行这个？
     *
     * @param commonCode
     * @param data
     * @return
     */
    public static <T extends Object> WbPlatformResponse<T> fail(WbPlatformSystemCode commonCode, T data) {
        return new WbPlatformResponse(commonCode, data);
    }

    /**
     * 返回失败信息，为什么会执行这个？
     *
     * @param commonCode
     * @param data
     * @return
     */
    public static <T extends Object> WbPlatformResponse<T> fail(WbPlatformSystemCode commonCode, String desc, T data) {
        return new WbPlatformResponse(commonCode, desc, data);
    }

    /**
     * 返回业务错误信息
     *
     * @param errorCode
     * @return
     */
    public static <T extends Object> WbPlatformResponse<T> fail(String serverErrorCodePrefix, WbPlatformException errorCode) {
        return new WbPlatformResponse(serverErrorCodePrefix, errorCode, errorCode.getData());
    }

    /**
     * 返回业务错误信息
     * @param serverErrorCodePrefix
     * @param errCode
     * @return
     */
    public static Object fail(String serverErrorCodePrefix, WbCommonErrCode errCode) {
        return new WbPlatformResponse(serverErrorCodePrefix, errCode, null);
    }

    @Override
    public String toString() {
        return "WbPlatformResponse{" +
                "cd='" + cd + '\'' +
                ", desc='" + desc + '\'' +
                ", data=" + data +
                '}';
    }
}
