package com.joo.pro.dto.response;


import com.joo.pro.entity.Customer;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

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

    public static List<CustomerDtoResponse> fromEntities(List<Customer> customers) {
        return customers.stream()
                .map(CustomerDtoResponse::fromEntity)
                .collect(Collectors.toList());
    }

}
