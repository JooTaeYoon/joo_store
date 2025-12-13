package com.joo.pro.repository;

import com.joo.pro.entity.Clothes;
import com.joo.pro.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByName(String name);

    List<Customer> findAllByName(String name);

}
