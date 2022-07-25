package com.wb.base.callback.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by @author 
 */
@SpringBootApplication(scanBasePackages = {"com.wb", "com.wanba"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
