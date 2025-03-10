package com.project.order_service.ports.input.message.listener;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.project.common.message.dto.request.UserCreatedRequest;
import com.project.order_service.domain.exception.OrderDomainException;
import com.project.order_service.model.Customer;
import com.project.order_service.repository.CustomerRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerMessageListener {

    private final CustomerRepository customerRepository;

    
    @Transactional
    void customerCreated(UserCreatedRequest createdUser) {

        log.info("User id : {}", createdUser.getUserId());

        

        try {
            Customer customer = customerRepository.saveAndFlush(Customer.builder().
                                    id(createdUser.getUserId()).        
                                    username(createdUser.getUsername()).
                                    nickname(createdUser.getNickname()).
                                    email(createdUser.getEmail()).
                                    build());
            
            if (customer == null) {
                log.error("Customer could not be created in order database with id: {}", createdUser.getUserId());
                throw new OrderDomainException("Customer could not be created in order database with id " +
                createdUser.getUserId());
            }
            log.info("Customer is created in order database with id: {}", customer.getId());
        }
        catch(DataIntegrityViolationException err) {
            log.error(err.getMessage());
        }


    }
}
