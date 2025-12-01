package com.joo.pro.dto.response;


import com.joo.pro.entity.Customer;
import lombok.*;

@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CustomerDtoResponse {

    private String name;

    private String phoneNumber;

    public static CustomerDtoResponse fromEntity(Customer customer) {
        return CustomerDtoResponse.builder()
                .name(customer.getName())
                .phoneNumber(customer.getPhoneNumber())
                .build();
    }

}
