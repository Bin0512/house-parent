package com.jike.myhouse;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@MapperScan("com.jike.myhouse.biz.mapper")
@EnableAsync
public class HouseWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(HouseWebApplication.class, args);
	}
}
