package com.joo.pro.repository;

import com.joo.pro.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    Optional<Orders> findByCustomerId(Long customerId);

    void deleteByCustomerId(Long id);
}
