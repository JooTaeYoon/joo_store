package com.joo.pro.service;

import com.joo.pro.dto.request.OrderRequest;
import com.joo.pro.dto.response.OrderResponse;

public interface ClothesService {

    OrderResponse saveClothes(Long id, OrderRequest clothesType);

}
