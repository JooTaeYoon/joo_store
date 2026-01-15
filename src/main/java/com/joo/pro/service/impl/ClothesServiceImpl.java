package com.joo.pro.service.impl;

import com.joo.pro.dto.request.ClothesDtoRequest;
import com.joo.pro.dto.request.OrderRequest;
import com.joo.pro.dto.request.PickupDtoRequest;
import com.joo.pro.dto.response.ClothesDtoResponse;
import com.joo.pro.dto.response.OrderResponse;
import com.joo.pro.entity.Clothes;
import com.joo.pro.entity.ClothesPicture;
import com.joo.pro.entity.Customer;
import com.joo.pro.entity.Orders;
import com.joo.pro.repository.ClothesRepository;
import com.joo.pro.repository.OrderRepository;
import com.joo.pro.repository.CustomerRepository;
import com.joo.pro.service.ClothesService;
import com.sun.tools.jconsole.JConsoleContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClothesServiceImpl implements ClothesService {

    private final ClothesRepository clothesRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderResponse saveClothes(Long id, OrderRequest request, List<MultipartFile> files) {

//        고객 찾기
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 고객이 존재하지 않습니다."));

        log.info("customer = {}", customer);

        Orders order = Orders.builder()
                .count(request.getClothesList().size())
                .customer(customer)
//                .clothes(request.getClothesList())
                .build();
        log.info("order = {}", order);

        for (ClothesDtoRequest dto : request.getClothesList()) {
            Clothes.SERVICE_TYPE serviceType = Clothes.SERVICE_TYPE.valueOf(String.valueOf(dto.getServiceType()));
            Clothes clothes = Clothes.builder()
                    .clothesType(dto.getClothesType())
                    .serviceType(serviceType)
//                    .category(Clothes.CATEGORY.valueOf(String.valueOf(dto.getCategory())))
                    .comment(dto.getComment())
//                    .status(Clothes.STATUS.valueOf(String.valueOf(dto.getStatus())))
                    .price(dto.getPrice())
                    .order(order)
//                    .pickup(dto.getPickup())
                    .build();

            if(files != null && !files.isEmpty()){
                for (MultipartFile file : files) {
                    if(!file.isEmpty()){
                        String storedPath = uploadFile(file);

                        ClothesPicture clothesPicture = ClothesPicture.builder()
                                .filePos(storedPath)
                                .clothes(clothes)
                                .originalFileName(file.getOriginalFilename())
                                .build();
                    }
                }
            }

            order.getClothes().add(clothes);
            clothesRepository.save(clothes);
        }


        orderRepository.save(order);
        return OrderResponse.fromEntity(order);
    }

    // 파일 업로드 로직 (예시)
    private String uploadFile(MultipartFile file) {
        // 실제 저장 로직 구현 (UUID 생성 등)
        return "/uploads/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
    }


    @Override
    @Transactional(readOnly = true)
    public List<ClothesDtoResponse> getClothes(Long id) {

        List<Clothes> clothes = new ArrayList<>();

        customerRepository
                .findById(id).orElseThrow(() -> new RuntimeException("해당 고객이 존재하지 않습니다."));

        List<Clothes> byOrderCustomerIdOrderByCreatedAtDesc = clothesRepository.findByOrder_Customer_IdOrderByCreatedAtDesc(id);
        byOrderCustomerIdOrderByCreatedAtDesc
                .forEach(clothes::add);

        byOrderCustomerIdOrderByCreatedAtDesc.forEach((clothes1 -> {

        }));
        log.info("byOrderCustomerIdOrderByCreatedAtDesc = {}", byOrderCustomerIdOrderByCreatedAtDesc);

        return ClothesDtoResponse.fromEntities(clothes);
    }

    /**
     * 옷 정보 수정
     *
     * @param id
     * @param request
     * @return
     */
    @Override
    @Transactional
    public OrderResponse saveUpdatedHistory(Long customerId, OrderRequest request) {
        // 1. 고객 존재 여부 확인
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("해당 고객이 존재하지 않습니다."));

        // 2. 기존 해당 고객의 '모든' 주문 기록 삭제 (연관된 옷들도 Cascade에 의해 삭제되도록 설정 필요)
        // 만약 기존 주문을 유지하지 않고 화면의 데이터로 덮어쓰는 기획이라면 삭제가 먼저입니다.
        orderRepository.deleteByCustomerId(customerId);
        log.info("customerId: {}", customerId);

        // 3. 새로운 주문(Orders) 객체 생성
        Orders order = Orders.builder()
                .count(request.getClothesList().size())
                .customer(customer)
                .clothes(new ArrayList<>()) // 초기화
                .build();

        // 4. DTO 리스트를 엔티티 리스트로 변환하여 주문에 추가
        for (ClothesDtoRequest dto : request.getClothesList()) {
            Clothes clothes = Clothes.builder()
                    .clothesType(dto.getClothesType())
                    .serviceType(dto.getServiceType()) // Enum 타입 체크 필요
                    .comment(dto.getComment())
                    .price(dto.getPrice())
                    .status(dto.getStatus())
                    .pickup(dto.getPickup())
                    .order(order) // 연관관계 설정
                    .build();

            order.getClothes().add(clothes);
        }

        // 5. 저장 (Orders를 저장하면 연관된 Clothes들도 함께 저장됨 - CascadeType.ALL 설정 시)
        Orders savedOrder = orderRepository.save(order);

        log.info("새로운 주문 저장 완료: ID = {}, 옷 수량 = {}", savedOrder.getId(), savedOrder.getClothes().size());

        return OrderResponse.fromEntity(savedOrder);
    }

}