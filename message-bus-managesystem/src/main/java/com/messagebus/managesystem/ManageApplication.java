package com.messagebus.managesystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.messagebus.managesystem.module.mapper")
public class ManageApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ManageApplication.class,args);
	}

}
