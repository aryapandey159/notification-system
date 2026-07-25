package com.notification.notification_service.Service;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    NewTopic createTopic(){
        return TopicBuilder.name("notification-event-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
