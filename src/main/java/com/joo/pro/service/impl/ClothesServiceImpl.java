package com.joo.pro.service.impl;

import com.joo.pro.dto.request.ClothesDtoRequest;
import com.joo.pro.dto.response.ClothesDtoResponse;
import com.joo.pro.entity.Clothes;
import com.joo.pro.entity.Customer;
import com.joo.pro.repository.ClothesRepository;
import com.joo.pro.repository.CustomerRepository;
import com.joo.pro.service.ClothesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClothesServiceImpl implements ClothesService {

    private final ClothesRepository clothesRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public ClothesDtoResponse saveClothes(Long id, ClothesDtoRequest clothesType) {
        return customerRepository.findById(id).map((customer) -> {
            Clothes clothes = Clothes.builder().clothesType(clothesType.getClothesType()).customer(customer).build();
            log.info("clothes: {}", clothes);
            clothesRepository.save(clothes);
            return ClothesDtoResponse.fromEntity(clothes);
        }).orElseThrow(() -> new RuntimeException("고객이 없습니다."));
    }
}
