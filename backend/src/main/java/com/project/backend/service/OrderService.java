package com.project.backend.service;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.project.backend.repository.ProductRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.security.request.OrderRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    public Order createOrder(OrderRequest request) {

        Optional<User> user = userRepository.findByEmail(request.getUserId());

        if (user.isPresent()) {

            Order existed = orderRepository.findByUser_Email(request.getUserId());
            if(existed != null)
            {
                orderRepository.delete(existed);
            }

            Order order = new Order();

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

            PaymentResult pr = PaymentResult.builder().build();
            order.setPaymentResult(pr);

            ShippingAddress shippingAddress = new ShippingAddress();
            AddressService.deepCopyShippingAddress(shippingAddress, request.getShippingAddress());
            order.setShippingAddress(shippingAddress);
            
            order.setCouponApplied(request.getCouponApplied());

            order.setTotal(request.getTotal());
            order.setTotalBeforeDiscount(request.getTotalBeforeDiscount());

            order.setUser(user.get());

            return orderRepository.save(order);           
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
            .id(Long.toString(data.getOrderId()))
            .isPaid(data.isPaid())
            .couponApplied(data.getCouponApplied())
            .deliveredAt(data.getDeliveredAt())
            .orderStatus(data.getOrderStatus().name())
            .paymentMethod(data.getPaymentMethod())
            .paymentResult(data.getPaymentResult().getStatus())
            .products(dtos)
            .shippingAddress(address)
            .user(UserDTO.builder()
                    .userId(Long.toString(data.getUser().getUserId()))
                    .userName(data.getUser().getUserName())
                    .image(data.getUser().getImage()).build()
             )
             .totalBeforeDiscount(data.getTotalBeforeDiscount())
             .total(data.getTotal())
            .build();



            log.info("Order returned");
            return result;
        }

        return null;
    }

    public boolean processPayment(String orderId) {

        int updatedRowcount = orderRepository.updateIsPaidById(Long.parseLong(orderId), true);

        if (updatedRowcount > 0) {
            return true;
        }
        return false;
    }


}
