package com.l.rpc.annotations;


import com.l.rpc.spring.RpcInitRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: EnableRpcClient
 * @Description: RPC接口注解，使用此注解标注在InterfaceClient类上，即可进行远程方法调用
 * @date 2022/7/4 1:44 PM
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RpcInitRegistrar.class)
public @interface EnableRpcClient {
}