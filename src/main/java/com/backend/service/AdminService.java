package com.backend.service;

import com.backend.dto.*;
import com.backend.dto.*;
import com.backend.entity.Customer;
import com.backend.entity.Farmer;
import com.backend.entity.Order;
import com.backend.entity.Product;
import com.backend.repository.CustomerRepository;
import com.backend.repository.FarmerRepository;
import com.backend.repository.OrderRepository;
import com.backend.repository.ProductRepository;
import com.backend.dto.*;
import com.backend.entity.*;
import com.backend.exception.ApiException;
import com.backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final CustomerRepository customerRepository;
    private final FarmerRepository farmerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public AdminService(CustomerRepository customerRepository, FarmerRepository farmerRepository,
                         ProductRepository productRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.farmerRepository = farmerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public DashboardStats getStats() {
        DashboardStats stats = new DashboardStats();
        stats.farmers = farmerRepository.count();
        stats.customers = customerRepository.count();
        List<Order> orders = orderRepository.findAll();
        stats.orders = orders.size();
        stats.revenue = orders.stream().mapToDouble(o -> o.getTotal() == null ? 0.0 : o.getTotal()).sum();
        return stats;
    }

    public List<AdminFarmerRow> getFarmerRows() {
        return farmerRepository.findAll().stream().map(f -> {
            AdminFarmerRow row = new AdminFarmerRow();
            row.id = f.getId();
            row.name = f.getFullName();
            row.village = f.getCity() != null ? f.getCity() : f.getFarmName();
            List<Product> products = productRepository.findByFarmerId(f.getId());
            row.products = products.size();
            row.totalSales = orderRepository.findAll().stream()
                    .flatMap(o -> o.getItems().stream())
                    .filter(i -> i.getProduct() != null && i.getProduct().getFarmer() != null
                            && i.getProduct().getFarmer().getId().equals(f.getId()))
                    .mapToDouble(i -> i.getPrice() * i.getQuantity())
                    .sum();
            row.status = f.getStatus();
            return row;
        }).collect(Collectors.toList());
    }

    public FarmerResponse updateFarmerStatus(Long farmerId, String status) {
        Farmer f = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new ApiException("Farmer not found.", HttpStatus.NOT_FOUND));
        f.setStatus(status);
        farmerRepository.save(f);
        return FarmerResponse.from(f);
    }

    public List<AdminCustomerRow> getCustomerRows() {
        List<Order> allOrders = orderRepository.findAll();
        return customerRepository.findAll().stream().map(c -> {
            AdminCustomerRow row = new AdminCustomerRow();
            row.id = c.getId();
            row.name = c.getFullName();
            row.city = c.getCity() != null ? c.getCity() : c.getState();
            List<Order> customerOrders = allOrders.stream()
                    .filter(o -> o.getCustomer() != null && o.getCustomer().getId().equals(c.getId()))
                    .collect(Collectors.toList());
            row.orders = customerOrders.size();
            row.totalSpent = customerOrders.stream().mapToDouble(o -> o.getTotal() == null ? 0.0 : o.getTotal()).sum();
            row.status = c.getStatus();
            return row;
        }).collect(Collectors.toList());
    }

    public void updateCustomerStatus(Long customerId, String status) {
        Customer c = customerRepository.findById(customerId)
                .orElseThrow(() -> new ApiException("Customer not found.", HttpStatus.NOT_FOUND));
        c.setStatus(status);
        customerRepository.save(c);
    }

    public List<AdminOrderRow> getOrderRows() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream().map(o -> {
            AdminOrderRow row = new AdminOrderRow();
            row.id = o.getId();
            row.customerName = o.getName() != null && !o.getName().isBlank() ? o.getName()
                    : (o.getCustomer() != null ? o.getCustomer().getFullName() : "Guest Customer");
            row.phone = o.getPhone();
            row.email = o.getEmail();
            row.address = o.getAddress();
            row.city = o.getCity();
            row.state = o.getState();
            row.pin = o.getPin();
            row.deliveryType = o.getDeliveryType();
            row.deliveryFee = o.getDeliveryFee();
            row.subtotal = o.getSubtotal();
            row.farmerName = o.getItems().isEmpty() || o.getItems().get(0).getFarmerName() == null
                    ? "-" : o.getItems().get(0).getFarmerName();
            row.amount = o.getTotal() == null ? 0.0 : o.getTotal();
            row.status = o.getStatus();
            row.createdAt = o.getCreatedAt() != null ? o.getCreatedAt().toString() : "";
            row.items = o.getItems().stream().map(OrderItemResponse::from).collect(Collectors.toList());
            return row;
        }).collect(Collectors.toList());
    }
}
