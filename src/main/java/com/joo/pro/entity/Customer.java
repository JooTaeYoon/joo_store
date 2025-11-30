package com.joo.pro.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Table(name = "customer")
@Entity
@Setter
@Getter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    @Size(max = 50)
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "이름에 허용되지 않는 문자가 포함되어 있습니다.")
    private String name;

    @Column
    private String phoneNumber;

}
