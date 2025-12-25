package com.joo.pro.dto.response;

import com.joo.pro.entity.Clothes;
import com.joo.pro.entity.Customer;
import com.joo.pro.entity.Orders;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;

    //    1:N (고객 1명 당 여러 벌의 옷)
    private Customer customer;

    //    손님이 맡긴 옷의 총 개수
    private Integer count;

    //    손님이 맡긴 옷들의 정보들
    private List<Clothes> clothes = new ArrayList<>();

   public static OrderResponse fromEntity(Orders order){
       return OrderResponse.builder()
               .id(order.getId())
//               .customer(order.getCustomer())
               .count(order.getCount())
               .clothes(order.getClothes())
               .build();
   }
}
