package com.joo.pro.service;

import com.joo.pro.dto.request.CustomerDtoRequest;
import com.joo.pro.dto.response.CustomerDtoResponse;

public interface CustomerService {

    CustomerDtoResponse createCustomer(CustomerDtoRequest request);

    CustomerDtoResponse updateCustomer(Long id, CustomerDtoRequest request);

}
