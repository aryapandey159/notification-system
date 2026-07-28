package com.EmailNotificationMicroService.EmailNotification.Handler;

import com.EmailNotificationMicroService.EmailNotification.Service.EmailService;
import com.commonEntity.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics="notification-event-topic")
public class ProductCreatedEventHandler {

    @Autowired
    EmailService emailService;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @KafkaHandler
    public void handle(NotificationEvent notificationEvent){
        LOGGER.info("Receved a new event: "+ notificationEvent.getEventType()+" "+"mail :"+notificationEvent.getEmail());
        emailService.sendEmail(notificationEvent.getEmail(), notificationEvent.getUserId(),notificationEvent.getMessage());
    }
}