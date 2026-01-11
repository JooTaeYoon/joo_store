package com.joo.pro.controller;

import com.joo.pro.dto.request.ClothesDtoRequest;
import com.joo.pro.dto.request.CustomerDtoRequest;
import com.joo.pro.dto.request.OrderRequest;
import com.joo.pro.dto.request.PickupDtoRequest;
import com.joo.pro.dto.response.ClothesDtoResponse;
import com.joo.pro.dto.response.CustomerDtoResponse;
import com.joo.pro.dto.response.SearchCustomerResponse;
import com.joo.pro.entity.Customer;
import com.joo.pro.service.ClothesService;
import com.joo.pro.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/store")
@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "StoreController", description = "옷가게 컨트롤러")
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
    @Operation(summary = "손님 등록", description = "손님 이름, 손님 번호를 받아 손님을 등록합니다.")
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
    @Operation(summary = "손님 정보 수정", description = "손님 이름을 받아 손님 정보를 수정합니다.")
    public ResponseEntity<CustomerDtoResponse> update(@RequestBody CustomerDtoRequest request, @PathVariable("id") Long id) {
        log.info("update 호출됨");
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    /**
     * 손님 한명의 정보 가져오기
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}/get")
    @Operation(summary = "손님 정보 가져오기", description = "손님 정보 가져오기")
    public ResponseEntity<SearchCustomerResponse> getCustomerInfo(@PathVariable("id") Long id) {
        return ResponseEntity.ok(customerService.getCustomerInfo(id));
    }

    /**
     * 손님 이름 및 번호(뒷자리 4자리)로 검색
     *
     * @param request
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<List<CustomerDtoResponse>> searchCustomers(@RequestParam("query") String query) throws IllegalAccessException {
        log.info("request = {}", query);
        List<CustomerDtoResponse> byOrderCustomerId = customerService.findByOrderCustomerId(query);
        log.info("byOrderCustomerId = {}", byOrderCustomerId);

        return ResponseEntity.ok(byOrderCustomerId);
    }

    /**
     * 옷 정보 수정
     *
     * @param id      손님 번호
     * @param request 수정 할 옷 정보
     * @return
     */
    @PutMapping("/{id}/update")
    public ResponseEntity<?> saveUpdatedHistory(@PathVariable("id") Long id, @RequestBody OrderRequest request) {
        log.info("updateClothesStatus: {}", request);
        clothesService.saveUpdatedHistory(id, request);
        return ResponseEntity.ok().build();
    }


    /**
     * 모든 손님 정보 가져오기
     *
     * @return
     */
    @GetMapping("/get")
    @Operation(summary = "모든 손님 정보 가져오기", description = "모든 손님 정보를 가져옵니다.")
    public ResponseEntity<List<CustomerDtoResponse>> get() {
        return ResponseEntity.ok(customerService.readAllCustomers());
    }

    /**
     * 한명 손님 정보 가져오기
     *
     * @param request
     * @return
     */
    @GetMapping("/customer-one")
    @Operation(summary = "검색해서 한명 손님 정보 가져오기", description = "한명 손님 정보를 가져옵니다.")
    public ResponseEntity<List<CustomerDtoResponse>> getCustomer(@RequestBody CustomerDtoRequest request) {
        return ResponseEntity.ok(customerService.getCustomer(request));
    }

    /**
     * 손님 옷 저장
     *
     * @param id      손님 id
     * @param request 옷 정보
     * @return
     */
    @PostMapping("/{id}/save/clothes")
    @Operation(summary = "손님 옷 저장", description = "손님이 맡긴 옷 정보를 저장합니다.")
    public ResponseEntity<?> saveClothesToCustomer(@PathVariable("id") Long id, @RequestBody OrderRequest request) {
        return ResponseEntity.ok(clothesService.saveClothes(id, request));
    }

    /**
     * 옷 찾기
     *
     * @param id      손님 id
     * @param request 옷 정보
     * @return
     */
    @GetMapping("/customer/{id}/clothes")
    @ApiResponse(description = "옷 찾기")
    @Operation(summary = "옷 찾기", description = "손님이 맡긴 옷 정보를 가져옵니다.")
    public ResponseEntity<List<ClothesDtoResponse>> getClothesFromCustomer(@PathVariable("id") Long id) {
        log.info("getClothesFromCustomer 호출됨");
        return ResponseEntity.ok(clothesService.getClothes(id));
    }
}
