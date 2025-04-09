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
    public List<AddressDto> saveShippingAddress(AddressRequest request, String username) {
        
        User user = userRepository.findByUsername(username)
        .orElseThrow(()->new RuntimeException("User not found"));
             

       ShippingAddress address = new ShippingAddress();

        ShippingAddress.deepCopyShippingAddress(address, request.getAddress());        

        address.setUser(user);

        shippingAddressRepository.save(address);       
       
                
        user.getShippingAddresses().add(address);

        userRepository.save(user);  // prevent java.stackoverflow        

        List<AddressDto> result = new ArrayList<>();
        for ( ShippingAddress src : user.getShippingAddresses()) {
            AddressDto dto = new AddressDto();
            AddressDto.deepCopyShippingAddressDto(dto, src);
            result.add(dto);
        }

        return result;
    }
        
    
    

    @Transactional(readOnly = true)
    public List<AddressDto> getShipAddresses(String username, String addressId) {

        Optional<User> user = userRepository.findByUsername(username);

        if (!user.isPresent())
            return null;

        List<ShippingAddress> addresses = shippingAddressRepository.findByUser_UserId(user.get().getUserId());

        List<AddressDto> addressDtos = new ArrayList<>();
        for (ShippingAddress address : addresses) {
            AddressDto dto = new AddressDto();
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

            addressDtos.add(dto);
        }

        return addressDtos;
    }

    @Transactional
    public List<AddressDto> deleteShippingAddress(String username, String addressId) {

        Optional<User> user = userRepository.findByUsername(username);

        if (!user.isPresent())
            return null;

        Optional<ShippingAddress> deleteAddress = shippingAddressRepository.findById(Long.parseLong(addressId));
        ;
        if (deleteAddress.isPresent()) {
            user.get().getShippingAddresses().remove(deleteAddress.get());
            shippingAddressRepository.delete(deleteAddress.get());
        }

        List<ShippingAddress> addresses = shippingAddressRepository.findByUser_UserId(user.get().getUserId());

        if (addresses.isEmpty()) 
          return null;

        List<AddressDto> addressDtos = new ArrayList<>();
        for (ShippingAddress address : addresses) {
            AddressDto dto = new AddressDto();
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

            // if (address.getShippingAddressId() == Long.parseLong(addressId)) {
            //     dto.setActive(true);
            // } else
            dto.setActive(false);

            addressDtos.add(dto);
        }

        return addressDtos;
    }



}
