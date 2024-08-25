package com.project.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.backend.model.Address;
import com.project.backend.model.ShippingAddress;
import com.project.backend.model.User;
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

        

        address.setAddress1(request.getAddress().getAddress1());
        address.setAddress2(request.getAddress().getAddress2());
        address.setCity(request.getAddress().getCity());
        address.setState(request.getAddress().getState());
        address.setCountry(request.getAddress().getCountry());
        address.setFirstname(request.getAddress().getFirstname());
        address.setLastname(request.getAddress().getLastname());
        address.setPhoneNumber(request.getAddress().getPhoneNumber());
        address.setZipCode(request.getAddress().getZipCode());

        Optional<User> user = userRepository.findByEmail(request.getUserId());
        
        address.setUser(user.get());

        return shippingAddressRepository.save(address);              
    }

    public Address saveUserAddress(AddressRequest request) {

        Address address = new Address();

        deepCopyUserAddress(address, request);

        return addressRepository.save(address);              
    }

    private void deepCopyUserAddress(Address address, AddressRequest request) {
        
        address.setAddress1(request.getAddress().getAddress1());
        address.setAddress2(request.getAddress().getAddress2());
        address.setCity(request.getAddress().getCity());
        address.setState(request.getAddress().getState());
        address.setZipCode(request.getAddress().getZipCode());
    }

    public Address updateUserAddress(AddressRequest request) {

        Optional<User> user = userRepository.findByEmail(request.getUserId());

        if (user.isPresent()) {

            Address address = userRepository.findAddressByEmail(request.getUserId());

            deepCopyUserAddress(address, request);

            return addressRepository.save(address);
        }
        
        return null;
    }

    //public ShippingAddress updateShippingAddress(AddressRequest request) {}

}
