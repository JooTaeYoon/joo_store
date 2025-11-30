package com.joo.pro.dto.response;


import lombok.*;

@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDtoResponse {

    private String name;
    private String phoneNumber;

}
