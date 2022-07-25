package com.wb.rpc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: WbBaseResponse
 * @Description: 玩吧返回统一接口
 * @date 2021/10/21 5:17 PM
 */
public interface WbBaseResponse<T> {

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
