package com.backend.service;

import com.backend.dto.CartItemRequest;
import com.backend.dto.CheckoutRequest;
import com.backend.dto.OrderItemResponse;
import com.backend.dto.OrderResponse;
import com.backend.entity.Customer;
import com.backend.entity.Farmer;
import com.backend.entity.Order;
import com.backend.entity.OrderItem;
import com.backend.entity.Product;
import com.backend.exception.ApiException;
import com.backend.repository.CustomerRepository;
import com.backend.repository.FarmerRepository;
import com.backend.repository.OrderItemRepository;
import com.backend.repository.OrderRepository;
import com.backend.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final FarmerRepository farmerRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductRepository productRepository,
                        CustomerRepository customerRepository,
                        FarmerRepository farmerRepository,
                        NotificationService notificationService,
                        EmailService emailService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.farmerRepository = farmerRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    /** Places an order from the cart (check.html). customerId is null for guest checkout. */
    @Transactional
    public OrderResponse checkout(Long customerId, CheckoutRequest req) {
        Order order = new Order();
        if (customerId != null) {
            Customer c = customerRepository.findById(customerId).orElse(null);
            order.setCustomer(c);
        }
        order.setName(req.name);
        order.setPhone(req.phone);
        order.setEmail(req.email);
        order.setAddress(req.address);
        order.setCity(req.city);
        order.setState(req.state);
        order.setPin(req.pin);
        order.setDeliveryType(req.deliveryType);
        order.setDeliveryFee(req.deliveryFee == null ? 0.0 : req.deliveryFee);

        double subtotal = 0.0;

        Map<Long, Farmer> farmerMap = new LinkedHashMap<>();
        Map<Long, List<OrderItem>> farmerItemsMap = new LinkedHashMap<>();

        for (CartItemRequest ci : req.items) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setQuantity(ci.quantity == null ? 1 : ci.quantity);
            item.setUnit(ci.unit);

            if (ci.productId != null) {
                Product product = productRepository.findById(ci.productId).orElse(null);
                if (product != null) {
                    item.setProduct(product);
                    item.setProductName(product.getName());
                    if (product.getFarmer() != null) {
                        Farmer farmer = product.getFarmer();
                        item.setFarmerName(farmer.getFullName());
                        farmerMap.put(farmer.getId(), farmer);
                        farmerItemsMap.computeIfAbsent(farmer.getId(), k -> new ArrayList<>()).add(item);
                    }
                    item.setPrice(product.getPrice());

                    // reduce stock, never below zero
                    int remaining = Math.max(0, product.getQuantity() - item.getQuantity());
                    product.setQuantity(remaining);
                    productRepository.save(product);
                } else {
                    item.setProductName(ci.productName);
                    item.setPrice(ci.price == null ? 0.0 : ci.price);
                }
            } else {
                item.setProductName(ci.productName);
                item.setPrice(ci.price == null ? 0.0 : ci.price);
            }

            subtotal += item.getPrice() * item.getQuantity();
            order.getItems().add(item);
        }

        order.setSubtotal(subtotal);
        order.setTotal(subtotal + order.getDeliveryFee());

        Order savedOrder = orderRepository.save(order);

        // Notify each farmer whose product(s) were ordered via In-App Notification and Email
        for (Map.Entry<Long, List<OrderItem>> entry : farmerItemsMap.entrySet()) {
            Farmer farmer = farmerMap.get(entry.getKey());
            List<OrderItem> itemsForFarmer = entry.getValue();
            if (farmer != null && itemsForFarmer != null && !itemsForFarmer.isEmpty()) {
                double farmerTotal = itemsForFarmer.stream()
                        .mapToDouble(i -> (i.getPrice() != null ? i.getPrice() : 0.0) * (i.getQuantity() != null ? i.getQuantity() : 1))
                        .sum();
                String summary = itemsForFarmer.stream()
                        .map(i -> i.getProductName() + " (" + i.getQuantity() + " " + (i.getUnit() != null ? i.getUnit() : "") + ")")
                        .collect(Collectors.joining(", "));

                String notifTitle = "New Order Received: #ORD" + savedOrder.getId();
                String notifMessage = "Customer " + savedOrder.getName() + " placed an order for: " + summary
                        + ". Total: ₹" + String.format("%.2f", farmerTotal) + ". Please review and accept.";

                try {
                    notificationService.createNotification(farmer, savedOrder, notifTitle, notifMessage, "NEW_ORDER");
                } catch (Exception e) {
                    // Log and proceed so checkout is never blocked
                }

                try {
                    emailService.sendNewOrderEmailToFarmer(farmer, savedOrder, itemsForFarmer);
                } catch (Exception e) {
                    // Log and proceed
                }
            }
        }

        return OrderResponse.from(savedOrder);
    }

    public List<OrderResponse> getCustomerOrders(Long customerId) {
        Customer c = customerRepository.findById(customerId)
                .orElseThrow(() -> new ApiException("Customer not found.", HttpStatus.NOT_FOUND));
        return orderRepository.findByCustomerOrderByCreatedAtDesc(c).stream()
                .map(OrderResponse::from).collect(Collectors.toList());
    }

    /** Orders that include at least one of this farmer's products (farmer/orders.html). */
    public List<OrderItemResponse> getFarmerOrderItems(Long farmerId) {
        return orderItemRepository.findByProduct_Farmer_IdOrderByOrder_CreatedAtDesc(farmerId).stream()
                .map(OrderItemResponse::from)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(OrderResponse::from).collect(Collectors.toList());
    }

    public OrderResponse updateStatus(Long orderId, String status) {
        Order o = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Order not found.", HttpStatus.NOT_FOUND));
        o.setStatus(status);
        orderRepository.save(o);
        return OrderResponse.from(o);
    }

    /** Farmer can update the status of an order only if it contains at least one of their products. */
    public void updateStatusAsFarmer(Long farmerId, Long orderId, String status) {
        Order o = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Order not found.", HttpStatus.NOT_FOUND));
        boolean owns = o.getItems().stream().anyMatch(i ->
                i.getProduct() != null && i.getProduct().getFarmer() != null
                        && i.getProduct().getFarmer().getId().equals(farmerId));
        if (!owns) {
            throw new ApiException("This order does not contain any of your products.", HttpStatus.FORBIDDEN);
        }
        o.setStatus(status);
        orderRepository.save(o);

        // When farmer accepts or changes status, inform customer if email is available
        if ("Confirmed".equalsIgnoreCase(status) || "Accepted".equalsIgnoreCase(status)) {
            Farmer farmer = farmerRepository.findById(farmerId).orElse(null);
            try {
                emailService.sendOrderAcceptedEmailToCustomer(o, farmer);
            } catch (Exception ignored) {}
        }
    }
}
