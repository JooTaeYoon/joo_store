package com.joo.pro.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PickupDtoRequest {

    @Schema(description = "수거할 옷 ID 리스트", example = "[1, 2, 3]")
    private List<Long> clothesId;

}
