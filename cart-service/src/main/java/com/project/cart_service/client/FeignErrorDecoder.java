package com.project.cart_service.client;


import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.project.common.exception.ErrorCode;



@Slf4j
@Component
public class FeignErrorDecoder implements ErrorDecoder {
    Environment env;
    @Autowired
    public FeignErrorDecoder(Environment env) {
        this.env = env;
    }

    @Override
    public Exception decode(String methodKey, Response response) {

        try {
            log.error("methodkey : {} status : {} body : {}", methodKey, response.status(), Util.toString(response.body().asReader(Util.UTF_8)));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        switch (response.status()) {
            case 400:
                break;
            case 404:
                if (methodKey.contains("find")) {
                    throw ErrorCode.throwUserNotFound();
                }                
                break;
            default:
                return new Exception(response.reason());
        }
        return null;
    }
}

