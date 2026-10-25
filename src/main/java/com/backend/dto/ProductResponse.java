package com.backend.dto;

import com.backend.entity.Product;

/** Flat, no-lazy-loading-surprise view of a Product, safe to send to Jackson. */
public class ProductResponse {
    public Long id;
    public String name;
    public String category;
    public Double price;
    public Integer quantity;
    public String unit;
    public String description;
    public String imageUrl;
    public String status;
    public Long farmerId;
    public String farmerName;
    public String farmName;

    public static ProductResponse from(Product p) {
        ProductResponse r = new ProductResponse();
        r.id = p.getId();
        r.name = p.getName();
        r.category = p.getCategory();
        r.price = p.getPrice();
        r.quantity = p.getQuantity();
        r.unit = p.getUnit();
        r.description = p.getDescription();
        r.imageUrl = p.getImageUrl();
        r.status = p.getStatus();
        if (p.getFarmer() != null) {
            r.farmerId = p.getFarmer().getId();
            r.farmerName = p.getFarmer().getFullName();
            r.farmName = p.getFarmer().getFarmName();
        }
        return r;
    }
}
