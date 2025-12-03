package com.joo.pro.service.impl;

import com.joo.pro.dto.request.CustomerDtoRequest;
import com.joo.pro.dto.response.CustomerDtoResponse;
import com.joo.pro.entity.Customer;
import com.joo.pro.repository.CustomerRepository;
import com.joo.pro.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {


    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerDtoResponse getCustomer(CustomerDtoRequest request) {
        Customer byName = customerRepository
                .findByName(request.getName());
        return CustomerDtoResponse.fromEntity(byName);
    }

    @Override
    @Transactional
    public CustomerDtoResponse createCustomer(CustomerDtoRequest request) {
        Customer customer = Customer.builder()
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .build();
        Customer save = customerRepository.save(customer);
        return CustomerDtoResponse.fromEntity(save);
    }

    @Override
    @Transactional
    public List<CustomerDtoResponse> readAllCustomers() {
        List<Customer> all = customerRepository
                .findAll();
        return CustomerDtoResponse.fromEntities(all);
    }

    @Override
    @Transactional
    public CustomerDtoResponse updateCustomer(Long id, CustomerDtoRequest request) {
        return customerRepository.findById(id).map(c -> {
            c.setName(request.getName());
            c.setPhoneNumber(request.getPhoneNumber());
            Customer update = customerRepository.save(c);
            return CustomerDtoResponse.fromEntity(update);
        }).orElseThrow(() -> new RuntimeException("해당 고객이 존재하지 않습니다."));
    }
}
