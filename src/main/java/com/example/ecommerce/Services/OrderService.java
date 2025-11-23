package com.example.ecommerce.Services;

import com.example.ecommerce.payload.OrderDTO;
import com.example.ecommerce.payload.OrderRequestDTO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface OrderService {
    OrderDTO placeOrder(String emailId, String paymentMethod, Long addressId, String pgName, String pgStatus, String pgPaymentId, String pgResponseMessage);
}
