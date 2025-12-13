package com.joo.pro.repository;

import com.joo.pro.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByName(String name);

    List<Customer> findAllByName(String name);

}
