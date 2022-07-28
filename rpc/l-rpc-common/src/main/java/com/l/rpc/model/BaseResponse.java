package com.l.rpc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: BaseResponse
 * @Description: 返回统一接口
 * @date 2021/10/21 5:17 PM
 */
public interface BaseResponse<T> {

    /**
     * 是否成功
     *
     * @return
     */
    @JsonIgnore
    boolean isSuccess();

    /**
     * 获取服务返回状态码
     *
     * @return
     */
    String getCd();

    /**
     * 获取备注信息
     *
     * @return
     */
    String getDesc();

    /**
     * 获取返回结果
     *
     * @return
     */
    T getData();
}
