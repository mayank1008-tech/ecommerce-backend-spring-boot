package com.example.ecommerce.Services;

import com.example.ecommerce.Exceptions.APIexception;
import com.example.ecommerce.Exceptions.ResourceNotFound;
import com.example.ecommerce.Model.*;
import com.example.ecommerce.Repository.*;
import com.example.ecommerce.Util.AuthUtil;
import com.example.ecommerce.payload.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class OrderServiceImple implements OrderService{

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private Orderrepository orderrepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartService cartService;

    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId, String paymentMethod, Long addressId, String pgName, String pgStatus, String pgPaymentId, String pgResponseMessage) {
        Cart cart = cartRepository.findCartByEmail(emailId);
        if(cart==null){
            throw new ResourceNotFound("Cart","emailId",emailId);
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(()->new ResourceNotFound("Address","id",addressId));

        //SETTING ORDER DETAILS
        Order order = new Order();
        order.setEmail(emailId);
        order.setAddress(address);
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus("Order Accepted!");
        order.setTotalAmount(cart.getTotalPrice());

        //SAVING ORDER AND PAYMENT DETAILS
        Payment payment = new Payment(paymentMethod, pgPaymentId,pgStatus, pgResponseMessage, pgName);
        order.setPayment(payment);
        payment.setOrder(order);  //Even though not necessary as order is the owner, but it is a good practice as it tells it in memory of java
        payment = paymentRepository.save(payment);
        Order savedOrder = orderrepository.save(order);

        //ADDING ORDERITEMS VIA CARTITEMS
        List<CartItem> cartItemList = cart.getCartItems();
        if(cartItemList==null){
            throw new APIexception("Cart is Empty1");
        }
        List<OrderItem> orderItemList = new ArrayList<>();
        for(CartItem cartItem:cartItemList){
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setPrice(cartItem.getSpecialPrice());
            orderItem.setShippingPrice(cartItem.getSpecialPrice());
            //SAVING THIS ORDER ITEM IN ORDER
            orderItem.setOrder(savedOrder);
            orderItemList.add(orderItem);
        }
        //SAVING ORDERITEMS
        orderItemList = orderItemRepository.saveAll(orderItemList); //SAVE EVERY ORDER ITEM FROM THAT LIST

        //UPDATING PRODUCT STOCK
        cart.getCartItems().forEach(item->{
            int quantity = item.getQuantity();
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity()-quantity);
            productRepository.saveAndFlush(product);
            cartService.deleteCartProduct(cart.getId(), product.getProductId());
        });

        //Converting in OrderDTO
        PaymentDTO sendPaymentDTO = modelMapper.map(payment,PaymentDTO.class);
        OrderDTO orderDTO = modelMapper.map(savedOrder,OrderDTO.class);


//        //My ORDERDTO DOESN'T CONTAIN RAW LIST OF ORDER ITEMS BUT IT CONTAINS LIST OF ORDERITEMSDTO
//        orderItemList.forEach(item-> orderDTO.getOrderItemDTOList().add(modelMapper.map(item, OrderItemDTO.class)));

        for (OrderItem item : orderItemList) {
            // Map OrderItem -> OrderItemDTO
            OrderItemDTO itemDTO = modelMapper.map(item, OrderItemDTO.class);

            // MANUALLY MAP THE PRODUCT DTO to ensure it is set
            if (item.getProduct() != null) {
                ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                itemDTO.setProductDTO(productDTO); // Assuming your OrderItemDTO has setProductDTO
            }

            orderDTO.getOrderItemDTOList().add(itemDTO);
        }

        orderDTO.setAddressId(addressId);
        orderDTO.setPaymentDTO(sendPaymentDTO);
        return orderDTO;
    }
}
