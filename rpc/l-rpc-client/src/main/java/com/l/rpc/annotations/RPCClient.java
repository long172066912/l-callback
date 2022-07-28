package com.l.rpc.annotations;


import java.lang.annotation.*;

/**   
* @Title: RPCClient 
* @Description: //TODO (用一句话描述该文件做什么) 
* @author JerryLong  
* @date 2022/7/25 9:47 AM
* @version V1.0    
*/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RPCClient {

    /**
     * 域名标识
     * @return
     */
    String value();
}