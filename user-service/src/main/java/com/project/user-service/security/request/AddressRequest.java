package com.project.user-service.security.request;


import com.project.user-service.dto.AddressDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddressRequest {

    private AddressDTO address;
        
}
