package com.serein.windojjudgeservice;

import com.serein.windojjudgeservice.rabbitmq.InitRabbitMq;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.serein")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.serein.windojserviceclient.service"})
public class WindojJudgeServiceApplication {

    public static void main(String[] args) {
        // 初始化消息队列
        InitRabbitMq.doInit();
        SpringApplication.run(WindojJudgeServiceApplication.class, args);
    }

}
