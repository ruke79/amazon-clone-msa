package com.project.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.backend.model.Address;
import com.project.backend.model.ShippingAddress;
import com.project.backend.model.User;
import com.project.backend.dto.AddressDTO;
import com.project.backend.repository.ShippingAddressRepository;
import com.project.backend.repository.UserRepository;
import com.project.backend.security.request.AddressRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AddressService {

    
    private final ShippingAddressRepository shippingAddressRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public AddressService(ShippingAddressRepository shippingAddressRepository, UserRepository userRepository) {
        this.shippingAddressRepository = shippingAddressRepository;
        this.userRepository = userRepository;
    }


    public List<AddressDTO> saveShippingAddress(AddressRequest request, String username) {
        
        ShippingAddress address = new ShippingAddress();

        deepCopyShippingAddress(address, request.getAddress());        

        User user = userRepository.findByUserName(username)
        .orElseThrow(()->new RuntimeException("User not found"));
        
        address.setUser(user);

        user.getShippingAddresses().add(address);

        shippingAddressRepository.save(address);              

        List<AddressDTO> result = new ArrayList<>();
        for ( ShippingAddress src : user.getShippingAddresses()) {
            AddressDTO dto = new AddressDTO();
            deepCopyShippingAddressDTO(dto, src);
            result.add(dto);
        }

        return result;
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

        address.setId(Long.toString(src.getShippingAddressId()));
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

    


}
