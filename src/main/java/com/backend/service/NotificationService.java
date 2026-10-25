package com.backend.service;

import com.backend.dto.NotificationResponse;
import com.backend.entity.Farmer;
import com.backend.entity.Notification;
import com.backend.entity.Order;
import com.backend.exception.ApiException;
import com.backend.repository.FarmerRepository;
import com.backend.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FarmerRepository farmerRepository;

    public NotificationService(NotificationRepository notificationRepository, FarmerRepository farmerRepository) {
        this.notificationRepository = notificationRepository;
        this.farmerRepository = farmerRepository;
    }

    @Transactional
    public Notification createNotification(Farmer farmer, Order order, String title, String message, String type) {
        Notification notification = new Notification(farmer, order, title, message, type);
        return notificationRepository.save(notification);
    }

    public List<NotificationResponse> getFarmerNotifications(Long farmerId) {
        return notificationRepository.findByFarmer_IdOrderByCreatedAtDesc(farmerId)
                .stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(Long farmerId) {
        return notificationRepository.countByFarmer_IdAndIsReadFalse(farmerId);
    }

    @Transactional
    public void markAsRead(Long farmerId, Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ApiException("Notification not found.", HttpStatus.NOT_FOUND));

        if (!n.getFarmer().getId().equals(farmerId)) {
            throw new ApiException("Access denied to this notification.", HttpStatus.FORBIDDEN);
        }

        n.setRead(true);
        notificationRepository.save(n);
    }

    @Transactional
    public void markAllAsRead(Long farmerId) {
        List<Notification> unread = notificationRepository.findByFarmer_IdAndIsReadFalseOrderByCreatedAtDesc(farmerId);
        for (Notification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }
}
