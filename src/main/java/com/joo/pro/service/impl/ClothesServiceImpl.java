package com.joo.pro.service.impl;

import com.joo.pro.dto.request.ClothesDtoRequest;
import com.joo.pro.dto.request.OrderRequest;
import com.joo.pro.dto.request.PickupDtoRequest;
import com.joo.pro.dto.response.ClothesDtoResponse;
import com.joo.pro.dto.response.OrderResponse;
import com.joo.pro.entity.Clothes;
import com.joo.pro.entity.Customer;
import com.joo.pro.entity.Order;
import com.joo.pro.repository.ClothesRepository;
import com.joo.pro.repository.OrderRepository;
import com.joo.pro.repository.CustomerRepository;
import com.joo.pro.service.ClothesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClothesServiceImpl implements ClothesService {

    private final ClothesRepository clothesRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderResponse saveClothes(Long id, List<OrderRequest> request) {

//        고객 찾기
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 고객이 존재하지 않습니다."));

        Order order = Order.builder()
//                .count(request.get(0).getCount())
                .customer(customer)
//                .clothes(request.getClothesList())
                .build();

        for (OrderRequest dto : request) {
            Clothes.SERVICE_TYPE serviceType = Clothes.SERVICE_TYPE.valueOf(dto.getServiceType().toUpperCase());
            Clothes clothes = Clothes.builder()
                    .clothesType(dto.getClothesType())
                    .serviceType(serviceType)
                    .category(Clothes.CATEGORY.valueOf(dto.getCategory().toUpperCase()))
                    .comment(dto.getComment())
                    .status(Clothes.STATUS.valueOf(dto.getStatus().toUpperCase()))
                    .price(dto.getPrice())
                    .orderId(order)
                    .build();
            order.getClothes().add(clothes);
            clothesRepository.save(clothes);
        }
        orderRepository.save(order);
        return OrderResponse.fromEntity(order);
    }

    @Override
    @Transactional
    public List<ClothesDtoResponse> getClothes(Long id, PickupDtoRequest clothesId) {

        List<Clothes> clothes = new ArrayList<>();

        System.out.println("clothesId = " + clothesId);

        customerRepository
                .findById(id).orElseThrow(() -> new RuntimeException("해당 고객이 존재하지 않습니다."));

        for (Long clothesIds : clothesId.getClothesId()) {
//            clothes = clothesRepository.findById(clothesIds)
//                    .orElseThrow(() -> new RuntimeException("해당 옷이 존재하지 않습니다."));
            Optional<Clothes> byIdAndOrderIdCustomerId = clothesRepository.findByIdAndOrderIdCustomerId(clothesIds, id);
            byIdAndOrderIdCustomerId
                    .ifPresentOrElse(
                            c -> {
                                log.info("c >>> {}", c);
                                // TODO:  옷 찾아감 구현
//                                c.setStatus(Clothes.STATUS.PARTIAL);
                            },
                            () -> {
                                throw new RuntimeException("해당 옷이 존재하지 않습니다.");
                            }
                    );
            clothes.add(byIdAndOrderIdCustomerId.get());
        }

        return ClothesDtoResponse.fromEntities(clothes);
    }

}