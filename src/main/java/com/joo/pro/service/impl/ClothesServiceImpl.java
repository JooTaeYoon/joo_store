package com.joo.pro.service.impl;

import com.joo.pro.dto.request.ClothesDtoRequest;
import com.joo.pro.dto.request.OrderRequest;
import com.joo.pro.dto.request.PickupDtoRequest;
import com.joo.pro.dto.response.ClothesDtoResponse;
import com.joo.pro.dto.response.OrderResponse;
import com.joo.pro.entity.Clothes;
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
    public OrderResponse saveClothes(Long id, OrderRequest request) {

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
            order.getClothes().add(clothes);
            clothesRepository.save(clothes);
        }
        orderRepository.save(order);
        return OrderResponse.fromEntity(order);
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
    public OrderResponse saveUpdatedHistory(Long id, OrderRequest request) {
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
            order.getClothes().add(clothes);
            clothesRepository.deleteById(id);
            clothesRepository.save(clothes);
        }
        orderRepository.deleteById(id);
        orderRepository.save(order);
        return OrderResponse.fromEntity(order);
    }

}