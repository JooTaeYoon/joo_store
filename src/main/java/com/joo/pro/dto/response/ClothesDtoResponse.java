package com.joo.pro.dto.response;

import com.joo.pro.entity.Clothes;
import lombok.*;

@Data
@Builder
public class ClothesDtoResponse {

    private String clothesType;

    private Integer count;

    private String comment;

    private String serviceType;

    private String category;

    private String status;

    public static ClothesDtoResponse fromEntity(Clothes clothes) {
        return ClothesDtoResponse.builder()
                .clothesType(clothes.getClothesType())
                .category(clothes.getCategory().getKorean())
                .serviceType(clothes.getServiceType().getKoreanName())
//                .count(clothes.getCount())
                .comment(clothes.getComment())
                .status(clothes.getStatus().getKorean())
                .build();
    }

}
