package com.ey.dynamicrouter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ey.dynamicrouter.dao")
public class DynamicRouterApplication {

	public static void main(String[] args) {
		SpringApplication.run(DynamicRouterApplication.class, args);
	}
}
