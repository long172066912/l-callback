package com.wb.base.callback.service.callback.model;

import com.wb.base.callback.model.CallBackType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @Title: CallBackConfig
* @Description: 配置，将根据路由+路径+回到方式进行背压检测
* @author JerryLong
* @date 2022/5/27 2:26 PM
* @version V1.0
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallBackConfig {
    /**
     * 服务路由
     */
    private String route;
    /**
     * 服务路径
     */
    private String path;
    /**
     * MQ方式的topic
     */
    private String topic;
    /**
     * 回调方式
     */
    private CallBackType callBackType;
    /**
     * 是否重试，1重试，0不重试
     */
    private Integer retry;
}
