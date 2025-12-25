package com.joo.pro.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.joo.pro.entity.Clothes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
@Schema(description = "옷 찾기 요청 DTO")
public class ClothesDtoRequest {

    private String clothesType;

    private Integer count;

    private String comment;

    private String price;

    private Clothes.SERVICE_TYPE serviceType;

    private Clothes.CATEGORY category;

    private Clothes.STATUS status;

    private Clothes.PICKUP pickup;

}
