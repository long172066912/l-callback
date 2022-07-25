package com.wb.rpc.utils.okhttp3;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpConnectionPoolMetrics;
import okhttp3.*;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author JerryLong
 * @version V1.0
 * @Title: OkHttp3Client
 * @Description: OKHttp3客户端封装，支持POST请求的同步异步，自动监控请求与连接池
 * @date 2022/6/17 2:50 PM
 */
public class OkHttpClientUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(OkHttpClientUtils.class);

    private static final OkHttpClient HTTP_CLIENT = OkHttpClientObject.CLIENT.getClientInstance();
    private static MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    /**
     * 发送同步post请求
     *
     * @param uri
     * @param data
     */
    public static Response send(String uri, String data) throws IOException {
        Call call = buildPostCall(uri, data);
        return call.execute();
    }

    /**
     * 发送post请求，异步有返回
     *
     * @param uri
     * @param data
     * @return
     */
    public static Future<Response> sendAsync(String uri, String data) {
        Call call = buildPostCall(uri, data);
        CompletableFuture<Response> future = new CompletableFuture<>();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LOGGER.warn("OkHttp3Client send error !", e);
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                future.complete(response);
                response.close();
            }
        });
        return future;
    }

    /**
     * 发送post请求，异步无返回
     *
     * @param uri
     * @param data
     */
    public static void sendAsyncNoResponse(String uri, String data) {
        Call call = buildPostCall(uri, data);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LOGGER.warn("OkHttp3Client send error !", e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    LOGGER.error("OkHttpUtils sendAsyncNoResponse fail ! response : {}", response.toString());
                }
                response.close();
            }
        });
    }

    /**
     * 构建Call对象
     *
     * @param uri
     * @param data
     * @return
     */
    private static Call buildPostCall(String uri, String data) {
        return HTTP_CLIENT.newCall(new Request.Builder().url(uri).post(RequestBody.create(mediaType, data)).build());
    }

    /**
     * 通过枚举创建连接池
     */
    private enum OkHttpClientObject {
        /**
         * 客户端
         */
        CLIENT;
        /**
         * 单例
         */
        private OkHttpClient clientInstance;
        /**
         * 连接超时时间
         */
        private Integer connectTimeoutTime = 10;
        /**
         * 请求超时时间
         */
        private Integer writeTimeoutTime = 10;
        /**
         * 读取超时时间
         */
        private Integer readTimeoutTime = 30;

        OkHttpClientObject() {
            //设置连接池，并监控
            int maxIdleConnections = 200;
            ConnectionPool connectionPool = new ConnectionPool(maxIdleConnections, 5, TimeUnit.MINUTES);
            OkHttpConnectionPoolMetrics okHttpConnectionPoolMetrics = new OkHttpConnectionPoolMetrics(connectionPool, "okhttp.pool", Collections.EMPTY_LIST, maxIdleConnections);
            okHttpConnectionPoolMetrics.bindTo(Metrics.globalRegistry);
            clientInstance = new OkHttpClient.Builder()
                    .connectTimeout(connectTimeoutTime, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeoutTime, TimeUnit.SECONDS)
                    .readTimeout(readTimeoutTime, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .connectionPool(connectionPool)
                    .eventListener(WbOkHttp3MetricsEventListener.builder(Metrics.globalRegistry, "okHttpRequests").build())
                    .build();
        }

        public OkHttpClient getClientInstance() {
            return clientInstance;
        }
    }
}