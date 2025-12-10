package com.joo.pro.repository;

import com.joo.pro.dto.response.ClothesDtoResponse;
import com.joo.pro.entity.Clothes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClothesRepository extends JpaRepository<Clothes, Long> {


    Optional<Clothes> findByIdAndOrderIdCustomerId(Long clothesId, Long customerId);

}
