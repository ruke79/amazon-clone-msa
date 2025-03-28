package com.project.order_service.saga;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bouncycastle.internal.asn1.misc.CAST5CBCParameters;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common.constants.OrderStatus;
import com.project.common.constants.PaymentStatus;
import com.project.common.message.dto.request.CartEmptyRequest;
import com.project.common.message.dto.request.CartRollbackRequest;
import com.project.common.message.dto.request.CouponRollbackRequest;
import com.project.common.message.dto.request.OrderProductRequest;
import com.project.common.message.dto.request.ProductUpdateRequest;
import com.project.common.message.dto.response.PaymentResponse;
import com.project.order_service.model.Order;
import com.project.order_service.model.OrderProduct;
import com.project.order_service.ports.output.message.publisher.CartEmptyKafkaPublisher;
import com.project.order_service.ports.output.message.publisher.CartRollbackKafkaPublisher;
import com.project.order_service.ports.output.message.publisher.CouponRollbackKafkaPublisher;
import com.project.order_service.ports.output.message.publisher.ProductUpdateKafkaPublisher;
import com.project.order_service.repository.OrderRepository;
import com.project.order_service.repository.OrderProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSaga {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final CartEmptyKafkaPublisher cartEmptyKafkaPublisher;
    private final ProductUpdateKafkaPublisher productUpdateKafkaPublisher;
    private final CouponRollbackKafkaPublisher couponRollbackKafkaPublisher;
    private final CartRollbackKafkaPublisher cartRollbackKafkaPublisher;

    @Transactional(readOnly=true)
    private Order getOrderByOrderStatus(Long orderId, OrderStatus orderStatus) {

        Optional<Order> order = orderRepository.findByOrderIdAndOrderStatus(orderId, orderStatus);

        if (order.isPresent()) {
            return order.get();
        }
        return null;
    }

    @Transactional(readOnly=true)
    private List<OrderProduct> getOrderProductsByOrder(Long orderId) {

        Optional<List<OrderProduct>> order = orderProductRepository.findByOrder_OrderId(orderId);

        if (order.isPresent()) {
            return order.get();
        }
        return null;
    }
    
    @Transactional
    public void process(PaymentResponse paymentResponse) {

        Order order = getOrderByOrderStatus(paymentResponse.getOrderId(), OrderStatus.NOT_PROCESSED);

        if ( null != order) {

            // order status
            //product sku sold 
            // product size update
            // cart empty 
            //                       
            updateOrderStatus(order, OrderStatus.PAID);                                    
            
            publishProductStateUpdate(order.getOrderId());

            publicCartEmptyState(order.getCustomerId());
        }
    }
    
    @Transactional
    private void publishProductStateUpdate(Long orderId) {

        List<OrderProduct> orderProducts = getOrderProductsByOrder(orderId);

        ProductUpdateRequest request = new ProductUpdateRequest();
        List<OrderProductRequest> dtos = new ArrayList<OrderProductRequest>();

        for(OrderProduct orderProduct:orderProducts)  {

            OrderProductRequest item = mappingOrderProductToOrderProductRequest(orderProduct);
            
            if (null != request.getOrderProducts()) {
                boolean ok = request.getOrderProducts().add(item);
            }
            dtos.add(item);
        }
        
        productUpdateKafkaPublisher.publish(request);
        
    }

    @Transactional
    private void publicCartEmptyState(Long customerId) {
        CartEmptyRequest request = new CartEmptyRequest();
        request.setUserId(customerId);
        request.setEmptyCart(true);
        cartEmptyKafkaPublisher.publish(request);
    }

    private OrderProductRequest mappingOrderProductToOrderProductRequest(OrderProduct orderProduct) {

        OrderProductRequest request = new OrderProductRequest();
        request.setProductId(orderProduct.getProductId());
        request.setQty(orderProduct.getQty());
        request.setColorId(orderProduct.getColorId());
        request.setSize(orderProduct.getSize());

        return request;
    }

    @Transactional
    private Order updateOrderStatus(Order order, OrderStatus orderStatus) {

        order.setOrderStatus(orderStatus);
        order = orderRepository.save(order);
        return order;
    }

    @Transactional
    private void rollbackOrderQty(Long orderId) {

        Optional<List<OrderProduct>> products = orderProductRepository.findByOrder_OrderId(orderId);

        if (products.isPresent()) {

            products.get().forEach(p->{                
                 p.setQty(1);
                 orderProductRepository.save(p);
            });          
        }        
    }

     
    private void publishCouponStateRollback(Long customerId, String couponName) {

        CouponRollbackRequest request = new CouponRollbackRequest();
        request.setCouponName(couponName);
        request.setUserId(customerId);        
        couponRollbackKafkaPublisher.publish(request);
    }

    private void publicCartStateRollback(Long customerId) {

        CartRollbackRequest request = new CartRollbackRequest();
        request.setUserId(customerId);
        request.setQty(1);
        cartRollbackKafkaPublisher.publish(request);
    }

    // 결재 취소 -> 주문상태 
    @Transactional
    public void rollback(PaymentResponse paymentResponse) {
                
        Order order = getOrderByOrderStatus(paymentResponse.getOrderId(), OrderStatus.NOT_PROCESSED );

        if ( null != order) {

                // order status
                // cart qty
                // coupon revert            

            updateOrderStatus(order, OrderStatus.CANCELED);

            publicCartStateRollback(order.getCustomerId());

            publishCouponStateRollback(order.getCustomerId(), order.getCouponApplied());
        }
    }

}
