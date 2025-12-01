package com.joo.pro.controller;

import com.joo.pro.dto.request.CustomerDtoRequest;
import com.joo.pro.dto.response.CustomerDtoResponse;
import com.joo.pro.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/store")
@RestController
@Slf4j
@RequiredArgsConstructor
public class StoreController {

    private final CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<CustomerDtoResponse> createCustomer(@RequestBody CustomerDtoRequest request) {
        CustomerDtoResponse customer = customerService.createCustomer(request);
        log.info("customer: {}", customer);
        return ResponseEntity.ok(customer);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<CustomerDtoResponse> update(@RequestBody CustomerDtoRequest request, @PathVariable("id") Long id) {
        log.info("update 호출됨");
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }
}
