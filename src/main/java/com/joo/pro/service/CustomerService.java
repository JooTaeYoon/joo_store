package com.joo.pro.service;

import com.joo.pro.dto.request.CustomerDtoRequest;
import com.joo.pro.dto.response.ClothesDtoResponse;
import com.joo.pro.dto.response.CustomerDtoResponse;
import com.joo.pro.dto.response.SearchCustomerResponse;
import com.joo.pro.entity.Customer;

import java.util.List;

public interface CustomerService {

    CustomerDtoResponse createCustomer(CustomerDtoRequest request);

    CustomerDtoResponse updateCustomer(Long id, CustomerDtoRequest request);

    List<CustomerDtoResponse> readAllCustomers();

    List<CustomerDtoResponse> getCustomer(CustomerDtoRequest request);

    SearchCustomerResponse getCustomerInfo(Long id);

    List<CustomerDtoResponse> findByOrderCustomerId(String request) throws IllegalAccessException;

}
