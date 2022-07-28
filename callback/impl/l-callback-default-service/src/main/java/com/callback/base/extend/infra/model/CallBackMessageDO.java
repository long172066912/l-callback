package com.callback.base.extend.infra.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**   
* @Title: CallBackMessageDO 
* @Description: //TODO (用一句话描述该文件做什么)
* @author JerryLong  
* @date 2022/5/30 4:49 PM 
* @version V1.0    
*/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("callback_message")
public class CallBackMessageDO {
    /**
     * 自增id
     */
    private Long id;
    /**
     * 回调平台
     */
    private String platformType;
    /**
     * 回调业务
     */
    private String businessType;
    /**
     * 背压状态
     */
    private String backPressure;
    /**
     * 回调配置
     */
    private String config;
    /**
     * 消息唯一key
     */
    private String messgeId;
    /**
     * 回调内容
     */
    private String data;
}
