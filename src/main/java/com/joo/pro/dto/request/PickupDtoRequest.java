package com.joo.pro.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PickupDtoRequest {

    private List<Long> clothesId;

}
