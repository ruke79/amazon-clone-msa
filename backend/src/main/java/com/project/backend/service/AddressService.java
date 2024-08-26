package com.project.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.backend.model.Address;
import com.project.backend.model.ShippingAddress;
import com.project.backend.model.User;
import com.project.backend.dto.AddressDTO;
import com.project.backend.repository.AddressRepository;
import com.project.backend.repository.ShippingAddressRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.security.request.AddressRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AddressService {

    @Autowired 
    ShippingAddressRepository shippingAddressRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserRepository userRepository;
    
    
    public ShippingAddress saveShippingAddress(AddressRequest request) {

        ShippingAddress address = new ShippingAddress();

        
        deepCopyShippingAddress(address, request.getAddress());        

        Optional<User> user = userRepository.findByEmail(request.getUserId());
        
        address.setUser(user.get());

        return shippingAddressRepository.save(address);              
    }

    public Address saveUserAddress(AddressRequest request) {

        Address address = new Address();

        deepCopyUserAddress(address, request.getAddress());

        return addressRepository.save(address);              
    }

    static public void deepCopyShippingAddress(ShippingAddress address, AddressDTO src) {

        address.setAddress1(src.getAddress1());
        address.setAddress2(src.getAddress2());
        address.setCity(src.getCity());
        address.setState(src.getState());
        address.setCountry(src.getCountry());
        address.setFirstname(src.getFirstname());
        address.setLastname(src.getLastname());
        address.setPhoneNumber(src.getPhoneNumber());
        address.setZipCode(src.getZipCode());
    }

    static public void deepCopyShippingAddressDTO(AddressDTO address, ShippingAddress src ) {

        address.setAddress1(src.getAddress1());
        address.setAddress2(src.getAddress2());
        address.setCity(src.getCity());
        address.setState(src.getState());
        address.setCountry(src.getCountry());
        address.setFirstname(src.getFirstname());
        address.setLastname(src.getLastname());
        address.setPhoneNumber(src.getPhoneNumber());
        address.setZipCode(src.getZipCode());
    }

    private void deepCopyUserAddress(Address address, AddressDTO src) {
        
        address.setAddress1(src.getAddress1());
        address.setAddress2(src.getAddress2());
        address.setCity(src.getCity());
        address.setState(src.getState());
        address.setZipCode(src.getZipCode());
    }

    public Address updateUserAddress(AddressRequest request) {

        Optional<User> user = userRepository.findByEmail(request.getUserId());

        if (user.isPresent()) {

            Address address = userRepository.findAddressByEmail(request.getUserId());

            deepCopyUserAddress(address, request.getAddress());

            return addressRepository.save(address);
        }
        
        return null;
    }

    //public ShippingAddress updateShippingAddress(AddressRequest request) {}

}
