package com.l.rpc.factory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.l.rpc.annotations.RPCClient;
import com.l.rpc.annotations.RPCMethod;
import com.l.rpc.exception.BaseBusinessException;
import com.l.rpc.exception.SystemCode;
import com.l.rpc.json.LJSON;
import com.l.rpc.model.RpcBaseResponse;
import com.l.rpc.model.BaseResponse;
import com.l.rpc.model.RpcVersion;
import com.l.rpc.utils.okhttp3.OkHttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.Response;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: RpcProxy
 * @Description: Client的代理类，调用会传递到这里触发
 * @date 2022/7/12 2:15 PM
 */
@Slf4j
public class RpcProxy implements InvocationHandler {

    /**
     * 代理类调用在这里触发
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String host = proxy.getClass().getAnnotation(RPCClient.class).value();
        String path = method.getAnnotation(RPCMethod.class).value();
        //TODO 写死请求本地，请修改为开源调用方式，并支持异步
        //处理请求参数
        Object body;
        if (args.length > 1) {
            Map<String, Object> data = new HashMap<>();
            Arrays.stream(args).forEach(param -> {
                data.put(param.getClass().getSimpleName(), param);
            });
            body = data;
        } else if (args.length == 1) {
            body = args[0];
        } else {
            body = null;
        }
        return responseHandle(OkHttpClientUtils.send("http://localhost" + path, LJSON.toJson(body)), method.getReturnType());
    }

    private Object responseHandle(Response response, Class<?> returnType) throws IOException {
        if (null == response) {
            return null;
        }
        //解析header
        final Headers headers = response.headers();
        String code = Optional.ofNullable(headers.get(RpcVersion.RPC_HEADER_ERROR_CD_KEY)).orElse(SystemCode.SUCCESS.name());
        if (RpcBaseResponse.isSuccess(code)) {
            if (Void.class.equals(returnType)) {
                return null;
            }
            try {
                StringBuilder textBuilder = new StringBuilder();
                try (Reader reader = response.body().charStream()) {
                    int c = 0;
                    while ((c = reader.read()) != -1) {
                        textBuilder.append((char) c);
                    }
                }
                TypeReference tr = new TypeReference<Object>() {
                    @Override
                    public Type getType() {
                        return returnType;
                    }
                };
                return LJSON.fromJson(textBuilder.toString(), tr);
            } catch (IOException e) {
                throw e;
            }
        } else {
            String desc = headers.get(RpcVersion.RPC_HEADER_ERROR_DESC_KEY);
            throw BaseBusinessException.business(new RpcResponse(code, desc));
        }
    }

    private static class RpcResponse implements BaseResponse {

        private String cd;
        private String msg;

        public RpcResponse(String cd, String msg) {
            this.cd = cd;
            this.msg = msg;
        }

        @Override
        public boolean isSuccess() {
            return RpcBaseResponse.isSuccess(cd);
        }

        @Override
        public String getCd() {
            return cd;
        }

        @Override
        public String getDesc() {
            return msg;
        }

        @Override
        public Object getData() {
            return null;
        }
    }
}
