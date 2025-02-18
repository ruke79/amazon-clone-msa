package com.project.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.backend.constants.OperationStatus;
import com.project.backend.dto.AddressDTO;
import com.project.backend.dto.CartDTO;
import com.project.backend.dto.CartProductDTO;
import com.project.backend.dto.ColorAttributeDTO;
import com.project.backend.dto.ProductInfoDTO;
import com.project.backend.model.Cart;
import com.project.backend.model.CartProduct;
import com.project.backend.model.Coupon;
import com.project.backend.model.Product;
import com.project.backend.model.ProductColorAttribute;
import com.project.backend.model.ProductSizeAttribute;
import com.project.backend.model.ProductSku;
import com.project.backend.model.ShippingAddress;
import com.project.backend.model.User;
import com.project.backend.model.WishList;
import com.project.backend.repository.CartProductRepository;
import com.project.backend.repository.CartRepository;
import com.project.backend.repository.CouponRepository;
import com.project.backend.repository.ProductRepository;
import com.project.backend.repository.ProductSkuRepository;
import com.project.backend.repository.ShippingAddressRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.repository.WishiListRepository;
import com.project.backend.security.request.CartRequest;
import com.project.backend.security.request.CouponRequest;
import com.project.backend.security.request.ProductInfoRequest;
import com.project.backend.security.request.WishListRequest;
import com.project.backend.security.response.CartResponse;
import com.project.backend.security.response.CouponResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CartService {

    private final CartRepository cartRepository;

    private final CartProductRepository cartProductRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final ProductSkuRepository productSkuRepository;

    private final ShippingAddressRepository shippingAddressRepository;

    private final WishiListRepository wishiListRepository;

    private final CouponRepository couponRepository;

    private final ProductService productService;

    @Autowired
    public CartService(CartRepository cartRepository, CartProductRepository cartProductRepository,
            UserRepository userRepository, ProductRepository productRepository,
            ProductSkuRepository productSkuRepository, ShippingAddressRepository shippingAddressRepository,
            WishiListRepository wishiListRepository, CouponRepository couponRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.cartProductRepository = cartProductRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productSkuRepository = productSkuRepository;
        this.shippingAddressRepository = shippingAddressRepository;
        this.wishiListRepository = wishiListRepository;
        this.couponRepository = couponRepository;
        this.productService = productService;
    }

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

    public List<AddressDTO> getShipAddresses(String username, String addressId) {

        Optional<User> user = userRepository.findByUserName(username);

        if (!user.isPresent())
            return null;

        List<ShippingAddress> addresses = shippingAddressRepository.findByUser_UserId(user.get().getUserId());

        List<AddressDTO> addressDTOs = new ArrayList<>();
        for (ShippingAddress address : addresses) {
            AddressDTO dto = new AddressDTO();
            dto.setId(Long.toString(address.getShippingAddressId()));

            dto.setAddress1(address.getAddress1());
            dto.setAddress2(address.getAddress2());
            dto.setCity(address.getCity());
            dto.setState(address.getState());
            dto.setCountry(address.getCountry());
            dto.setFirstname(address.getFirstname());
            dto.setLastname(address.getLastname());
            dto.setPhoneNumber(address.getPhoneNumber());
            dto.setZipCode(address.getZipCode());

            if (address.getShippingAddressId() == Long.parseLong(addressId)) {
                dto.setActive(true);
            } else
                dto.setActive(false);

            addressDTOs.add(dto);
        }

        return addressDTOs;
    }

    public List<AddressDTO> deleteShippingAddress(String username, String addressId) {

        Optional<User> user = userRepository.findByUserName(username);

        if (!user.isPresent())
            return null;

        shippingAddressRepository.deleteById(Long.parseLong(addressId));

        List<ShippingAddress> addresses = shippingAddressRepository.findByUser_UserId(user.get().getUserId());

        List<AddressDTO> addressDTOs = new ArrayList<>();
        for (ShippingAddress address : addresses) {
            AddressDTO dto = new AddressDTO();
            dto.setId(Long.toString(address.getShippingAddressId()));

            dto.setAddress1(address.getAddress1());
            dto.setAddress2(address.getAddress2());
            dto.setCity(address.getCity());
            dto.setState(address.getState());
            dto.setCountry(address.getCountry());
            dto.setFirstname(address.getFirstname());
            dto.setLastname(address.getLastname());
            dto.setPhoneNumber(address.getPhoneNumber());
            dto.setZipCode(address.getZipCode());

            if (address.getShippingAddressId() == Long.parseLong(addressId)) {
                dto.setActive(true);
            } else
                dto.setActive(false);

            addressDTOs.add(dto);
        }

        return addressDTOs;
    }

    public CartDTO getCart(String username) {

        Optional<User> user = userRepository.findByUserName(username);

        if (!user.isPresent())
            return null;

        Optional<Cart> cart = cartRepository.findByUser_UserName(username);

        if (!cart.isPresent())
            return null;

        CartDTO result = new CartDTO();
        result.setUserImage(user.get().getImage());

        List<ShippingAddress> addresses = shippingAddressRepository.findByUser_UserId(user.get().getUserId());

        {
            List<AddressDTO> addressDTOs = new ArrayList<>();
            for (ShippingAddress address : addresses) {
                if (address != null) {
                    AddressDTO dto = new AddressDTO();
                    dto.setId(Long.toString(address.getShippingAddressId()));

                    dto.setAddress1(address.getAddress1());
                    dto.setAddress2(address.getAddress2());
                    dto.setCity(address.getCity());
                    dto.setState(address.getState());
                    dto.setCountry(address.getCountry());
                    dto.setFirstname(address.getFirstname());
                    dto.setLastname(address.getLastname());
                    dto.setPhoneNumber(address.getPhoneNumber());
                    dto.setZipCode(address.getZipCode());
                    dto.setActive(false);

                    addressDTOs.add(dto);
                }
            }
            result.setAddress(addressDTOs);
        }
        // }

        List<CartProductDTO> products = new ArrayList<>();

        for (CartProduct item : cart.get().getCartProducts()) {

            CartProductDTO dto = new CartProductDTO();
            dto.setId(Long.toString(item.getCartproductId()));
            dto.setColor(ColorAttributeDTO.builder()
                    .id(Long.toString(item.getColor().getColorId()))
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

    public List<ProductInfoDTO> updateCart(ProductInfoRequest request) {

        List<ProductInfoDTO> result = new ArrayList<>();

        for (ProductInfoDTO cartItem : request.getProducts()) {

            Product product = productRepository.findById(Long.parseLong(cartItem.getId())).get();

            int originalPrice = product.getSku_products().get(cartItem.getStyle()).getSizes().stream()
                    .filter(i -> i.getSize().equals(cartItem.getSize())).findFirst().get().getPrice();
            int quantity = product.getSku_products().get(cartItem.getStyle()).getSizes().stream()
                    .filter(i -> i.getSize().equals(i.getSize())).findFirst().get().getQuantity();
            int discount = product.getSku_products().get(cartItem.getStyle()).getDiscount();

            ProductInfoDTO dto = new ProductInfoDTO();
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

    public List<ProductInfoDTO> loadCart(String username) {

        Optional<Cart> cart = cartRepository.findByUser_UserName(username);
        // .orElseThrow(()->new RuntimeException("Cart not found"));

        if (cart.isPresent()) {

            List<CartProduct> products = cart.get().getCartProducts();

            List<ProductInfoDTO> result = new ArrayList<>();
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

                ProductInfoDTO dto = productService.getCartProductInfo(p.getProduct().getProductId(),
                        p.getStyle(), index);

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

            for (ProductInfoDTO cartItem : request.getProducts()) {

                
                Optional<Product> product = productRepository.findById(Long.parseLong(cartItem.getId()));

                if (product.isPresent()) {
                    ProductSku sku = product.get().getSku_products().get(cartItem.getStyle());

                    int price = sku.getSizes().stream().filter(p -> p.getSize().equals(cartItem.getSize()))
                            .findFirst().get().getPrice();
                    if (sku.getDiscount() > 0)
                        price = (price - price / sku.getDiscount());

                    ProductColorAttribute color = ProductColorAttribute.builder()
                            .colorId(Long.parseLong(cartItem.getColor().getId()))
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

    public OperationStatus addWishList(String username, WishListRequest request) {

        Long id = Long.parseLong(request.getId());

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (null != user) {

            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (null != product) {

                for (WishList w : user.getWishLists()) {
                    log.info(Long.toString(w.getProduct().getProductId()) +
                            w.getStyle());
                }

                Optional<WishList> existed = user.getWishLists().stream()
                        .filter(x -> x.getProduct().getProductId() == id && x.getStyle().equals(request.getStyle()))
                        .findFirst();

                if (existed.isPresent()) {

                    return OperationStatus.OS_ALREADY_EXISTED;

                } else {

                    WishList wish = new WishList();
                    wish.setStyle(request.getStyle());
                    wish.setProduct(product);

                    user.getWishLists().add(wish);

                    wish.setUser(user);

                    wishiListRepository.save(wish);

                    return OperationStatus.OS_SUCCESS;
                }

            }
        }

        return OperationStatus.OS_FAILED;
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
