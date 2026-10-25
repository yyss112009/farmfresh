package com.backend.dto;

import com.backend.entity.Notification;
import java.time.LocalDateTime;

public class NotificationResponse {
    public Long id;
    public Long orderId;
    public String title;
    public String message;
    public String type;
    public boolean isRead;
    public LocalDateTime createdAt;

    public static NotificationResponse from(Notification n) {
        NotificationResponse r = new NotificationResponse();
        r.id = n.getId();
        r.orderId = n.getOrder() != null ? n.getOrder().getId() : null;
        r.title = n.getTitle();
        r.message = n.getMessage();
        r.type = n.getType();
        r.isRead = n.isRead();
        r.createdAt = n.getCreatedAt();
        return r;
    }
}
