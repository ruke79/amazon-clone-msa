package com.project.backend.service;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.backend.constants.PaymentResultStatus;
import com.project.backend.constants.StatusMessages;
import com.project.backend.dto.AddressDTO;
import com.project.backend.dto.CartProductDTO;
import com.project.backend.dto.ColorAttributeDTO;
import com.project.backend.dto.OrderDTO;
import com.project.backend.dto.OrderedProductDTO;
import com.project.backend.dto.PaymentResultDTO;
import com.project.backend.dto.UserProfileDTO;
import com.project.backend.model.CartProduct;
import com.project.backend.model.Coupon;
import com.project.backend.model.Order;
import com.project.backend.model.OrderedProduct;
import com.project.backend.model.PaymentResult;
import com.project.backend.model.Product;
import com.project.backend.model.ProductColorAttribute;
import com.project.backend.model.ShippingAddress;
import com.project.backend.model.User;
import com.project.backend.repository.CartProductRepository;
import com.project.backend.repository.OrderRepository;
import com.project.backend.repository.OrderedProductRepository;
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

    private final OrderedProductRepository orderedProductRepository;
    
    private final ProductRepository productRepository;

    private final PaymentRepository paymentRepository;

    private final CartProductRepository cartProductRepository;

    private final ShippingAddressRepository shippingAddressRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
            ProductRepository productRepository, ShippingAddressRepository shippingAddressRepository, PaymentRepository paymentRepository, OrderedProductRepository orderedProductRepository, CartProductRepository cartProductRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderedProductRepository = orderedProductRepository;
        this.productRepository = productRepository;
        this.paymentRepository = paymentRepository;
        this.cartProductRepository = cartProductRepository;
        this.shippingAddressRepository = shippingAddressRepository;
    }

    public Order createOrder(OrderRequest request, String username) {

        Optional<User> user = userRepository.findByUserName(username);

        if (user.isPresent()) {
            


            Order order = new Order();

            order.setOrderNumber(request.getOrderNumber());
            order.setPaymentMethod(request.getPaymentMethod());

            order.setCouponApplied(request.getCouponApplied());

            order.setTotal(request.getTotal());
            order.setTotalBeforeDiscount(request.getTotalBeforeDiscount());

            user.get().getOrderLists().add(order);

            order.setUser(user.get());

            AddressDTO shippingAddress = request.getShippingAddress();
            ShippingAddress existedAddress = shippingAddressRepository.findById(Long.parseLong(shippingAddress.getId()))
            .orElseThrow(()->new RuntimeException("Shipping address not found"));
            ;

            existedAddress.getOrders().add(order);

            
            order.setShippingAddress(existedAddress);
            
                        
            

            PaymentResult pr = PaymentResult.builder()
            .payPrice(request.getTotal())
            .payStatus(PaymentResultStatus.NOT_PROCESSED).build();
           
            
            order.setPaymentResult(pr);
         

            paymentRepository.save(pr);

            List<OrderedProduct> ordered = new ArrayList<>();
                        
            for (CartProductDTO p : request.getProducts()) {

                OrderedProduct op = new OrderedProduct();

                ProductColorAttribute color = ProductColorAttribute.builder()
                .colorId(Long.parseLong(p.getColor().getId()))
                .color(p.getColor().getColor())
                .colorImage(p.getColor().getColorImage()).build();

                //color.getOrderedProducts().add(op);
                
                op.setName(p.getName());
                op.setColor(color);
                op.setImage(p.getImage());
                op.setPrice(p.getPrice());
                op.setQty(p.getQty());
                op.setSize(p.getSize());

                

                CartProduct cartPrdouct = cartProductRepository.findById(Long.parseLong(p.getId()))
                .orElseThrow(()->new RuntimeException("Product not found in cart."));
                
                Product data= productRepository.findById(cartPrdouct.getProduct().getProductId())
                .orElseThrow(()->new RuntimeException("Product not found"));
                                
                op.setProduct(data);            

                data.getOrderedProducts().add(op);
                    
                ordered.add(op);
                op.setOrder(order);
                orderedProductRepository.save(op);

                
                
            }

            order.setOrderedProducts(ordered);

            order.setPaymentResult(pr);
            
            Order result = orderRepository.save(order);  
             


            return result;
        }       
        return null;
    }

    public OrderDTO getOrder(Long orderId, String filter)  {

        Order data = null;

        if(filter.isEmpty()) {
            data = orderRepository.findById(orderId)
            .orElseThrow(()->new RuntimeException("Order not found."));
        }
        else {
            data = orderRepository.findByOrderIdAndPaymentResult_PayStatus(orderId,PaymentResultStatus.getStatus(filter))
            .orElseThrow(()->new RuntimeException("Order not found."));
        }       
        

        if (null != data) {            

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
            .user(UserProfileDTO.builder()
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

    public List<OrderDTO> getOrders(String username, String filter) {

        User user = userRepository.findByUserName(username)
        .orElseThrow(()->new RuntimeException(StatusMessages.USER_NOT_FOUND));

        List<OrderDTO> result = new ArrayList<>();

        if (null != user) {
                List<Order> orders = user.getOrderLists();

                for(Order o : orders) {

                    OrderDTO dto = getOrder(o.getOrderId(), filter);

                    result.add(dto);
                }
        }

        return result;

    }   

}
