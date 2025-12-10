package com.joo.pro.controller;

import com.joo.pro.dto.request.CustomerDtoRequest;
import com.joo.pro.dto.request.OrderRequest;
import com.joo.pro.dto.request.PickupDtoRequest;
import com.joo.pro.dto.response.CustomerDtoResponse;
import com.joo.pro.service.ClothesService;
import com.joo.pro.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/store")
@RestController
@Slf4j
@RequiredArgsConstructor
public class StoreController {

    private final CustomerService customerService;
    private final ClothesService clothesService;

    /**
     * 손님 등록
     *
     * @param request 손님 이름, 손님 번호
     * @return request와 동일값
     */
    @PostMapping("/create")
    public ResponseEntity<CustomerDtoResponse> createCustomer(@RequestBody CustomerDtoRequest request) {
        CustomerDtoResponse customer = customerService.createCustomer(request);
        log.info("customer: {}", customer);
        return ResponseEntity.ok(customer);
    }

    /**
     * 손님 정보 수정
     *
     * @param request 손님 이름
     * @param id      수정 할 손님 번호
     * @return 변경 된 손님 정보
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<CustomerDtoResponse> update(@RequestBody CustomerDtoRequest request, @PathVariable("id") Long id) {
        log.info("update 호출됨");
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @GetMapping("/read")
    public ResponseEntity<List<CustomerDtoResponse>> read() {
        return ResponseEntity.ok(customerService.readAllCustomers());
    }

    @GetMapping("/customer-one")
    public ResponseEntity<?> getCustomer(@RequestBody CustomerDtoRequest request) {
        return ResponseEntity.ok(customerService.getCustomer(request));
    }

    @PostMapping("/{id}/save/clothes")
    public ResponseEntity<?> saveClothesToCustomer(@PathVariable("id") Long id, @RequestBody OrderRequest request) {
        return ResponseEntity.ok(clothesService.saveClothes(id, request));
    }

    /**
     * 옷 찾기
     * @param id 손님 id
     * @param request 옷 정보
     * @return
     */
    @PostMapping("/{id}/get/clothes")
    public ResponseEntity<?> getClothesFromCustomer(@PathVariable("id") Long id, @RequestBody PickupDtoRequest clothesId){
        System.out.println("hi");
        return ResponseEntity.ok(clothesService.getClothes(id, clothesId));
    }
}
