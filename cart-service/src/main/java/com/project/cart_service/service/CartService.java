package com.project.cart_service.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.cart_service.client.CatalogServiceClient;
import com.project.cart_service.client.UserServiceClient;
import com.project.cart_service.dto.CartDto;
import com.project.cart_service.dto.CartProductDto;


import com.project.cart_service.dto.request.CartRequest;
import com.project.cart_service.dto.request.ProductInfoRequest;
import com.project.cart_service.model.Cart;
import com.project.cart_service.model.CartProduct;

import com.project.cart_service.repository.CartProductRepository;
import com.project.cart_service.repository.CartRepository;
import com.project.common.dto.ProductColorDto;
import com.project.common.dto.ProductDto;
import com.project.common.dto.ProductInfoDto;
import com.project.common.dto.ProductSkuDto;
import com.project.common.dto.SharedUserDto;
import com.project.common.message.dto.request.CartEmptyRequest;
import com.project.common.message.dto.request.CartRollbackRequest;
import com.project.common.message.dto.request.CouponUseRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    private final CartProductRepository cartProductRepository;

    private final UserServiceClient userServiceClient;
    private final CatalogServiceClient catalogServiceClient;

    public Long getProductId(String cartProductId) {

        log.info(cartProductId);

        CartProduct product = cartProductRepository.findById(Long.parseLong(cartProductId)).orElse(null);

        return product.getProductId();
    }

    public CartDto getCart(String email) {

        ObjectMapper mapper = new ObjectMapper();
        SharedUserDto response = mapper.convertValue(userServiceClient.findUserByEmail(email).getBody(),
                SharedUserDto.class);

        Optional<Cart> cart = cartRepository.findByUserId(response.getUserId());

        if (!cart.isPresent())
            return null;

        CartDto result = new CartDto();
        result.setUserId(Long.toString(response.getUserId()));
        result.setUserImage(response.getImage());

        List<CartProductDto> products = new ArrayList<>();

        for (CartProduct item : cart.get().getCartProducts()) {

            CartProductDto dto = new CartProductDto();
            dto.setId(Long.toString(item.getCartproductId()));

            ProductColorDto color = mapper.convertValue(
                    catalogServiceClient.getColorInfo(Long.toString(item.getColorId())).getBody(),
                    ProductColorDto.class);

            dto.setColor(ProductColorDto.builder()
                    .id(color.getId())
                    .color(color.getColor())
                    .colorImage(color.getColorImage()).build());
            dto.setImage(item.getImage());
            dto.setName(item.getName());
            dto.setPrice(item.getPrice());
            dto.setQty(item.getQty());
            dto.setSize(item.getSize());

            products.add(dto);
        }
        result.setProducts(products);
        result.setCartTotal(cart.get().getCartTotal());
        result.setTotalAfterDiscount(cart.get().getTotalAfterDiscount());

        return result;
    }

    public int deleteCartItem(String uid, Long userId) {

        CartProduct item = cartProductRepository.findBy_uid(uid)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (null != item) {

            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));
            BigDecimal deletedPrice = item.getPrice().add(item.getShipping());
            cart.setCartTotal(cart.getCartTotal().subtract(deletedPrice));

            cartRepository.save(cart);

            if (-1 == cart.getCartTotal().compareTo(new BigDecimal(0)))
                cartRepository.delete(cart);

            cartProductRepository.delete(item);

            return 1;
        }
        return 0;
    }

    public List<ProductInfoDto> updateCart(ProductInfoRequest request) {

        if (null != request.getProducts()) {

            List<ProductInfoDto> result = new ArrayList<>();

            for (ProductInfoDto cartItem : request.getProducts()) {

                // Product product = productRepository.findById(cartItem.getId()).get();
                ProductDto product = catalogServiceClient.getProductInfo(cartItem.getId()).getBody();

                BigDecimal originalPrice = computeOriginalPriceFromSkusByStyleAndSize(
                        product.getSkus(), cartItem.getStyle(), cartItem.getSize());

                BigDecimal discount = new BigDecimal(product.getSkus().get(cartItem.getStyle()).getDiscount());

                BigDecimal price = computeDiscountedPriceFromOriginalPrice(originalPrice, discount);

                ProductInfoDto dto = new ProductInfoDto();
                dto = cartItem;
                dto.setPriceBefore(originalPrice.toPlainString());
                dto.setQty(cartItem.getQty());

                dto.setPrice(price.toPlainString());

                dto.setShipping(product.getShipping());

                result.add(dto);
            }

            return result;
        }
        return null;
    }

    public List<ProductInfoDto> loadCart(String userId) {

        ObjectMapper mapper = new ObjectMapper();
        SharedUserDto response = mapper.convertValue(userServiceClient.findUserByEmail(userId).getBody(),
                SharedUserDto.class);

        if (null == response) {
            return null;
        }

        Optional<Cart> cart = cartRepository.findByUserId(response.getUserId());
        // .orElseThrow(()->new RuntimeException("Cart not found"));

        if (cart.isPresent()) {

            List<CartProduct> products = cart.get().getCartProducts();

            List<ProductInfoDto> result = new ArrayList<>();
            for (CartProduct p : products) {

                ProductDto product = catalogServiceClient.getProductInfo(Long.toString(p.getProductId())).getBody();

                AtomicInteger counter = new AtomicInteger(-1);

                int index = product.getSkus().get(p.getStyle()).getSizes().stream()
                        .filter(size -> {
                            counter.getAndIncrement();
                            return size.getSize().equals(p.getSize());
                        })
                        .mapToInt(size -> counter.get())
                        .findFirst()
                        .orElse(-1);

                // ProductInfoDto dto =
                // productService.getCartProductInfo(p.getProduct().getProductId(),
                // p.getStyle(), index);
                ProductInfoDto dto = mapper
                        .convertValue(catalogServiceClient.getCartProductInfo(Long.toString(p.getProductId()),
                                p.getStyle(), index).getBody(), ProductInfoDto.class);

                BigDecimal originalPrice = computeOriginalPriceFromSkusByStyleAndSize(
                        product.getSkus(), p.getStyle(), p.getSize());

                dto.setPriceBefore(originalPrice.toPlainString());

                BigDecimal discount = new BigDecimal(product.getSkus().get(p.getStyle()).getDiscount());

                BigDecimal price = computeDiscountedPriceFromOriginalPrice(originalPrice, discount);

                dto.setPrice(price.toPlainString());

                // if (discount > 0) {
                // BigDecimal price = originalPrice - (originalPrice / discount);
                // dto.setPrice(price);
                // } else {
                // dto.setPrice(originalPrice);
                // }

                dto.setQty(p.getQty());
                dto.set_uid(p.get_uid());
                dto.setShipping(product.getShipping());

                result.add(dto);

            }

            return result;
        }
        return null;

    }

    private BigDecimal computeDiscountedPriceFromOriginalPrice(BigDecimal originalPrice, BigDecimal discount) {

        BigDecimal discountedPrice = originalPrice;
        if (discount.compareTo(new BigDecimal(0)) > 0) {
            discountedPrice = originalPrice.subtract(originalPrice.divide(discount, 2, RoundingMode.HALF_UP));
        }
        return discountedPrice;
    }

    private BigDecimal computeOriginalPriceFromSkusByStyleAndSize(List<ProductSkuDto> skus, int style, String size) {

        BigDecimal originalPrice = skus.get(style).getSizes().stream()
                .filter(i -> i.getSize().equals(size)).findFirst().get().getPrice();

        return originalPrice;
    }

    @Transactional
    private void deleteCartItem(Long userId) {

        Optional<Cart> existed = cartRepository.findByUserId(userId);
        if (existed.isPresent()) {

            
            cartRepository.delete(existed.get());
            log.info("Deleteing cart completed");
            

        }
    }

    private BigDecimal getPriceFromSkuBySize(ProductSkuDto sku, String size) {

        BigDecimal price = sku.getSizes().stream().filter(p -> p.getSize().equals(size))
                .findFirst().get().getPrice();
        if (sku.getDiscount() > 0) {

            BigDecimal discount = new BigDecimal(sku.getDiscount());
            price = price.subtract(price.divide(discount, 2, RoundingMode.HALF_UP));
        }
        return price;
    }

    private BigDecimal computeCartTotal(List<CartProduct> products) {
        BigDecimal cartTotal = new BigDecimal(0);
        for (CartProduct p : products) {
            BigDecimal qty = new BigDecimal(p.getQty());
            cartTotal = cartTotal.add(p.getPrice().multiply(qty)).add(p.getShipping());
        }
        return cartTotal;
    }

    public Cart saveCart(CartRequest request) {

        if (request.getProducts().size() > 0) {

            ObjectMapper mapper = new ObjectMapper();
            SharedUserDto response = mapper.convertValue(
                    userServiceClient.findUserByEmail(request.getEmail()).getBody(),
                    SharedUserDto.class);

            deleteCartItem(response.getUserId());

            Cart cart = new Cart();
            cart.setUserId(response.getUserId());

            List<CartProduct> products = new ArrayList<CartProduct>();

            for (ProductInfoDto cartItem : request.getProducts()) {

                ProductDto product = catalogServiceClient.getProductInfo(cartItem.getId()).getBody();

                if (null != product) {

                    ProductSkuDto sku = product.getSkus().get(cartItem.getStyle());

                    BigDecimal price = getPriceFromSkuBySize(sku, cartItem.getSize());

                    CartProduct cartProduct = CartProduct.builder()
                            .name(product.getName())
                            .colorId(Long.parseLong(cartItem.getColor().getId()))
                            .style(cartItem.getStyle())
                            .productId(Long.parseLong(product.getId()))
                            .image(sku.getImages().get(0))
                            .qty(cartItem.getQty())
                            .price(price)
                            .size(cartItem.getSize())
                            ._uid(cartItem.get_uid())
                            .shipping(new BigDecimal(product.getShipping()))
                            .build();

                    products.add(cartProduct);
                    cartProduct.setCart(cart);
                }

            }

            BigDecimal cartTotal = computeCartTotal(products);

            cart.setCartProducts(products);
            cart.setCartTotal(cartTotal);

            return cartRepository.save(cart);

        }

        return null;
    }

    @Transactional(readOnly = true)
    private Cart getCart(long userId) {

        Optional<Cart> cart = cartRepository.findByUserId(userId);

        if (cart.isPresent()) {

            return cart.get();
        }

        return null;
    }

    @Transactional
    public Cart couponUsed(CouponUseRequest request) {

        Cart cart = getCart(request.getUserId());

        if (null != cart) {

            cart.setTotalAfterDiscount(request.getTotalAfterDiscount());

            cart = cartRepository.save(cart);

            return cart;
        }

        return null;
    }

    @Transactional
    public void cartEmpty(CartEmptyRequest request) {

        if (request.isEmptyCart() == true) {

            deleteCartItem(request.getUserId());
        }
    }

    @Transactional
    void updateQtys(List<CartProduct> cartProducts, int qty) {

        for (CartProduct cartProduct : cartProducts) {
            updateQty(cartProduct, qty);
        }
    }

    @Transactional
    void updateQty(CartProduct product, int qty) {

        log.info("cart product rollback id : {}  qty : {}" ,product.getCartproductId(), qty);
        product.setQty(qty);
        cartProductRepository.save(product);
    }

    @Transactional
    public Cart cartRollback(CartRollbackRequest request) {

        Cart cart = getCart(request.getUserId());

        if (null != cart) {

            updateQtys(cart.getCartProducts(), 1);

            BigDecimal cartTotal = computeCartTotal(cart.getCartProducts());

            cart.setCartTotal(cartTotal);
            cart.setTotalAfterDiscount(new BigDecimal(0));

            cart = cartRepository.save(cart);

            return cart;
        }

        return null;
    }

}
