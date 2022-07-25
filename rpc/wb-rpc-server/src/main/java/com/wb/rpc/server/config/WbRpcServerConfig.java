package com.wb.rpc.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**   
* @Title: RpcConfig 
* @Description: 拦截器配置
* @author JerryLong  
* @date 2021/11/22 11:18 AM 
* @version V1.0    
*/
@Configuration
public class WbRpcServerConfig implements WebMvcConfigurer {

    @Resource
    ResponseTypeResolverInterceptor baseInterceptor;
    @Resource
    TraceIdInterceptor traceIdInterceptor;
    @Resource
    AllowFromInterceptor allowFromInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(allowFromInterceptor).addPathPatterns("/**").order(0);
        registry.addInterceptor(baseInterceptor).addPathPatterns("/**").order(1);
        registry.addInterceptor(traceIdInterceptor).addPathPatterns("/**").order(2);
    }
}