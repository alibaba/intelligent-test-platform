package com.alibaba.markovdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
@MapperScan("com.alibaba.markovdemo.mapper")
public class MarkovDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarkovDemoApplication.class, args);
    }

}
