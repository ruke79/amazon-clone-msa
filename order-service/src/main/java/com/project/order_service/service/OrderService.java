package com.project.order_service.service;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.math.stat.descriptive.summary.Product;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.order_service.client.CartServiceClient;
import com.project.order_service.client.CatalogServiceClient;
import com.project.order_service.client.UserServiceClient;
import com.project.order_service.constants.PaymentResultStatus;
import com.project.order_service.constants.StatusMessages;
import com.project.order_service.dto.OrderAddressDto;
import com.project.order_service.dto.CartProductDto;
import com.project.order_service.dto.ColorAttributeDto;
import com.project.order_service.dto.OrderDto;
import com.project.order_service.dto.OrderedProductDto;
import com.project.order_service.dto.ServiceUserDto;
import com.project.order_service.dto.UserProfileDto;
import com.project.order_service.dto.request.OrderRequest;
import com.project.order_service.model.Order;
import com.project.order_service.model.OrderedProduct;
import com.project.order_service.repository.OrderRepository;
import com.project.order_service.repository.OrderedProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    
    private final OrderRepository orderRepository;

    private final UserServiceClient userServiceClient;

    private final CatalogServiceClient catalogServiceClient;
    private final CartServiceClient cartServiceClient;
    //private final UserRepository userRepository;

    private final OrderedProductRepository orderedProductRepository;
    
    //private final ProductRepository productRepository;

    //private final PaymentRepository paymentRepository;

    //private final CartProductRepository cartProductRepository;

    //private final ShippingAddressRepository shippingAddressRepository;

    
    public Order createOrder(OrderRequest request) {

        ObjectMapper mapper = new ObjectMapper();
            ServiceUserDto response = mapper.convertValue(userServiceClient.findUserByEmail(request.getUserId()).getBody(), 
            ServiceUserDto.class);


        if (null != response) {

            Order order = new Order();

            order.setOrderNumber(request.getOrderNumber());
            order.setPaymentMethod(request.getPaymentMethod());
            order.setCouponApplied(request.getCouponApplied());
            order.setTotal(request.getTotal());
            order.setTotalBeforeDiscount(request.getTotalBeforeDiscount());

            //user.getOrderLists().add(order.get);

            
            order.setUserId(Long.parseLong(response.getUserId()));

            // AddressDto shippingAddress = request.getShippingAddress();
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

            List<OrderedProduct> ordered = new ArrayList<>();
                        
            for (CartProductDto p : request.getProducts()) {

                OrderedProduct op = new OrderedProduct();

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

                String productId = mapper.convertValue(cartServiceClient.getProductId(p.getId()).getBody(), 
                String.class);

                op.setProductId(Long.parseLong(productId));

                //data.getOrderedProducts().add(op);
                    
                ordered.add(op);
                op.setOrder(order);
                orderedProductRepository.save(op);                
                
            }

            order.setOrderedProducts(ordered);

            //order.setPaymentResult(pr);
            
            Order result = orderRepository.save(order);  
             


            return result;
        }       
        return null;
    }

    public OrderDto getOrder(Long orderId, String filter)  {

        Order data = null;

        ObjectMapper mapper = new ObjectMapper();

        if(filter.isEmpty()) {
            data = orderRepository.findById(orderId)
            .orElseThrow(()->new RuntimeException("Order not found."));
        }
        else {
            data = orderRepository.findByOrderIdAndPaymentResult_PayStatus(orderId,PaymentResultStatus.getStatus(filter))
            .orElseThrow(()->new RuntimeException("Order not found."));
        }       
        

        if (null != data) {            

            List<OrderedProductDto> dtos = new ArrayList<>();
            for(OrderedProduct product : data.getOrderedProducts()) {

                ColorAttributeDto color = mapper.convertValue(catalogServiceClient.getColorInfo(Long.toString(product.getColorId())).getBody(), 
            ColorAttributeDto.class);

                
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
                        

            ServiceUserDto userInfo = mapper.convertValue(userServiceClient.findUserByEmail(Long.toString(data.getUserId())).getBody(), 
            ServiceUserDto.class);

            OrderDto result = OrderDto.builder()
            .orderNumber(data.getOrderNumber())
            .isPaid(data.isPaid())
            .couponApplied(data.getCouponApplied())
            .deliveredAt(data.getDeliveredAt())
            .orderStatus(data.getOrderStatus().name())
            .paymentMethod(data.getPaymentMethod())
            //.paymentResult(data.getPaymentResult().getPayStatus().getStatus())            
            .paymentResult(PaymentResultStatus.NOT_PROCESSED.getStatus())
            .products(dtos)
            .shippingAddress(address)
            // .user(UserProfileDto.builder()
            //         .userId(Long.toString(data.getUser().getUserId()))
            //         .username(data.getUser().getUserName())
            //         .image(data.getUser().getImage()).build()
            //  )
            .user(userInfo)
             .totalBeforeDiscount(data.getTotalBeforeDiscount())
             .total(data.getTotal())
            .build();
            
            return result;
        }

        return null;
    }

    public List<OrderDto> getOrders(String username, String filter) {

        // User user = userRepository.findByUserName(username)
        // .orElseThrow(()->new RuntimeException(StatusMessages.USER_NOT_FOUND));
        ObjectMapper mapper = new ObjectMapper();
        ServiceUserDto user = mapper.convertValue(userServiceClient.findUserByName(username).getBody(), 
        ServiceUserDto.class);
        

        List<OrderDto> result = new ArrayList<>();

        // if (null != user) {
        //         List<Order> orders = user.getOrderLists();

        //         for(Order o : orders) {

        //             OrderDto dto = getOrder(o.getOrderId(), filter);

        //             result.add(dto);
        //         }
        // }

        return result;

    }   

}
