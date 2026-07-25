package com.notification.notification_service.DTO;

public class NotificationResponse {

    private Long Id;
    private Long userId;
    private String email;
    private String eventType;
    private String message;

    private String kafkaId;

    public String getKafkaId() {
        return kafkaId;
    }

    public void setKafkaId(String kafkaId) {
        this.kafkaId = kafkaId;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
