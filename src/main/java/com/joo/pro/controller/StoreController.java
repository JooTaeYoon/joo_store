package com.joo.pro.controller;

import com.joo.pro.dto.response.CustomerDtoResponse;
import com.joo.pro.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/store")
@RestController
@Slf4j
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/create")
    public ResponseEntity<CustomerDtoResponse> createCustomer() {
        log.info("createCustomer 호출됨");
        return ResponseEntity.ok(new CustomerDtoResponse());
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        log.info("test 호출됨");
        return ResponseEntity.ok("test 성공");
    }

}
