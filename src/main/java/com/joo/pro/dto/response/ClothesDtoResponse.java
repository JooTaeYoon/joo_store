package com.joo.pro.dto.response;

import com.joo.pro.entity.Clothes;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ClothesDtoResponse {

    private LocalDate createdAt;

    private String clothesType;

    private Integer count;

    private String comment;

    private String category;

    private String price;

    private String pickUp;

    private String status;

    private String serviceType;

    public static ClothesDtoResponse fromEntity(Clothes clothes) {
        return ClothesDtoResponse.builder()
                .clothesType(clothes.getClothesType())
                .category(clothes.getCategory() != null ? clothes.getCategory().getKorean() : "")
                .serviceType(clothes.getServiceType() != null ? clothes.getServiceType().getKoreanName(): "")
                .count(clothes.getClothesType().length())
                .comment(clothes.getComment())
                .status(clothes.getStatus() != null ?  clothes.getStatus().getKorean() : "")
                .pickUp(clothes.getPickup() != null ? clothes.getPickup().getKorean() : "")
                .price(clothes.getPrice())
                .createdAt(clothes.getCreatedAt().toLocalDate())
                .build();
    }

    public static List<ClothesDtoResponse> fromEntities(List<Clothes> clothesList) {
        return clothesList.stream()
                .map(ClothesDtoResponse::fromEntity)
                .toList();
    }
}
