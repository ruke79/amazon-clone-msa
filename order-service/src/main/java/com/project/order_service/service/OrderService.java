package com.project.order_service.service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.List;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;

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

    private final ObjectMapper objectMapper; // ObjectMapper를 주입받아 사용

    // @Cacheable: email을 키로 사용하여 고객 ID를 캐시합니다.
    @Cacheable(value = "customers", key = "#email")
    @Transactional(readOnly = true)
    public Long getCustomerIdByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(Customer::getId)
                .orElse(null);
    }
    
    // @Cacheable: customerId를 키로 사용하여 주문 목록을 캐시합니다.
    @Cacheable(value = "orders", key = "#customerId")
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId).orElse(null);
    }

    @Transactional(readOnly = true)
    public Order getOrderByTrackingId(String trackingId) {
        return orderRepository.findByTrackingId(trackingId).orElse(null);
    }

    // @CacheEvict: 새로운 주문 생성 시, 관련 캐시(orders, customers)를 무효화합니다.
    @CacheEvict(value = {"orders", "customers"}, allEntries = true)
    @Transactional
    public Order createOrder(OrderRequest request) {
        Long customerId = getCustomerIdByEmail(request.getEmail());

        if (customerId != null) {
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
                OrderProduct op = OrderProduct.builder()
                        .name(p.getName())
                        .colorId(Long.parseLong(p.getColor().getId()))
                        .image(p.getImage())
                        .price(p.getPrice())
                        .qty(p.getQty())
                        .size(p.getSize())
                        .productId(Long.parseLong(cartServiceClient.getProductId(p.getId())))
                        .order(order)
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

    // @Cacheable: orderId와 email을 키로 사용하여 주문 상세 정보를 캐시합니다.
    @Cacheable(value = "order", key = "{#orderId, #email, #filter}")
    public OrderDto getOrder(Long orderId, String email, String filter) {
        Order data = filter.isEmpty()
                ? orderRepository.findById(orderId).orElse(null)
                : orderRepository.findByOrderIdAndOrderStatus(orderId, OrderStatus.getStatus(filter)).orElse(null);

        if (data != null) {
            List<OrderedProductDto> dtos = data.getOrderedProducts().stream()
                    .map(product -> {
                        ProductColorDto color = objectMapper.convertValue(
                                catalogServiceClient.getColorInfo(Long.toString(product.getColorId())).getBody(),
                                ProductColorDto.class);
                        return OrderedProductDto.builder()
                                .color(color)
                                .id(Long.toString(product.getOrderProductId()))
                                .image(product.getImage())
                                .name(product.getName())
                                .price(product.getPrice())
                                .qty(product.getQty())
                                .build();
                    })
                    .collect(Collectors.toList());

            OrderAddressDto address = new OrderAddressDto();
            OrderAddressDto.toDto(address, data.getShippingAddress());

            return OrderDto.builder()
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
        }
        return null;
    }

    // @Cacheable: email과 filter를 키로 사용하여 주문 목록을 캐시합니다.
    @Cacheable(value = "orderList", key = "{#email, #filter}")
    public List<OrderDto> getOrders(String email, String filter) {
        Long customerId = getCustomerIdByEmail(email);
        List<Order> orders = getOrdersByCustomerId(customerId);

        if (orders != null && !orders.isEmpty()) {
            return orders.stream()
                    .map(o -> getOrder(o.getOrderId(), email, filter))
                    .collect(Collectors.toList());
        }
        return null;
    }

     // 페이지별로 주문 목록을 조회하는 새로운 메서드
    @Cacheable(value = "ordersPage", key = "{#customerId, #page, #size}")
    @Transactional(readOnly = true)
    public Page<OrderDto> getOrdersByCustomerIdWithPagination(Long customerId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> ordersPage = orderRepository.findByCustomerId(customerId, pageRequest);

        return ordersPage.map(this::convertToOrderDto);
    }

    // --- 점진적 로딩(커서 기반 페이징) 메서드 추가 ---
    /**
     * 고객 ID로 주문 목록을 커서 기반으로 조회합니다 (점진적 로딩).
     * @param customerId 조회할 고객의 ID
     * @param cursorId 마지막으로 조회한 주문의 ID (첫 페이지 조회 시 null)
     * @param size 페이지당 주문 개수
     * @return OrderDto 목록과 다음 커서 ID
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "ordersCursor", key = "{#customerId, #cursorId, #size}")
    public Page<OrderDto> getOrdersByCustomerIdWithCursor(Long customerId, Long cursorId, int size) {
        PageRequest pageRequest = PageRequest.of(0, size, Sort.by("orderId").descending());
        Page<Order> ordersPage;

        if (cursorId == null || cursorId == 0) {
            // 첫 페이지 조회 (커서가 없을 경우)
            ordersPage = orderRepository.findByCustomerIdOrderByOrderIdDesc(customerId, pageRequest);
        } else {
            // 다음 페이지 조회 (커서가 있을 경우)
            ordersPage = orderRepository.findByCustomerIdAndOrderIdLessThanOrderByOrderIdDesc(customerId, cursorId, pageRequest);
        }

        return ordersPage.map(this::convertToOrderDto);
    }


    // @CacheEvict: 결제 완료 시, 관련 캐시(orders, orderList)를 무효화합니다.
    @CacheEvict(value = {"orders", "orderList"}, allEntries = true)
    @Transactional
    public void persisitPaypalPayment(PaypalPaymentRequest request) {
        Order order = getOrderByTrackingId(request.getTrackingId());
        if (order != null) {
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

      // 도메인 엔티티를 DTO로 변환하는 공통 메서드
    private OrderDto convertToOrderDto(Order data) {
        List<OrderedProductDto> dtos = data.getOrderedProducts().stream()
                .map(product -> {
                    ProductColorDto color = objectMapper.convertValue(
                            catalogServiceClient.getColorInfo(Long.toString(product.getColorId())).getBody(),
                            ProductColorDto.class);
                    return OrderedProductDto.builder()
                            .color(color)
                            .id(Long.toString(product.getOrderProductId()))
                            .image(product.getImage())
                            .name(product.getName())
                            .price(product.getPrice())
                            .qty(product.getQty())
                            .build();
                })
                .collect(Collectors.toList());

        OrderAddressDto address = new OrderAddressDto();
        OrderAddressDto.toDto(address, data.getShippingAddress());

        return OrderDto.builder()
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
    }
}