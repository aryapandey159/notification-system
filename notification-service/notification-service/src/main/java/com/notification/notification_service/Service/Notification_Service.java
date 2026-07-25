package com.notification.notification_service.Service;
import com.notification.notification_service.DTO.NotificationRequest;
import com.notification.notification_service.Entity.NotificationHistory;
import com.notification.notification_service.Repository.Notification_Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.commonEntity.NotificationEvent;

@Service
public class Notification_Service {

    @Autowired
    Notification_Repository notificationRepository;

    KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    public Notification_Service(KafkaTemplate<String, NotificationEvent> kafkaTemplate){
        this.kafkaTemplate= kafkaTemplate;
    }
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public String saveToDb(NotificationRequest notificationRequest){
        String userId= UUID.randomUUID().toString();
        //Store in DB
        NotificationHistory notificationHistory = new NotificationHistory();
        notificationHistory.setUserId(notificationRequest.getUserId());
        notificationHistory.setEmail(notificationRequest.getEmail());
        notificationHistory.setEventType(notificationRequest.getEventType());
        notificationHistory.setMessage(notificationRequest.getMessage());

        notificationHistory.setStatus("SUCCESS");
        LocalDateTime currentDateTime = LocalDateTime.now();
        notificationHistory.setCreatedAt(currentDateTime);

        notificationRepository.save(notificationHistory);

        NotificationEvent notificationEvent= new NotificationEvent(userId, notificationRequest.getEmail(), notificationRequest.getEventType(),notificationRequest.getMessage());
        LOGGER.info("******* Before publishing a NotificationCreatedEvent");
        CompletableFuture<SendResult<String,NotificationEvent>> future = kafkaTemplate.send("notification-event-topic",userId,notificationEvent);
        future.whenComplete((result,exception)->{
            if (exception != null) {
                LOGGER.error("********* Failed to send message: " + exception.getMessage());
                LOGGER.info("Partition "+ result.getRecordMetadata());
                LOGGER.info("Topic: "+ result.getRecordMetadata().topic());
                LOGGER.info("Offset: "+ result.getRecordMetadata().offset());
            } else {
                LOGGER.info("********** Message sent successsfully: " + result.getRecordMetadata());
            }
        });
        future.join(); //We have added this to make our request synchronus
        return userId;



    }
    public Long getID(NotificationRequest notificationRequest){
        NotificationHistory nh=  notificationRepository.findByUserId(notificationRequest.getUserId());
        return nh.getId();
    }
    public NotificationHistory getById(Long id){
        return notificationRepository.findById(id).orElse(new NotificationHistory());
    }
    public List<NotificationHistory> getAllEntities(){
        return notificationRepository.findAll();
    }
}
