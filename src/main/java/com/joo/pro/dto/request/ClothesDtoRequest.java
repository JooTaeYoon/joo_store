package com.joo.pro.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.joo.pro.entity.Clothes;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClothesDtoRequest {

    private String clothesType;

    private Integer count;

    private String comment;

    private Clothes.SERVICE_TYPE serviceType;

    private Clothes.CATEGORY category;

    private Clothes.STATUS status;

}
