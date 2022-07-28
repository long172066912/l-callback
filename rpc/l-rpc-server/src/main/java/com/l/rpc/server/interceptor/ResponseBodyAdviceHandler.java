package com.l.rpc.server.interceptor;

import com.l.rpc.exception.SystemCode;
import com.l.rpc.model.RpcBaseResponse;
import com.l.rpc.model.RpcVersion;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: ResponseBodyAdviceHandler
 * @Description: 返回拦截
 * @date 2021/10/13 2:09 PM
 */
@ControllerAdvice
public class ResponseBodyAdviceHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof RpcBaseResponse) {
            return body;
        }
        if (CollectionUtils.isEmpty(response.getHeaders().get(RpcVersion.RPC_HEADER_ERROR_CD_KEY))) {
            response.getHeaders().add(RpcVersion.RPC_HEADER_ERROR_CD_KEY, SystemCode.SUCCESS.name());
        }
        return body;
    }
}
