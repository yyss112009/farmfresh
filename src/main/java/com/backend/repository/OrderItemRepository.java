package com.backend.repository;

import com.backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // items belonging to orders that contain at least one product of this farmer
    List<OrderItem> findByProduct_Farmer_IdOrderByOrder_CreatedAtDesc(Long farmerId);

    @Modifying
    @Transactional
    @Query("UPDATE OrderItem oi SET oi.product = null WHERE oi.product.id = :productId")
    void nullifyProductReference(@Param("productId") Long productId);
}
