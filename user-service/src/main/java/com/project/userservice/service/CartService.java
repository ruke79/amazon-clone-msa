package com.project.userservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.userservice.constants.OperationStatus;
import com.project.userservice.dto.AddressDto;
import com.project.userservice.dto.CartDto;
import com.project.userservice.dto.CartProductDto;
import com.project.userservice.dto.ColorAttributeDto;
import com.project.userservice.dto.ProductInfoDto;
import com.project.userservice.model.Cart;
import com.project.userservice.model.CartProduct;
import com.project.userservice.model.Coupon;
import com.project.userservice.model.Product;
import com.project.userservice.model.ProductColorAttribute;
import com.project.userservice.model.ProductSizeAttribute;
import com.project.userservice.model.ProductSku;
import com.project.userservice.model.ShippingAddress;
import com.project.userservice.model.User;
import com.project.userservice.model.WishList;
import com.project.userservice.repository.CartProductRepository;
import com.project.userservice.repository.CartRepository;
import com.project.userservice.repository.CouponRepository;
import com.project.userservice.repository.ProductRepository;
import com.project.userservice.repository.ProductSkuRepository;
import com.project.userservice.repository.ShippingAddressRepository;
import com.project.userservice.repository.UserRepository;
import com.project.userservice.repository.WishiListRepository;
import com.project.userservice.security.request.CartRequest;
import com.project.userservice.security.request.CouponRequest;
import com.project.userservice.security.request.ProductInfoRequest;
import com.project.userservice.security.request.WishListRequest;
import com.project.userservice.security.response.CartResponse;
import com.project.userservice.security.response.CouponResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    private final CartProductRepository cartProductRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final ProductSkuRepository productSkuRepository;

    private final ShippingAddressRepository shippingAddressRepository;

    private final WishiListRepository wishiListRepository;

    private final CouponRepository couponRepository;

   

   

    public String updatePaymentMethod(String username, String paymentMethod) {

        Optional<User> user = userRepository.findByUserName(username);

        if (!user.isPresent())
            return null;

        user.get().setDefaultPaymentMethod(paymentMethod);

        if (userRepository.updateDefaultPaymentMethod(user.get().getUserId(), paymentMethod) > 0) {
            return paymentMethod;
        }
        return null;

    }

   
    public CartDto getCart(String username) {

        Optional<User> user = userRepository.findByUserName(username);

        if (!user.isPresent())
            return null;

        Optional<Cart> cart = cartRepository.findByUser_UserName(username);

        if (!cart.isPresent())
            return null;

        CartDto result = new CartDto();
        result.setUserImage(user.get().getImage());

        // List<ShippingAddress> addresses = shippingAddressRepository.findByUser_UserId(user.get().getUserId());

        // {
        //     List<AddressDto> addressDtos = new ArrayList<>();
        //     for (ShippingAddress address : addresses) {
        //         if (address != null) {
        //             AddressDto dto = new AddressDto();
        //             dto.setId(Long.toString(address.getShippingAddressId()));

        //             dto.setAddress1(address.getAddress1());
        //             dto.setAddress2(address.getAddress2());
        //             dto.setCity(address.getCity());
        //             dto.setState(address.getState());
        //             dto.setCountry(address.getCountry());
        //             dto.setFirstname(address.getFirstname());
        //             dto.setLastname(address.getLastname());
        //             dto.setPhoneNumber(address.getPhoneNumber());
        //             dto.setZipCode(address.getZipCode());
        //             dto.setActive(false);

        //             addressDtos.add(dto);
        //         }
        //     }
        //     result.setAddress(addressDtos);
        // }
        // }

        List<CartProductDto> products = new ArrayList<>();

        for (CartProduct item : cart.get().getCartProducts()) {

            CartProductDto dto = new CartProductDto();
            dto.setId(Long.toString(item.getCartproductId()));
            dto.setColor(ColorAttributeDto.builder()
                    .id(item.getColor().getColorId())
                    .color(item.getColor().getColor())
                    .colorImage(item.getColor().getColorImage()).build());
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

    public int deleteCartItem(String uid, String username) {

        

        CartProduct item = cartProductRepository.findBy_uid(uid)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (null != item) {

            Cart cart = cartRepository.findByUser_UserName(username)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));
            cart.setCartTotal(cart.getCartTotal() - (item.getPrice() + item.getShipping()));

            cartRepository.save(cart);

            if (cart.getCartTotal() <= 0)
                cartRepository.delete(cart);

            cartProductRepository.delete(item);

            return 1;
        }
        return 0;
    }

    public List<ProductInfoDto> updateCart(ProductInfoRequest request) {

        List<ProductInfoDto> result = new ArrayList<>();

        for (ProductInfoDto cartItem : request.getProducts()) {

            Product product = productRepository.findById(cartItem.getId()).get();

            int originalPrice = product.getSku_products().get(cartItem.getStyle()).getSizes().stream()
                    .filter(i -> i.getSize().equals(cartItem.getSize())).findFirst().get().getPrice();
            int quantity = product.getSku_products().get(cartItem.getStyle()).getSizes().stream()
                    .filter(i -> i.getSize().equals(i.getSize())).findFirst().get().getQuantity();
            int discount = product.getSku_products().get(cartItem.getStyle()).getDiscount();

            ProductInfoDto dto = new ProductInfoDto();
            dto = cartItem;
            dto.setPriceBefore(originalPrice);
            dto.setQty(cartItem.getQty());

            if (discount > 0) {
                int price = originalPrice - (originalPrice / discount);
                dto.setPrice(price);
            } else {
                dto.setPrice(originalPrice);
            }
            dto.setShipping(product.getShipping());

            result.add(dto);
        }

        return result;
    }

    public List<ProductInfoDto> loadCart(String username) {

        Optional<Cart> cart = cartRepository.findByUser_UserName(username);
        // .orElseThrow(()->new RuntimeException("Cart not found"));

        if (cart.isPresent()) {

            List<CartProduct> products = cart.get().getCartProducts();

            List<ProductInfoDto> result = new ArrayList<>();
            for (CartProduct p : products) {

                Product product = productRepository.findById(p.getProduct().getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                AtomicInteger counter = new AtomicInteger(-1);

                int index = product.getSku_products().get(p.getStyle()).getSizes().stream()
                        .filter(size -> {
                            counter.getAndIncrement();
                            return size.getSize().equals(p.getSize());
                        })
                        .mapToInt(size -> counter.get())
                        .findFirst()
                        .orElse(-1);

                // ProductInfoDto dto = productService.getCartProductInfo(p.getProduct().getProductId(),
                //         p.getStyle(), index);

                // int originalPrice = product.getSku_products().get(p.getStyle()).getSizes().stream()
                //         .filter(i -> i.getSize().equals(p.getSize())).findFirst().get().getPrice();
                // int discount = product.getSku_products().get(p.getStyle()).getDiscount();

                // dto.setPriceBefore(originalPrice);
                // dto.setQty(p.getQty());
                // dto.set_uid(p.get_uid());

                // if (discount > 0) {
                //     int price = originalPrice - (originalPrice / discount);
                //     dto.setPrice(price);
                // } else {
                //     dto.setPrice(originalPrice);
                // }
                // dto.setShipping(product.getShipping());

                // result.add(dto);

            }

            return result;
        }
        return null;

    }

    public Cart saveCart(CartRequest request, String username) {

        if (request.getProducts().size() > 0) {

            Optional<User> user = userRepository.findByUserName(username);

            if (user.isPresent()) {
                Optional<Cart> existed = cartRepository.findByUser_UserName(username);
                if (existed.isPresent()) {

                    cartRepository.delete(existed.get());
                }
            }

            Cart cart = new Cart();
            cart.setUser(user.get());

            List<CartProduct> products = new ArrayList<CartProduct>();

            for (ProductInfoDto cartItem : request.getProducts()) {

                
                Optional<Product> product = productRepository.findById(cartItem.getId());

                if (product.isPresent()) {
                    ProductSku sku = product.get().getSku_products().get(cartItem.getStyle());

                    int price = sku.getSizes().stream().filter(p -> p.getSize().equals(cartItem.getSize()))
                            .findFirst().get().getPrice();
                    if (sku.getDiscount() > 0)
                        price = (price - price / sku.getDiscount());

                    ProductColorAttribute color = ProductColorAttribute.builder()
                            .colorId(cartItem.getColor().getId())
                            .color(cartItem.getColor().getColor())
                            .colorImage(cartItem.getColor().getColorImage()).build();

                    CartProduct cartProduct = CartProduct.builder()
                            .name(product.get().getName())
                            .color(color)
                            .style(cartItem.getStyle())
                            .product(product.get())
                            .image(sku.getImages().get(0))
                            .qty(cartItem.getQty())
                            .price(price)
                            .size(cartItem.getSize())
                            ._uid(cartItem.get_uid())
                            .shipping(product.get().getShipping())
                            .build();

                    products.add(cartProduct);
                    cartProduct.setCart(cart);
                }

            }
            int cartTotal = 0;
            for (CartProduct p : products) {
                cartTotal = cartTotal + p.getPrice() * p.getQty() + p.getShipping();
            }

            cart.setCartProducts(products);
            cart.setCartTotal(cartTotal);

            return cartRepository.save(cart);

        }

        return null;
    }

 

    public CouponResponse applyCoupon(CouponRequest request, String username) {

        Optional<User> user = userRepository.findByUserName(username);

        if (user.isPresent()) {

            Optional<Coupon> coupon = couponRepository.findById(Long.parseLong(request.getCoupon().getId()));

            if (!coupon.isPresent()) {
                return null;
            }

            Optional<Cart> cart = cartRepository.findByUser_UserName(username);

            int totalAfterDiscount = (cart.get().getCartTotal() * coupon.get().getDiscount()) / 100;

            cart.get().setTotalAfterDiscount(totalAfterDiscount);

            cartRepository.save(cart.get());

            CouponResponse result = new CouponResponse(totalAfterDiscount, coupon.get().getDiscount());

            return result;
        }
        return null;

    }

}
