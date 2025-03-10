package com.project.order_service.service;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common.constants.OrderStatus;
import com.project.common.constants.PaymentStatus;
import com.project.common.constants.PaymentType;
import com.project.common.dto.CartProductDto;
import com.project.common.dto.ProductColorDto;
import com.project.common.dto.SharedUserDto;
import com.project.common.dto.request.PaypalPaymentRequest;
import com.project.common.message.dto.request.PaymentRequest;
import com.project.common.outbox.OutboxStatus;
import com.project.order_service.client.CartServiceClient;
import com.project.order_service.client.CatalogServiceClient;
import com.project.order_service.client.UserServiceClient;

import com.project.order_service.dto.OrderAddressDto;
import com.project.order_service.dto.OrderDto;
import com.project.order_service.dto.OrderedProductDto;
import com.project.order_service.dto.request.OrderRequest;
import com.project.order_service.model.Customer;
import com.project.order_service.model.Order;
import com.project.order_service.model.OrderAddress;
import com.project.order_service.model.OrderProduct;
import com.project.order_service.outbox.PaymentOutboxHelper;
import com.project.order_service.repository.CustomerRepository;
import com.project.order_service.repository.OrderAddressRepository;
import com.project.order_service.repository.OrderRepository;
import com.project.order_service.repository.OrderProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    
    private final UserServiceClient userServiceClient;

    private final PaymentOutboxHelper paymentOutboxHelper;

    private final CatalogServiceClient catalogServiceClient;
    private final CartServiceClient cartServiceClient;
    
    private final OrderProductRepository orderedProductRepository;
    private final OrderAddressRepository orderAddressRepository;

    @Transactional(readOnly = true)
    public Long getCustomerIdByEmail(String email) {

        Optional<Customer> customer = customerRepository.findByEmail(email);

        if (customer.isPresent()) {
            return customer.get().getId();
        }
        return null;
    }

    @Transactional(readOnly = true) 
    public List<Order> getOrdersByCustomerId(Long customerId) {

        List<Order> orders = orderRepository.findByCustomerId(customerId).orElse(null);

        return orders;

    }

    
    
    
    @Transactional
    public Order createOrder(OrderRequest request) {

        Long customerId = getCustomerIdByEmail(request.getEmail());
        
        if (null != customerId) {

            Order order = new Order();

            order.setTrackingId(UUID.randomUUID().toString());
            order.setPaymentMethod(request.getPaymentMethod());
            order.setCouponApplied(request.getCouponApplied());
            order.setTotal(request.getTotal());
            order.setTotalBeforeDiscount(request.getTotalBeforeDiscount());
            order.setOrderStatus(OrderStatus.NOT_PROCESSED);

            //user.getOrderLists().add(order.get);
            
            order.setCustomerId(customerId);

            
            // AddressDto shippingAddress = 
            // ShippingAddress existedAddress = shippingAddressRepository.findById(Long.parseLong(shippingAddress.getId()))
            // .orElseThrow(()->new RuntimeException("Shipping address not found"));
            // ;

            // existedAddress.getOrders().add(order);

            
            
            //order.setShippingAddressId(Long.parseLong(request.getShippingAddressId()));
            
            // PaymentResult pr = PaymentResult.builder()
            // .payPrice(request.getTotal())
            // .payStatus(PaymentResultStatus.NOT_PROCESSED).build();           
           // order.setPaymentResult(pr);
            //paymentRepository.save(pr);

            List<OrderProduct> ordered = new ArrayList<>();
                        
            for (CartProductDto p : request.getProducts()) {

                OrderProduct op = new OrderProduct();

                // ProductColorAttribute color = ProductColorAttribute.builder()
                // .colorId(Long.parseLong(p.getColor().getId()))
                // .color(p.getColor().getColor())
                // .colorImage(p.getColor().getColorImage()).build();

                //color.getOrderedProducts().add(op);
                
                op.setName(p.getName());
                op.setColorId(Long.parseLong(p.getColor().getId()));
                op.setImage(p.getImage());
                op.setPrice(p.getPrice());
                op.setQty(p.getQty());
                op.setSize(p.getSize());

                

                // CartProduct cartPrdouct = cartProductRepository.findById(Long.parseLong(p.getId()))
                // .orElseThrow(()->new RuntimeException("Product not found in cart."));
                
                // Product data= productRepository.findById(cartPrdouct.getProduct().getProductId())
                // .orElseThrow(()->new RuntimeException("Product not found"));
                                
                // op.setProduct(data);            
                
                String productId = cartServiceClient.getProductId(p.getId());
                

                op.setProductId(Long.parseLong(productId));

                //data.getOrderedProducts().add(op);
                    
                ordered.add(op);
                op.setOrder(order);
                // orderedProductRepository.save(op);                
                
            }

            order.setOrderedProducts(ordered);

            //order.setPaymentResult(pr);
            
            Order result = orderRepository.save(order);  
            
            OrderAddress address = new OrderAddress();
            OrderAddress.deepCopyShippingAddress(address, request.getShippingAddress());

            order.setShippingAddress(address);

            address.setOrder(order);

            orderAddressRepository.save(address);


            return result;
        }       
        return null;
    }

    public OrderDto getOrder(Long orderId, String email, String filter)  {

        Order data = null;

        ObjectMapper mapper = new ObjectMapper();

        if(filter.isEmpty()) {
            data = orderRepository.findById(orderId)
            .orElseThrow(()->new RuntimeException("Order not found."));
        }
        else {
            data = orderRepository.findByOrderIdAndOrderStatus(orderId,OrderStatus.getStatus(filter))
            .orElseThrow(()->new RuntimeException("Order not found."));
        }       
        

        if (null != data) {            

            List<OrderedProductDto> dtos = new ArrayList<>();
            for(OrderProduct product : data.getOrderedProducts()) {

                ProductColorDto color = mapper.convertValue(catalogServiceClient.getColorInfo(Long.toString(product.getColorId())).getBody(), 
            ProductColorDto.class);

                
                OrderedProductDto dto = OrderedProductDto.builder()
                .color(color)
                .id(Long.toString(product.getOrderProductId()))
                .image(product.getImage())
                .name(product.getName())
                .price(product.getPrice())
                .qty(product.getQty())
                .build();
                
                dtos.add(dto);
            }

            
            OrderAddressDto address = new OrderAddressDto();

            OrderAddressDto.deepCopyShippingAddressDto(address, data.getShippingAddress());
                        

            // SharedUserDto userInfo = mapper.convertValue(userServiceClient.findUserByEmail(email).getBody(), 
            // SharedUserDto.class);

            OrderDto result = OrderDto.builder()
            .trackingId(data.getTrackingId())
            //.isPaid(data.isPaid())
            .couponApplied(data.getCouponApplied())
            .deliveredCreatedAt(data.getDeliveredCreatedAt())
            .orderStatus(data.getOrderStatus().name())
            .paymentMethod(data.getPaymentMethod())            
            .paymentStatus(PaymentStatus.COMPLETED.getStatus())
            .products(dtos)
            .shippingAddress(address)
            // .user(UserProfileDto.builder()
            //         .userId(Long.toString(data.getUser().getUserId()))
            //         .username(data.getUser().getUserName())
            //         .image(data.getUser().getImage()).build()
            //  )
            //.user(userInfo)
             .totalBeforeDiscount(data.getTotalBeforeDiscount())
             .total(data.getTotal())
            .build();
            
            return result;
        }

        return null;
    }

    public List<OrderDto> getOrders(String email, String filter) {

        // User user = userRepository.findByUserName(username)
        // .orElseThrow(()->new RuntimeException(StatusMessages.USER_NOT_FOUND));

        Long customerId = getCustomerIdByEmail(email);

        List<Order> orders = getOrdersByCustomerId(customerId);

        if (!orders.isEmpty()) {

            List<OrderDto> result = new ArrayList<>();

            for(Order o : orders) {

                OrderDto dto = getOrder(o.getOrderId(), email, filter);

                result.add(dto);
            }

            return result;
        }

        return null;

    }   

        

    @Transactional
    public void persisitPaypalPayment(PaypalPaymentRequest request) {


        PaymentRequest payload = new PaymentRequest();
        payload.setOrderId(Long.parseLong(request.getOrderId()));             
        payload.setCustomerId(getCustomerIdByEmail(request.getUserEmail()));
        payload.setTrackingId(request.getTrackingId());
        payload.setAmounts(request.getAmounts());
        payload.setPaypalOrderId(request.getPaypalOrderId());
        payload.setOrderStatus(OrderStatus.valueOf(request.getOrderStatus()));
        payload.setPaymentType(PaymentType.PAYPAL);
        payload.setPaymentStatus(PaymentStatus.valueOf(request.getPaymentStatus()));
        
        paymentOutboxHelper.savePaymentOutbox(payload, OutboxStatus.STARTED);

    }

}
