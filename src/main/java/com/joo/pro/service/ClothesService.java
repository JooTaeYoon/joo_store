package com.joo.pro.service;

import com.joo.pro.dto.request.ClothesDtoRequest;
import com.joo.pro.dto.response.ClothesDtoResponse;

public interface ClothesService {

    ClothesDtoResponse saveClothes(Long id, ClothesDtoRequest clothesType);

}
