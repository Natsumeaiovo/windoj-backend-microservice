package com.serein.windojuserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan({"com.serein.windojuserservice.mapper"})
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.serein")
@EnableDiscoveryClient
public class WindojUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WindojUserServiceApplication.class, args);
    }
}
