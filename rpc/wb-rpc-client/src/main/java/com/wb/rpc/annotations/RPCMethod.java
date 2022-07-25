package com.wb.rpc.annotations;


import java.lang.annotation.*;

/**
* @Title: RPCMethod
* @Description: //TODO (用一句话描述该文件做什么)
* @author JerryLong
* @date 2022/7/25 9:36 AM
* @version V1.0
*/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RPCMethod {
    /**
     * 请求路径，例如 /user/info
     */
    String value();
}