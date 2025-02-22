package com.project.cart_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.cart_service.client.CatalogServiceClient;
import com.project.cart_service.client.UserServiceClient;
import com.project.cart_service.dto.CartDto;
import com.project.cart_service.dto.CartProductDto;
import com.project.cart_service.dto.ColorAttributeDto;
import com.project.cart_service.dto.ProductDto;
import com.project.cart_service.dto.ProductInfoDto;
import com.project.cart_service.dto.ProductSkuDto;
import com.project.cart_service.dto.ServiceUserDto;
import com.project.cart_service.dto.request.CartRequest;
import com.project.cart_service.dto.request.ProductInfoRequest;
import com.project.cart_service.model.Cart;
import com.project.cart_service.model.CartProduct;

import com.project.cart_service.repository.CartProductRepository;
import com.project.cart_service.repository.CartRepository;



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

        CartProduct product = cartProductRepository.findById(Long.parseLong(cartProductId)).orElse(null);

        return product.getProductId();
    }

    public CartDto getCart(String email) {

        
        ObjectMapper mapper = new ObjectMapper();
            ServiceUserDto response = mapper.convertValue(userServiceClient.findUserByEmail(email).getBody(), 
            ServiceUserDto.class);

               
        Optional<Cart> cart = cartRepository.findByUserId(Long.parseLong(response.getUserId()));

        if (!cart.isPresent())
            return null;

        CartDto result = new CartDto();
        result.setUserId(response.getUserId());
        result.setUserImage(response.getImage());

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
        // // }

        List<CartProductDto> products = new ArrayList<>();

        for (CartProduct item : cart.get().getCartProducts()) {



            CartProductDto dto = new CartProductDto();
            dto.setId(Long.toString(item.getCartproductId()));
            
            ColorAttributeDto color = mapper.convertValue(catalogServiceClient.getColorInfo(Long.toString(item.getColorId())).getBody(), 
            ColorAttributeDto.class);

             

            dto.setColor(ColorAttributeDto.builder()
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

        if (null != request.getProducts()) {

            List<ProductInfoDto> result = new ArrayList<>();

            for (ProductInfoDto cartItem : request.getProducts()) {

                //Product product = productRepository.findById(cartItem.getId()).get();
                ProductDto product = catalogServiceClient.getProductInfo(cartItem.getId()).getBody();

                int originalPrice = product.getSku_products().get(cartItem.getStyle()).getSizes().stream()
                        .filter(i -> i.getSize().equals(cartItem.getSize())).findFirst().get().getPrice();
                // int quantity = product.getSku_products().get(cartItem.getStyle()).getSizes().stream()
                //         .filter(i -> i.getSize().equals(i.getSize())).findFirst().get().getQuantity();
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
        return null;
    }

    public List<ProductInfoDto> loadCart(String userId) {


        ObjectMapper mapper = new ObjectMapper();
            ServiceUserDto response = mapper.convertValue(userServiceClient.findUserByEmail(userId).getBody(), 
            ServiceUserDto.class);

        Optional<Cart> cart = cartRepository.findByUserId(Long.parseLong(response.getUserId()));
        // .orElseThrow(()->new RuntimeException("Cart not found"));

        if (cart.isPresent()) {

            List<CartProduct> products = cart.get().getCartProducts();

            List<ProductInfoDto> result = new ArrayList<>();
            for (CartProduct p : products) {
                
                ProductDto product = catalogServiceClient.getProductInfo(Long.toString(p.getProductId())).getBody();
                
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
                ProductInfoDto dto = mapper.convertValue(catalogServiceClient.getCartProductInfo(Long.toString(p.getProductId()),
                         p.getStyle(), index).getBody(), ProductInfoDto.class);

                int originalPrice = product.getSku_products().get(p.getStyle()).getSizes().stream()
                        .filter(i -> i.getSize().equals(p.getSize())).findFirst().get().getPrice();
                int discount = product.getSku_products().get(p.getStyle()).getDiscount();

                dto.setPriceBefore(originalPrice);
                dto.setQty(p.getQty());
                dto.set_uid(p.get_uid());

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
        return null;

    }

    public Cart saveCart(CartRequest request) {

        
        if (request.getProducts().size() > 0) {

            ObjectMapper mapper = new ObjectMapper();
            ServiceUserDto response = mapper.convertValue(userServiceClient.findUserByEmail(request.getEmail()).getBody(), 
            ServiceUserDto.class);
            
                Optional<Cart> existed = cartRepository.findByUserId(Long.parseLong(response.getUserId()));
                if (existed.isPresent()) {

                    cartRepository.delete(existed.get());
                }
            

            Cart cart = new Cart();
            cart.setUserId(Long.parseLong(response.getUserId()));

            List<CartProduct> products = new ArrayList<CartProduct>();

            for (ProductInfoDto cartItem : request.getProducts()) {

                
                ProductDto product = catalogServiceClient.getProductInfo(cartItem.getId()).getBody();

                if (null != product) {
                    ProductSkuDto sku = product.getSku_products().get(cartItem.getStyle());

                    int price = sku.getSizes().stream().filter(p -> p.getSize().equals(cartItem.getSize()))
                            .findFirst().get().getPrice();
                    if (sku.getDiscount() > 0)
                        price = (price - price / sku.getDiscount());
                    

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
                            .shipping(product.getShipping())
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

    

    // public CouponResponse applyCoupon(CouponRequest request, String username) {

    //     Optional<User> user = userRepository.findByUserName(username);

    //     if (user.isPresent()) {

    //         Optional<Coupon> coupon = couponRepository.findById(Long.parseLong(request.getCoupon().getId()));

    //         if (!coupon.isPresent()) {
    //             return null;
    //         }

    //         Optional<Cart> cart = cartRepository.findByUser_UserName(username);

    //         int totalAfterDiscount = (cart.get().getCartTotal() * coupon.get().getDiscount()) / 100;

    //         cart.get().setTotalAfterDiscount(totalAfterDiscount);

    //         cartRepository.save(cart.get());

    //         CouponResponse result = new CouponResponse(totalAfterDiscount, coupon.get().getDiscount());

    //         return result;
    //     }
    //     return null;

    // }

}
