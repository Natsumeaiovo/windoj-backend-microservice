package com.serein.windojquestionservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan({"com.serein.windojquestionservice.mapper"})
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.serein")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.serein.windojserviceclient.service"})
public class WindojQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WindojQuestionServiceApplication.class, args);
    }

}
