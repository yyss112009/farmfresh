package com.backend.repository;

import com.backend.entity.Farmer;
import com.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatus(String status);
    List<Product> findByFarmer(Farmer farmer);
    List<Product> findByFarmerId(Long farmerId);
}
