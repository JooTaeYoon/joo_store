package com.joo.pro.repository;

import com.joo.pro.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Customer, Long> {
}
