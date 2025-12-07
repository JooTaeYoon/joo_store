package com.joo.pro.dto.request;

import com.joo.pro.entity.Clothes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    private Integer count;

    private List<ClothesDtoRequest> clothesList;

}

