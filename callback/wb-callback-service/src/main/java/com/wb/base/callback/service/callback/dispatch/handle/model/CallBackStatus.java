package com.wb.base.callback.service.callback.dispatch.handle.model;

/**
* @Title: RetryStatus
* @Description: 回调结果
* @author JerryLong
* @date 2022/5/26 3:41 PM
* @version V1.0
*/
public enum CallBackStatus {
    /**
     * 成功
     */
    SUCCESS,
    /**
     * 失败
     */
    FAIL,
    /**
     * 失败，不重试
     */
    FAIL_NO_RETRY,
    /**
     * 失败，重试结束
     */
    FAIL_RETRY_OVER
    ;
}
