package com.backend.repository;

import com.backend.entity.Customer;
import com.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerOrderByCreatedAtDesc(Customer customer);
    List<Order> findAllByOrderByCreatedAtDesc();
}
