package com.project.userservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.userservice.model.ShippingAddress;
import com.project.userservice.model.User;
import com.project.userservice.dto.AddressDto;
import com.project.userservice.repository.ShippingAddressRepository;
import com.project.userservice.repository.UserRepository;
import com.project.userservice.security.request.AddressRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {

    private final ShippingAddressRepository shippingAddressRepository;
    private final UserRepository userRepository;

    public AddressDto saveShippingAddress(AddressRequest request, String username) {
        log.info("saveShippingAddress called with username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User found: {}", user.getUsername());

        ShippingAddress address = ShippingAddress.builder()
                .address1(request.getAddress().getAddress1())
                .address2(request.getAddress().getAddress2())
                .city(request.getAddress().getCity())
                .state(request.getAddress().getState())
                .zipCode(request.getAddress().getZipCode())
                .country(request.getAddress().getCountry())
                .phoneNumber(request.getAddress().getPhoneNumber())
                .active(request.getAddress().isActive())
                .firstname(request.getAddress().getFirstname())
                .lastname(request.getAddress().getLastname())
                .build();

        ShippingAddress.deepCopyShippingAddress(address, request.getAddress());

        address.setUser(user);

        log.info("Saving new address for user: {}", user.getUserId());
        address = shippingAddressRepository.save(address);
        log.info("Address saved with ID: {}", address.getShippingAddressId());

        user.getShippingAddresses().add(address);

        userRepository.save(user); // prevent java.stackoverflow

        AddressDto result = new AddressDto();
        AddressDto.deepCopyShippingAddressDto(result, address);

        log.info("saveShippingAddress completed, returning result: {}", result.getId());

        return result;
    }

    public AddressDto selectShipAddress(String username, String addressId) {
        log.info("selectShipAddress called with username: {}, addressId: {}", username, addressId);

        Optional<User> user = userRepository.findByUsername(username);

        if (!user.isPresent()) {
            log.warn("User not found: {}", username);
            return null;
        }

        log.info("User found, getting addresses for user ID: {}", user.get().getUserId());
        List<ShippingAddress> addresses = shippingAddressRepository.findByUser_UserId(user.get().getUserId());
        log.info("Found {} addresses for user.", addresses.size());

        for (ShippingAddress address : addresses) {
            log.info("Checking address ID: {}", address.getShippingAddressId());
            if (Objects.equals(address.getShippingAddressId(), Long.parseLong(addressId))) {
                log.info("Matching address found, ID: {}", addressId);
                ShippingAddress currActiveAddress = shippingAddressRepository.findByActive(true).orElse(null);

                if (currActiveAddress != null
                        && !Objects.equals(currActiveAddress.getShippingAddressId(), Long.parseLong(addressId))) {
                    log.info("Deactivating current active address with ID: {}",
                            currActiveAddress.getShippingAddressId());
                    currActiveAddress.setActive(false);
                    shippingAddressRepository.save(currActiveAddress);
                }

                address.setActive(true);
                log.info("Activating new address with ID: {}", address.getShippingAddressId());
                address = shippingAddressRepository.save(address);

                AddressDto dto = new AddressDto();
                AddressDto.deepCopyShippingAddressDto(dto, address);

                log.info("selectShipAddress completed, returning active address: {}", dto.getId());

                return dto;
            }
        }
        log.warn("Matching address not found for ID: {}", addressId);
        return null;
    }

    @Transactional(readOnly = true)
    public List<AddressDto> getShipAddresses(String username) {
        log.info("getShipAddresses called with username: {}", username);

        // Optional을 안전하게 다루고, 사용자가 없을 경우 예외를 명확하게 처리
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        log.info("User found, getting all addresses for user ID: {}", user.getUserId());
        List<ShippingAddress> addresses = shippingAddressRepository.findByUser_UserId(user.getUserId());
        log.info("Found {} addresses.", addresses.size());

        // 스트림 API를 사용하여 변환 로직을 간결하게 작성
        List<AddressDto> addressDtos = addresses.stream()
                .map(address -> {
                    AddressDto dto = new AddressDto();
                    AddressDto.deepCopyShippingAddressDto(dto, address);
                    return dto;
                })
                .collect(Collectors.toList());
        log.info("Successfully retrieved {} addresses for user: {}", addressDtos.size(), username);

        return addressDtos;
    }

    public void deleteShippingAddress(String username, String addressId) {
        log.info("deleteShippingAddress called with username: {}, addressId: {}", username, addressId);

        Optional<User> user = userRepository.findByUsername(username);

        if (!user.isPresent()) {
            log.warn("User not found: {}", username);
            return;
        }

        Optional<ShippingAddress> deleteAddress = shippingAddressRepository.findById(Long.parseLong(addressId));
        if (deleteAddress.isPresent()) {
            log.info("Address to delete found, ID: {}", deleteAddress.get().getShippingAddressId());
            user.get().getShippingAddresses().remove(deleteAddress.get());
            shippingAddressRepository.delete(deleteAddress.get());
            log.info("Address deleted successfully.");
        } else {
            log.warn("Address with ID {} not found for deletion.", addressId);
        }
    }
}