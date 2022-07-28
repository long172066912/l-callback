package com.l.rpc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.l.rpc.exception.BaseBusinessException;
import com.l.rpc.exception.CommonErrCode;
import com.l.rpc.exception.SystemCode;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RpcBaseResponse
 * @Description: 平台统一返回包装
 * @date 2021/9/27 2:23 PM
 */
@Builder
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RpcBaseResponse<T> implements BaseResponse {
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
        return StringUtils.isNotBlank(cd) && (SystemCode.SUCCESS.name().equals(cd) || "0".equals(cd));
    }

    public RpcBaseResponse(SystemCode commonCode, T data) {
        this.cd = commonCode.name();
        this.desc = commonCode.getDesc();
        this.data = data;
    }

    public RpcBaseResponse(SystemCode commonCode, String desc, T data) {
        this.cd = commonCode.name();
        this.desc = desc;
        this.data = data;
    }

    public RpcBaseResponse(String serverErrorCodePrefix, BaseBusinessException errorCode, T data) {
        this.cd = serverErrorCodePrefix + errorCode.getCode();
        this.desc = errorCode.getDesc();
        this.data = data;
    }

    public RpcBaseResponse(String serverErrorCodePrefix, CommonErrCode errorCode, T data) {
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
    public static <T extends Object> RpcBaseResponse<T> success(T data) {
        return new RpcBaseResponse(SystemCode.SUCCESS, data);
    }

    /**
     * 返回失败信息，为什么会执行这个？
     *
     * @param commonCode
     * @param data
     * @return
     */
    public static <T extends Object> RpcBaseResponse<T> fail(SystemCode commonCode, T data) {
        return new RpcBaseResponse(commonCode, data);
    }

    /**
     * 返回失败信息，为什么会执行这个？
     *
     * @param commonCode
     * @param data
     * @return
     */
    public static <T extends Object> RpcBaseResponse<T> fail(SystemCode commonCode, String desc, T data) {
        return new RpcBaseResponse(commonCode, desc, data);
    }

    /**
     * 返回业务错误信息
     *
     * @param errorCode
     * @return
     */
    public static <T extends Object> RpcBaseResponse<T> fail(String serverErrorCodePrefix, BaseBusinessException errorCode) {
        return new RpcBaseResponse(serverErrorCodePrefix, errorCode, null);
    }

    /**
     * 返回业务错误信息
     *
     * @param serverErrorCodePrefix
     * @param errCode
     * @return
     */
    public static Object fail(String serverErrorCodePrefix, CommonErrCode errCode) {
        return new RpcBaseResponse(serverErrorCodePrefix, errCode, null);
    }
}
