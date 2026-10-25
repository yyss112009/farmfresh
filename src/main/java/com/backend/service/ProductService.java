package com.backend.service;

import com.backend.dto.ProductRequest;
import com.backend.dto.ProductResponse;
import com.backend.entity.Farmer;
import com.backend.entity.Product;
import com.backend.exception.ApiException;
import com.backend.repository.FarmerRepository;
import com.backend.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.backend.repository.OrderItemRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final FarmerRepository farmerRepository;
    private final OrderItemRepository orderItemRepository;

    public ProductService(ProductRepository productRepository, FarmerRepository farmerRepository,
                          OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.farmerRepository = farmerRepository;
        this.orderItemRepository = orderItemRepository;
    }

    /** Public shop listing - active products only (ourproduct.html). */
    public List<ProductResponse> getActiveProducts() {
        return productRepository.findByStatus("ok").stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getFarmerProducts(Long farmerId) {
        return productRepository.findByFarmerId(farmerId).stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    public ProductResponse addProduct(Long farmerId, ProductRequest req) {
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new ApiException("Farmer not found.", HttpStatus.NOT_FOUND));

        Product p = new Product();
        p.setName(req.getName());
        p.setCategory(req.getCategory());
        p.setPrice(req.getPrice());
        p.setQuantity(req.getQuantity());
        p.setUnit(req.getUnit());
        p.setDescription(req.getDescription());
        p.setImageUrl(req.getImageUrl());
        p.setFarmer(farmer);
        productRepository.save(p);
        return ProductResponse.from(p);
    }

    public ProductResponse updateProduct(Long farmerId, Long productId, ProductRequest req) {
        Product p = findOwnedByFarmer(farmerId, productId);
        p.setName(req.getName());
        p.setCategory(req.getCategory());
        p.setPrice(req.getPrice());
        p.setQuantity(req.getQuantity());
        p.setUnit(req.getUnit());
        if (req.getDescription() != null) p.setDescription(req.getDescription());
        if (req.getImageUrl() != null) p.setImageUrl(req.getImageUrl());
        productRepository.save(p);
        return ProductResponse.from(p);
    }

    @Transactional
    public void deleteProduct(Long farmerId, Long productId) {
        Product p = findOwnedByFarmer(farmerId, productId);
        // Unlink from past order items without losing order history snapshots
        orderItemRepository.nullifyProductReference(productId);
        productRepository.delete(p);
    }

    private Product findOwnedByFarmer(Long farmerId, Long productId) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException("Product not found.", HttpStatus.NOT_FOUND));
        if (!p.getFarmer().getId().equals(farmerId)) {
            throw new ApiException("This product does not belong to you.", HttpStatus.FORBIDDEN);
        }
        return p;
    }

    // ---- admin ----
    public List<ProductResponse> getAllProductsForAdmin() {
        return productRepository.findAll().stream().map(ProductResponse::from).collect(Collectors.toList());
    }

    public ProductResponse updateStatusAsAdmin(Long productId, String status) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException("Product not found.", HttpStatus.NOT_FOUND));
        p.setStatus(status);
        productRepository.save(p);
        return ProductResponse.from(p);
    }
}
