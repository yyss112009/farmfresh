package com.backend.service;

import com.backend.entity.Farmer;
import com.backend.entity.Order;
import com.backend.entity.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@farmfresh.com}")
    private String fromEmail;

    /**
     * Sends an email notification to the farmer when a customer orders their product(s).
     * If JavaMailSender is not configured or throws an error, it logs gracefully without
     * blocking the checkout process.
     */
    public void sendNewOrderEmailToFarmer(Farmer farmer, Order order, List<OrderItem> items) {
        if (farmer == null || farmer.getEmail() == null || farmer.getEmail().isBlank()) {
            log.warn("Farmer or farmer email is missing, skipping email notification.");
            return;
        }

        StringBuilder itemsSummary = new StringBuilder();
        double farmerTotal = 0.0;
        for (OrderItem item : items) {
            double lineTotal = (item.getPrice() != null ? item.getPrice() : 0.0) * (item.getQuantity() != null ? item.getQuantity() : 1);
            farmerTotal += lineTotal;
            itemsSummary.append("  - ").append(item.getProductName())
                    .append(" x ").append(item.getQuantity())
                    .append(" ").append(item.getUnit() != null ? item.getUnit() : "")
                    .append(" = \u20B9").append(String.format("%.2f", lineTotal))
                    .append("\n");
        }

        String subject = "🌾 New Order Received: #ORD" + order.getId() + " - FarmFresh";
        String body = "Hello " + farmer.getFullName() + ",\n\n"
                + "Great news! A customer has just placed an order for your product(s) on FarmFresh.\n\n"
                + "--- ORDER DETAILS ---\n"
                + "Order ID: ORD" + order.getId() + "\n"
                + "Customer Name: " + order.getName() + "\n"
                + "Customer Phone: " + order.getPhone() + "\n"
                + "Customer Email: " + (order.getEmail() != null ? order.getEmail() : "N/A") + "\n"
                + "Delivery Address: " + order.getAddress() + ", " + (order.getCity() != null ? order.getCity() : "") + ", " + (order.getState() != null ? order.getState() : "") + " - " + (order.getPin() != null ? order.getPin() : "") + "\n\n"
                + "--- ITEMS ORDERED ---\n"
                + itemsSummary
                + "Farmer Total: \u20B9" + String.format("%.2f", farmerTotal) + "\n\n"
                + "Status: " + order.getStatus() + "\n\n"
                + "Action Required:\n"
                + "Please log in to your FarmFresh Farmer Dashboard to Accept and process this order:\n"
                + "http://localhost:8080/frontend/farmer/orders.html\n\n"
                + "Thank you,\n"
                + "FarmFresh Team";

        sendEmail(farmer.getEmail(), subject, body);
    }

    /**
     * Sends an email notification to the customer when the farmer accepts their order.
     */
    public void sendOrderAcceptedEmailToCustomer(Order order, Farmer farmer) {
        if (order == null || order.getEmail() == null || order.getEmail().isBlank()) {
            return;
        }

        String farmerName = (farmer != null && farmer.getFullName() != null) ? farmer.getFullName() : "The farmer";
        String subject = "✅ Order #ORD" + order.getId() + " Accepted! - FarmFresh";
        String body = "Hello " + order.getName() + ",\n\n"
                + "Great news! Your order #ORD" + order.getId() + " has been ACCEPTED by " + farmerName + ".\n\n"
                + "The farmer is preparing your fresh farm products for delivery.\n"
                + "Delivery Address: " + order.getAddress() + ", " + order.getCity() + "\n"
                + "Total Amount: \u20B9" + order.getTotal() + "\n\n"
                + "Thank you for supporting our local farmers!\n\n"
                + "FarmFresh Team";

        sendEmail(order.getEmail(), subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        if (mailSender == null) {
            log.info("[SIMULATED EMAIL] To: {} | Subject: {}\n{}", to, subject, body);
            return;
        }

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
            log.info("Email sent successfully to {}", to);
        } catch (Exception ex) {
            log.warn("Could not send email to {}: {}. (Simulated log kept).", to, ex.getMessage());
            log.info("[SIMULATED EMAIL FALLBACK] To: {} | Subject: {}\n{}", to, subject, body);
        }
    }
}
