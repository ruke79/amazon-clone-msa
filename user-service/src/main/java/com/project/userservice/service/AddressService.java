package com.project.userservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {

    
    private final ShippingAddressRepository shippingAddressRepository;
    private final UserRepository userRepository;
    
  
    @Transactional
    public AddressDto saveShippingAddress(AddressRequest request, String username) {
        
        User user = userRepository.findByUsername(username)
        .orElseThrow(()->new RuntimeException("User not found"));
             

       ShippingAddress address = new ShippingAddress();

        ShippingAddress.deepCopyShippingAddress(address, request.getAddress());        

        address.setUser(user);

        address = shippingAddressRepository.save(address);       
        
        user.getShippingAddresses().add(address);

        userRepository.save(user);  // prevent java.stackoverflow        

        AddressDto result = new AddressDto();
        AddressDto.deepCopyShippingAddressDto(result, address);

        return result;
    }
        
    
    @Transactional
    public AddressDto selectShipAddress(String username, String addressId) {

        Optional<User> user = userRepository.findByUsername(username);

        if (!user.isPresent())
            return null;

        List<ShippingAddress> addresses = shippingAddressRepository.findByUser_UserId(user.get().getUserId());

                           

        for (ShippingAddress address : addresses) {

            if (address.getShippingAddressId() == Long.parseLong(addressId)) {

                ShippingAddress currActiveAddress = shippingAddressRepository.findByActive(true).orElse(null);
                
                if (currActiveAddress != null) {

                    currActiveAddress.setActive(false);
                    shippingAddressRepository.save(currActiveAddress);
                }

                address.setActive(true);

                address = shippingAddressRepository.save(address);
                
                AddressDto dto = new AddressDto();

                AddressDto.deepCopyShippingAddressDto(dto, address);

                return dto;
            }
        }
        return null;
    }

            


    

    @Transactional(readOnly = true)
    public List<AddressDto> getShipAddresses(String username) {

        Optional<User> user = userRepository.findByUsername(username);

        if (!user.isPresent())
            return null;

        List<ShippingAddress> addresses = shippingAddressRepository.findByUser_UserId(user.get().getUserId());

        List<AddressDto> addressDtos = new ArrayList<>();
        for (ShippingAddress address : addresses) {
            AddressDto dto = new AddressDto();

            AddressDto.deepCopyShippingAddressDto(dto, address);

            addressDtos.add(dto);
        }

        return addressDtos;
    }

    @Transactional
    public void deleteShippingAddress(String username, String addressId) {

        Optional<User> user = userRepository.findByUsername(username);

        if (!user.isPresent())
            return;

        Optional<ShippingAddress> deleteAddress = shippingAddressRepository.findById(Long.parseLong(addressId));
        ;
        if (deleteAddress.isPresent()) {
            user.get().getShippingAddresses().remove(deleteAddress.get());
            shippingAddressRepository.delete(deleteAddress.get());
        }
        
    }



}
