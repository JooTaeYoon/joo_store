package com.joo.pro.dto.response;

import com.joo.pro.entity.Clothes;
import com.joo.pro.entity.Customer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Schema(title = "고객 검색 응답 DTO")
@Builder
public record SearchCustomerResponse(
        @Schema(title = "고객 이름", example = "홍길동")
        Customer customer,

        @Schema(title = "옷 정보", example = "TOP(상의),BOTTOM(하의)")
        Clothes.CATEGORY category,

        @Schema(title = "옷 가격", example = "15000")
        String price,

        @Schema(title = "서비스 타입", example = "WASH(세탁),DRY_CLEANING(드라이클리닝)")
        Clothes.SERVICE_TYPE serviceType,

        @Schema(title = "생성 일자", example = "2024-01-01T10:00:00")
        LocalDateTime createdAt,

        @Schema(title = "옷 찾아감", example = "YES/NO")
        Clothes.PICKUP pickUp
) {

    public static SearchCustomerResponse fromEntity(Clothes clothes) {
        return SearchCustomerResponse.builder()
                .customer(clothes.getOrder().getCustomer())
                .category(clothes.getCategory())
                .price(clothes.getPrice())
                .serviceType(clothes.getServiceType())
                .createdAt(clothes.getCreatedAt())
                .pickUp(clothes.getPickup())
                .build();
    }

    public static List<SearchCustomerResponse> fromEntities(List<Clothes> clothesList) {
        return clothesList.stream()
                .map(SearchCustomerResponse::fromEntity)
                .toList();
    }
}
