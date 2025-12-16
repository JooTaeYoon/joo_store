package com.joo.pro.service.impl;

import com.joo.pro.dto.request.CustomerDtoRequest;
import com.joo.pro.dto.response.ClothesDtoResponse;
import com.joo.pro.dto.response.CustomerDtoResponse;
import com.joo.pro.entity.Clothes;
import com.joo.pro.entity.Customer;
import com.joo.pro.repository.ClothesRepository;
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
    private final ClothesRepository clothesRepository;

    @Override
    @Transactional
    public List<CustomerDtoResponse> getCustomer(CustomerDtoRequest request) {
        List<Customer> byName = customerRepository
                .findAllByName(request.getName());
        return CustomerDtoResponse.fromEntityList(byName);
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
            if (request.getPhoneNumber() != null) {
                c.setPhoneNumber(request.getPhoneNumber());
            }
            Customer update = customerRepository.save(c);
            return CustomerDtoResponse.fromEntity(update);
        }).orElseThrow(() -> new RuntimeException("해당 고객이 존재하지 않습니다."));
    }

    @Override
    public List<ClothesDtoResponse> getCustomerClothes(Long id) {
        List<Clothes> byIdAndOrderIdCustomerId = clothesRepository.findAllByOrderIdCustomerId(id);
        return ClothesDtoResponse.fromEntities(byIdAndOrderIdCustomerId);
    }

    @Override
    public CustomerDtoResponse getCustomerInfo(Long id) {
        return customerRepository.findById(id)
                .map(CustomerDtoResponse::fromEntity)
                .orElseThrow(() -> new RuntimeException("해당 고객이 존재하지 않습니다."));
    }
}
