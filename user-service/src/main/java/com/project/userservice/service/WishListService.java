package com.project.userservice.service;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.userservice.client.CatalogServiceClient;
import com.project.userservice.constants.OperationStatus;
import com.project.userservice.dto.ProductDto;
import com.project.userservice.model.User;
import com.project.userservice.model.WishList;
import com.project.userservice.repository.UserRepository;
import com.project.userservice.repository.WishiListRepository;
import com.project.userservice.security.request.WishListRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishListService {

    private final UserRepository userRepository;
    private final WishiListRepository wishiListRepository;
    private final CatalogServiceClient catalogServiceClient;

    public OperationStatus addWishList(String username, WishListRequest request) {

        Long id = Long.parseLong(request.getId());

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (null != user) {

            // Product product = productRepository.findById(id)
            //         .orElseThrow(() -> new RuntimeException("Product not found"));
            ResponseEntity<ProductDto> product = catalogServiceClient.getProductInfo(request.getId());

            if (null != product) {

                // for (WishList w : user.getWishLists()) {
                //     log.info(Long.toString(w.getProduct().getProductId()) +
                //             w.getStyle());
                // }

                Optional<WishList> existed = user.getWishLists().stream()
                        .filter(x -> x.getProductId() == id && x.getStyle().equals(request.getStyle()))
                        .findFirst();

                if (existed.isPresent()) {

                    return OperationStatus.OS_ALREADY_EXISTED;

                } else {

                    WishList wish = new WishList();
                    wish.setStyle(request.getStyle());
                    wish.setProductId(id);

                    user.getWishLists().add(wish);

                    wish.setUser(user);

                    wishiListRepository.save(wish);

                    return OperationStatus.OS_SUCCESS;
                }

            }
        }

        return OperationStatus.OS_FAILED;
    }

}
