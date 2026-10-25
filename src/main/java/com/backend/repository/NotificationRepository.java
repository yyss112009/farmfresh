package com.backend.repository;

import com.backend.entity.Farmer;
import com.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByFarmerOrderByCreatedAtDesc(Farmer farmer);

    List<Notification> findByFarmer_IdOrderByCreatedAtDesc(Long farmerId);

    List<Notification> findByFarmer_IdAndIsReadFalseOrderByCreatedAtDesc(Long farmerId);

    long countByFarmer_IdAndIsReadFalse(Long farmerId);
}
