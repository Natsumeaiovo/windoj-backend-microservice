package com.serein.windojjudgeservice.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: serein
 * @date: 2025/2/25 11:27
 * @description: 用于创建测试程序用到的交换机和队列（只用在程序启动前执行一次）
 */
@Slf4j
public class InitRabbitMq {

    public static void doInit() {
        try {
            // 创建一个新的 ConnectionFactory 实例，用于配置和创建与 RabbitMQ 服务器的连接
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            // 使用 ConnectionFactory 创建一个新的连接到 RabbitMQ 服务器的连接。
            Connection connection = factory.newConnection();
            // 通过连接创建一个新的通道（Channel）。通道是用于与 RabbitMQ 服务器进行通信的虚拟连接。
            Channel channel = connection.createChannel();
            String EXCHANGE_NAME = "code_exchange";
            // 声明一个名为 code_exchange 的交换机，类型为 direct。如果交换机不存在，则会创建它。
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            // 声明一个名为 code_queue 的队列。如果队列不存在，则会创建它。参数解释：
            // true：队列是否持久化。
            // false：队列是否独占。
            // false：队列是否在不使用时自动删除。
            // null：队列的其他参数
            String queueName = "code_queue";
            channel.queueDeclare(queueName, true, false, false, null);
            // 将队列 code_queue 绑定到交换机 code_exchange，并指定路由键 my_routingKey。
            channel.queueBind(queueName, EXCHANGE_NAME, "my_routingKey");
            log.info("rabbit mq启动成功！");
        } catch (Exception e) {
            log.error("启动rabbit mq失败！", e);
        }
    }

    public static void main(String[] args) {
        doInit();
    }
}
