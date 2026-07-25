package com.notification.notification_service.Controller;

import com.notification.notification_service.DTO.NotificationRequest;
import com.notification.notification_service.DTO.NotificationResponse;
import com.notification.notification_service.Entity.NotificationHistory;
import com.notification.notification_service.Service.Notification_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Restcontroller {

    @Autowired
    Notification_Service notificationService;


    @PostMapping("/notifications")
    public NotificationResponse setNotification(@RequestBody NotificationRequest notificationRequest){
        String userId= notificationService.saveToDb(notificationRequest);
        NotificationResponse notificationResponse= new NotificationResponse();
        Long id = notificationService.getID(notificationRequest);
        notificationResponse.setId(id);
        notificationResponse.setUserId(notificationRequest.getUserId());
        notificationResponse.setEmail(notificationRequest.getEmail());
        notificationResponse.setEventType(notificationRequest.getEventType());
        notificationResponse.setMessage(notificationRequest.getMessage());
        notificationResponse.setKafkaId(userId);

        return notificationResponse;
    }
    @GetMapping("/notifications/{id}")
    public NotificationResponse getNotificationWithId(@PathVariable Long id){
        NotificationHistory nh = notificationService.getById(id);
        NotificationResponse notificationResponse= new NotificationResponse();
        notificationResponse.setId(id);
        notificationResponse.setUserId(nh.getUserId());
        notificationResponse.setEmail(nh.getEmail());
        notificationResponse.setEventType(nh.getEventType());
        notificationResponse.setMessage(nh.getMessage());

        return notificationResponse;
    }
    @GetMapping("/notifications")
    public List<NotificationHistory> getAll(){
        return notificationService.getAllEntities();
    }
}
