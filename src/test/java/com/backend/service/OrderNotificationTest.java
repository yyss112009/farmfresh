package com.backend.service;

import com.backend.dto.CartItemRequest;
import com.backend.dto.CheckoutRequest;
import com.backend.dto.NotificationResponse;
import com.backend.dto.OrderResponse;
import com.backend.entity.*;
import com.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderNotificationTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private FarmerRepository farmerRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private EmailService emailService;

    private NotificationService notificationService;
    private OrderService orderService;

    private Farmer testFarmer;
    private Product testProduct;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationRepository, farmerRepository);
        orderService = new OrderService(
                orderRepository,
                orderItemRepository,
                productRepository,
                customerRepository,
                farmerRepository,
                notificationService,
                emailService
        );

        testFarmer = new Farmer();
        testFarmer.setId(10L);
        testFarmer.setFullName("Ramesh Patil");
        testFarmer.setEmail("ramesh@farmfresh.test");

        testProduct = new Product();
        testProduct.setId(101L);
        testProduct.setName("Organic Tomatoes");
        testProduct.setPrice(40.0);
        testProduct.setQuantity(50);
        testProduct.setUnit("kg");
        testProduct.setFarmer(testFarmer);

        testCustomer = new Customer();
        testCustomer.setId(20L);
        testCustomer.setFullName("Sunita Sharma");
        testCustomer.setEmail("sunita@gmail.test");
    }

    @Test
    void testCheckoutSendsNotificationAndEmailToFarmer() {
        CheckoutRequest req = new CheckoutRequest();
        req.name = "Sunita Sharma";
        req.phone = "9876543210";
        req.email = "sunita@gmail.test";
        req.address = "123 Green Valley";
        req.city = "Pune";
        req.state = "Maharashtra";
        req.pin = "411001";
        req.deliveryType = "Standard Delivery";
        req.deliveryFee = 20.0;

        CartItemRequest cartItem = new CartItemRequest();
        cartItem.productId = 101L;
        cartItem.quantity = 3;
        cartItem.unit = "kg";
        req.items = Collections.singletonList(cartItem);

        when(customerRepository.findById(20L)).thenReturn(Optional.of(testCustomer));
        when(productRepository.findById(101L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(555L);
            return o;
        });

        OrderResponse res = orderService.checkout(20L, req);

        assertNotNull(res);
        assertEquals(555L, res.id);
        assertEquals(120.0, res.subtotal); // 3 * 40
        assertEquals(140.0, res.total); // 120 + 20

        // Verify In-App Notification was created and saved for the farmer
        ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notifCaptor.capture());
        Notification savedNotif = notifCaptor.getValue();
        assertEquals(testFarmer, savedNotif.getFarmer());
        assertEquals("NEW_ORDER", savedNotif.getType());
        assertTrue(savedNotif.getTitle().contains("555"));
        assertTrue(savedNotif.getMessage().contains("Sunita Sharma"));

        // Verify Email was sent to farmer
        verify(emailService).sendNewOrderEmailToFarmer(eq(testFarmer), any(Order.class), anyList());
    }

    @Test
    void testFarmerCanAcceptOrder() {
        Order order = new Order();
        order.setId(555L);
        order.setName("Sunita Sharma");
        order.setEmail("sunita@gmail.test");
        order.setStatus("Pending");

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(testProduct);
        item.setPrice(40.0);
        item.setQuantity(2);
        order.getItems().add(item);

        when(orderRepository.findById(555L)).thenReturn(Optional.of(order));
        when(farmerRepository.findById(10L)).thenReturn(Optional.of(testFarmer));

        // Farmer accepts the order (status: "Confirmed")
        orderService.updateStatusAsFarmer(10L, 555L, "Confirmed");

        assertEquals("Confirmed", order.getStatus());
        verify(orderRepository).save(order);

        // Verify customer received confirmation email
        verify(emailService).sendOrderAcceptedEmailToCustomer(order, testFarmer);
    }

    @Test
    void testNotificationServiceOperations() {
        Notification n = new Notification(testFarmer, null, "Test Title", "Test Message", "NEW_ORDER");
        n.setId(1L);
        when(notificationRepository.findByFarmer_IdOrderByCreatedAtDesc(10L))
                .thenReturn(Collections.singletonList(n));
        when(notificationRepository.countByFarmer_IdAndIsReadFalse(10L)).thenReturn(1L);

        List<NotificationResponse> list = notificationService.getFarmerNotifications(10L);
        assertEquals(1, list.size());
        assertEquals("Test Title", list.get(0).title);

        long unread = notificationService.getUnreadCount(10L);
        assertEquals(1L, unread);

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));
        notificationService.markAsRead(10L, 1L);
        assertTrue(n.isRead());
        verify(notificationRepository).save(n);
    }
}
