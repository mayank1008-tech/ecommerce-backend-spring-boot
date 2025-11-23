package com.example.ecommerce.Controller;

import com.example.ecommerce.Services.OrderService;
import com.example.ecommerce.Util.AuthUtil;
import com.example.ecommerce.payload.OrderDTO;
import com.example.ecommerce.payload.OrderRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderRequestDTO orderRequestDTO,
                                                @PathVariable String paymentMethod) {
        String emailId = authUtil.loggedInEmail();
        OrderDTO orderDTO = orderService.placeOrder(
                emailId,
                paymentMethod,
                orderRequestDTO.getAddressId(),
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgResponseMessage()
        );
        return ResponseEntity.ok(orderDTO);
    }
}
