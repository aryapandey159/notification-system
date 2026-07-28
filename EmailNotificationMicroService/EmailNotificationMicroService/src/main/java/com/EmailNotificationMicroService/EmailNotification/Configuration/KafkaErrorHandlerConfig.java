package com.EmailNotificationMicroService.EmailNotification.Configuration;

import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.util.backoff.BackOff;

@Configuration

public class KafkaErrorHandlerConfig {
    private static final Logger log = LoggerFactory.getLogger(KafkaErrorHandlerConfig.class);

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {

        // DLT recoverer — failed messages go to "notification-event-topic.DLT" automatically
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, exception) -> {
                    // Log before sending to DLT
                    log.error("All retries exhausted. Sending to DLT." +
                                    " Topic: {}, Partition: {}, Offset: {}, Value: {}, Error: {}",
                            record.topic(),
                            record.partition(),
                            record.offset(),
                            record.value(),
                            exception.getMessage());

                    return new TopicPartition(record.topic() + ".DLT", record.partition());
                });

        // Exponential backoff — 1s, 2s, 4s → then sends to DLT
        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);
        backOff.setInitialInterval(1000L);
        backOff.setMultiplier(2.0);

        return new DefaultErrorHandler(recoverer, backOff);
    }
}
