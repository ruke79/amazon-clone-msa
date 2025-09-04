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

    @Transactional(readOnly = true)
    public Order getOrderByTrackingId(String trackingId) {

        Order order = orderRepository.findByTrackingId(trackingId).orElse(null);

        return order;

    }

    @Transactional
    public Order createOrder(OrderRequest request) {

        Long customerId = getCustomerIdByEmail(request.getEmail());

        if (null != customerId) {

            Order order = Order.builder()
                    .trackingId(UUID.randomUUID().toString())
                    .paymentMethod(request.getPaymentMethod())
                    .couponApplied(request.getCouponApplied())
                    .total(request.getTotal())
                    .totalBeforeDiscount(request.getTotalBeforeDiscount())
                    .orderStatus(OrderStatus.NOT_PROCESSED)
                    .customerId(customerId)
                    .build();

            List<OrderProduct> ordered = new ArrayList<>();

            for (CartProductDto p : request.getProducts()) {
                // Use the builder pattern to create OrderProduct
                OrderProduct op = OrderProduct.builder()
                        .name(p.getName())
                        .colorId(Long.parseLong(p.getColor().getId()))
                        .image(p.getImage())
                        .price(p.getPrice())
                        .qty(p.getQty())
                        .size(p.getSize())
                        .productId(Long.parseLong(cartServiceClient.getProductId(p.getId())))
                        .order(order) // Set the parent Order object
                        .build();

                ordered.add(op);
            }

            order.setOrderedProducts(ordered);

            Order result = orderRepository.save(order);

            OrderAddress address = OrderAddress.builder()
                    .firstname(request.getShippingAddress().getFirstname())
                    .lastname(request.getShippingAddress().getLastname())
                    .address1(request.getShippingAddress().getAddress1())
                    .address2(request.getShippingAddress().getAddress2())
                    .city(request.getShippingAddress().getCity())
                    .state(request.getShippingAddress().getState())
                    .zipCode(request.getShippingAddress().getZipCode())
                    .country(request.getShippingAddress().getCountry())
                    .phoneNumber(request.getShippingAddress().getPhoneNumber())
                    .build();            

            result.setShippingAddress(address);

            address.setOrder(result);

            orderAddressRepository.save(address);

            return result;
        }
        return null;
    }

    public OrderDto getOrder(Long orderId, String email, String filter) {

        Order data = null;

        ObjectMapper mapper = new ObjectMapper();

        if (filter.isEmpty()) {
            data = orderRepository.findById(orderId)
                    .orElse(null);
        } else {
            data = orderRepository.findByOrderIdAndOrderStatus(orderId, OrderStatus.getStatus(filter))
                    .orElse(null);
        }

        if (null != data) {

            List<OrderedProductDto> dtos = new ArrayList<>();
            for (OrderProduct product : data.getOrderedProducts()) {

                ProductColorDto color = mapper.convertValue(
                        catalogServiceClient.getColorInfo(Long.toString(product.getColorId())).getBody(),
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

            OrderAddressDto.toDto(address, data.getShippingAddress());

            OrderDto result = OrderDto.builder()
                    .id(Long.toString(data.getOrderId()))
                    .trackingId(data.getTrackingId())
                    .couponApplied(data.getCouponApplied())
                    .deliveredCreatedAt(data.getDeliveredCreatedAt())
                    .orderStatus(data.getOrderStatus().name())
                    .paymentMethod(data.getPaymentMethod())
                    .paymentStatus(PaymentStatus.COMPLETED.getStatus())
                    .products(dtos)
                    .shippingAddress(address)
                    .totalBeforeDiscount(data.getTotalBeforeDiscount())
                    .total(data.getTotal())
                    .build();

            return result;
        }

        return null;
    }

    public List<OrderDto> getOrders(String email, String filter) {

        Long customerId = getCustomerIdByEmail(email);

        List<Order> orders = getOrdersByCustomerId(customerId);

        if (!orders.isEmpty()) {

            List<OrderDto> result = new ArrayList<>();

            for (Order o : orders) {

                OrderDto dto = getOrder(o.getOrderId(), email, filter);

                result.add(dto);
            }

            return result;
        }

        return null;

    }

    @Transactional
    public void persisitPaypalPayment(PaypalPaymentRequest request) {

        Order order = getOrderByTrackingId(request.getTrackingId());
        if (null != order) {

            PaymentRequest payload = PaymentRequest.builder()
                    .orderId(order.getOrderId())
                    .customerId(getCustomerIdByEmail(request.getUserEmail()))
                    .trackingId(request.getTrackingId())
                    .amounts(request.getAmounts())
                    .paypalOrderId(request.getPaypalOrderId())
                    .couponName(request.getCouponName())
                    .orderStatus(OrderStatus.valueOf(request.getOrderStatus()))
                    .paymentType(PaymentType.PAYPAL)
                    .paymentStatus(PaymentStatus.valueOf(request.getPaymentStatus()))
                    .build();

            paymentOutboxHelper.savePaymentOutbox(payload, OutboxStatus.STARTED);
        }
    }
}