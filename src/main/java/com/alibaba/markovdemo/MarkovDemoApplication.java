package com.alibaba.markovdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.alibaba.markovdemo.mapper")
public class MarkovDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarkovDemoApplication.class, args);
	}

}
