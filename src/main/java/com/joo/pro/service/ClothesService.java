package com.joo.pro.service;

import com.joo.pro.dto.request.OrderRequest;
import com.joo.pro.dto.request.PickupDtoRequest;
import com.joo.pro.dto.response.ClothesDtoResponse;
import com.joo.pro.dto.response.OrderResponse;

import java.util.List;

public interface ClothesService {

    OrderResponse saveClothes(Long id, List<OrderRequest> clothesType);

    List<ClothesDtoResponse> getClothes(Long id, PickupDtoRequest clothesIdList);


}
