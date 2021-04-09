package com.messagebus.httpbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class HttpApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(HttpApplication.class,args);
	}

}
