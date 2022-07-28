package com.callback.base.consumer.controller;

import com.callback.base.constants.CallBackPlatformTypeEnums;
import com.callback.base.consumer.model.TestMessage;
import com.callback.base.sdk.consumer.CallBackConsumer;
import com.callback.base.starter.client.CallbackClient;
import com.l.rpc.json.LJSON;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by @author
 */
@RestController
@RequestMapping("/callback/consumer")
public class TestConsumerController {

    @PostMapping("/test")
    public String test(@Validated @RequestBody String param) {
        return CallbackClient.getInstance().callAndConsume(
                () -> this.post("http://localhost:8083/callback/producer/doHandle", "test"),
                CallBackPlatformTypeEnums.TEST,
                "TEST",
                (CallBackConsumer<TestMessage>) message -> {
                    System.out.println("HTTP消费 ： " + LJSON.toJson(message) + " , 平台 ： " + message.getPlatformType() + " , 业务场景 ： " + message.getBusinessType() + " , 消息实体 ： " + LJSON.toJson(message.getData()));
                    return true;
                }
        );
    }

    public String post(String strUrl, String params) {
        BufferedReader reader = null;
        try {
            // 创建连接
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setReadTimeout(2000);
            // 设置请求方式
            connection.setRequestMethod("POST");
            // 设置发送数据的格式
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            //一定要用BufferedReader 来接收响应， 使用字节来接收响应的方法是接收不到内容的
            // utf-8编码
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            out.append(params);
            out.flush();
            out.close();
            // 读取响应
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            String res = "";
            while ((line = reader.readLine()) != null) {
                res += line;
            }
            reader.close();
            return res;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 自定义错误信息
        return "error";
    }
}
