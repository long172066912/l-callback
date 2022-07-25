package com.wb.rpc.server;

import com.wb.rpc.annotations.EnableRpcClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Collections;
import java.util.List;

/**
* @Title: Application
* @Description: 服务启动类
* @author JerryLong
* @date 2021/10/19 11:20 AM
* @version V1.0
*/
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, MongoAutoConfiguration.class}, scanBasePackages = {"com.callback"})
@EnableRpcClient
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private List<HttpMessageConverter<?>> getConverters() {
        return Collections.singletonList(new MappingJackson2HttpMessageConverter());
    }
}
