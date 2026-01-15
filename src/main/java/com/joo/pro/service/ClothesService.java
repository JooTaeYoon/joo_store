package com.joo.pro.service;

import com.joo.pro.dto.request.OrderRequest;
import com.joo.pro.dto.request.PickupDtoRequest;
import com.joo.pro.dto.response.ClothesDtoResponse;
import com.joo.pro.dto.response.OrderResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClothesService {

    OrderResponse saveClothes(Long id, OrderRequest clothesTypem , List<MultipartFile> files);

    List<ClothesDtoResponse> getClothes(Long id);

    OrderResponse saveUpdatedHistory(Long id, OrderRequest request);

}
