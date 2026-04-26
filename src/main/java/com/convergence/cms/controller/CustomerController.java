package com.convergence.cms.service;

import com.convergence.cms.entity.Customer;

import java.util.List;

public interface CustomerService {

    Customer create(Customer customer);

    Customer update(Long id, Customer customer);

    Customer get(Long id);

    List<Customer> getAll();
}