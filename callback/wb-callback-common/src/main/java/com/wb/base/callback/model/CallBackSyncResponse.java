package com.wb.base.callback.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @Title: CallBackResponse
* @Description: //TODO (用一句话描述该文件做什么)
* @author JerryLong
* @date 2022/6/6 6:35 PM
* @version V1.0
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CallBackSyncResponse {
    /**
     * 回调业务服务
     */
    private String route;
    /**
     * 回调地址
     */
    private String path;
    /**
     * 回调结果，成功|失败
     */
    private Boolean status;
}
