package com.project.backend.service;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.backend.constants.PaymentResultStatus;
import com.project.backend.dto.AddressDTO;
import com.project.backend.dto.CartProductDTO;
import com.project.backend.dto.ColorAttributeDTO;
import com.project.backend.dto.OrderDTO;
import com.project.backend.dto.OrderedProductDTO;
import com.project.backend.dto.PaymentResultDTO;
import com.project.backend.dto.UserDTO;
import com.project.backend.model.Coupon;
import com.project.backend.model.Order;
import com.project.backend.model.OrderedProduct;
import com.project.backend.model.PaymentResult;
import com.project.backend.model.Product;
import com.project.backend.model.ProductColorAttribute;
import com.project.backend.model.ShippingAddress;
import com.project.backend.model.User;
import com.project.backend.repository.OrderRepository;
import com.project.backend.repository.PaymentRepository;
import com.project.backend.repository.ProductRepository;
import com.project.backend.repository.ShippingAddressRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.security.request.OrderRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderService {

    
    private final OrderRepository orderRepository;
    
    private final UserRepository userRepository;
    
    private final ProductRepository productRepository;

    private final PaymentRepository paymentRepository;

    private final ShippingAddressRepository shippingAddressRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
            ProductRepository productRepository, ShippingAddressRepository shippingAddressRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.paymentRepository = paymentRepository;
        this.shippingAddressRepository = shippingAddressRepository;
    }

    public Order createOrder(OrderRequest request, String username) {

        Optional<User> user = userRepository.findByUserName(username);

        if (user.isPresent()) {
            
            Order order = new Order();

            order.setOrderNumber(request.getOrderNumber());

            List<OrderedProduct> ordered = new ArrayList<>();
                        
            for (CartProductDTO p : request.getProducts()) {

                OrderedProduct op = new OrderedProduct();
                
                op.setName(p.getName());
                op.setColor(ProductColorAttribute.builder()
                .colorId(Long.parseLong(p.getColor().getId()))
                .color(p.getColor().getColor())
                .colorImage(p.getColor().getColorImage()).build());
                op.setImage(p.getImage());
                op.setPrice(p.getPrice());
                op.setQty(p.getQty());
                op.setSize(p.getSize());
                
                Optional<Product> data= productRepository.findById(Long.parseLong(p.getId()));
                if (data.isPresent())
                    op.setProduct(data.get());

                ordered.add(op);
                op.setOrder(order);
            }

            order.setOrderedProducts(ordered);

            order.setPaymentMethod(request.getPaymentMethod());

            PaymentResult pr = PaymentResult.builder()
            .payPrice(request.getTotal())
            .payStatus(PaymentResultStatus.WAITING_FOR_PAYMENT).build();
            
            order.setPaymentResult(pr);

            paymentRepository.save(pr); 
            
            
            
            AddressDTO shippingAddress = request.getShippingAddress();
            ShippingAddress existedAddress = shippingAddressRepository.findById(Long.parseLong(shippingAddress.getId()))
            .orElseThrow(()->new RuntimeException("Shipping address not found"));
            ;
            
            order.setShippingAddress(existedAddress);

                        
            order.setCouponApplied(request.getCouponApplied());

            order.setTotal(request.getTotal());
            order.setTotalBeforeDiscount(request.getTotalBeforeDiscount());

            user.get().getOrderLists().add(order);

            order.setUser(user.get());
            
            Order result = orderRepository.save(order);           

            userRepository.save(user.get());                       

                        
            

            return result;
        }       
        return null;
    }

    public OrderDTO getOrder(String orderId)  {

        Optional<Order> order = orderRepository.findById(Long.parseLong(orderId));

        if (order.isPresent()) {

            Order data = order.get();

            List<OrderedProductDTO> dtos = new ArrayList<>();
            for(OrderedProduct product : data.getOrderedProducts()) {
                
                OrderedProductDTO dto = OrderedProductDTO.builder()
                .color(ColorAttributeDTO.builder()
                        .id(Long.toString(product.getColor().getColorId()))
                        .color(product.getColor().getColor())
                        .colorImage(product.getColor().getColorImage())
                        .build())
                .id(Long.toString(product.getOrderProductId()))
                .image(product.getImage())
                .name(product.getName())
                .price(product.getPrice())
                .qty(product.getQty())
                .build();
                
                dtos.add(dto);
            }

            AddressDTO address = new AddressDTO();
            
            AddressService.deepCopyShippingAddressDTO(address, data.getShippingAddress());

            OrderDTO result = OrderDTO.builder()
            .orderNumber(data.getOrderNumber())
            .isPaid(data.isPaid())
            .couponApplied(data.getCouponApplied())
            .deliveredAt(data.getDeliveredAt())
            .orderStatus(data.getOrderStatus().name())
            .paymentMethod(data.getPaymentMethod())
            .paymentResult(data.getPaymentResult().getPayStatus().getStatus())
            .products(dtos)
            .shippingAddress(address)
            .user(UserDTO.builder()
                    .userId(Long.toString(data.getUser().getUserId()))
                    .username(data.getUser().getUserName())
                    .image(data.getUser().getImage()).build()
             )
             .totalBeforeDiscount(data.getTotalBeforeDiscount())
             .total(data.getTotal())
            .build();
            
            return result;
        }

        return null;
    }

    


}
