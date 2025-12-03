package com.joo.pro.dto.response;

import com.joo.pro.entity.Clothes;
import lombok.*;

@Data
@Builder
public class ClothesDtoResponse {

    private String clothesType;

    public static ClothesDtoResponse fromEntity(Clothes clothes) {
        return ClothesDtoResponse.builder()
                .clothesType(clothes.getClothesType())
                .build();
    }

}
