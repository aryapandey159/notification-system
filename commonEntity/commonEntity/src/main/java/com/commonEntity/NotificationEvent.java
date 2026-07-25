package com.commonEntity;

public class NotificationEvent {
    private String userId;
    private String email;
    private String eventType;
    private String message;

    public NotificationEvent(String userId, String email, String eventType, String message) {
        this.userId = userId;
        this.email = email;
        this.eventType = eventType;
        this.message = message;
    }

    public NotificationEvent() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
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
