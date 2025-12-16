package com.joo.pro.service;

import com.joo.pro.dto.request.CustomerDtoRequest;
import com.joo.pro.dto.response.ClothesDtoResponse;
import com.joo.pro.dto.response.CustomerDtoResponse;

import java.util.List;

public interface CustomerService {

    CustomerDtoResponse createCustomer(CustomerDtoRequest request);

    CustomerDtoResponse updateCustomer(Long id, CustomerDtoRequest request);

    List<CustomerDtoResponse> readAllCustomers();

    List<CustomerDtoResponse> getCustomer(CustomerDtoRequest request);

    List<ClothesDtoResponse> getCustomerClothes(Long id);

    CustomerDtoResponse getCustomerInfo(Long id);

}
