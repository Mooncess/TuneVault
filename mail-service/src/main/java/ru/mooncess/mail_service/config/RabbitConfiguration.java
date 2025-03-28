package ru.mooncess.mail_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Value("${queue.name}")
    private String queueName;
    @Bean
    public Queue queue() {
        return new Queue(queueName);
    }
}
