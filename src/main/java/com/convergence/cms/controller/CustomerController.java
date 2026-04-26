package com.convergence.cms.controller;

import com.convergence.cms.dto.CustomerDTO;
import com.convergence.cms.entity.Customer;
import com.convergence.cms.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    // ✅ Create Customer
    @PostMapping
    public Customer create(@Valid @RequestBody CustomerDTO dto) {
        return service.create(dto);
    }

    // ✅ Update Customer
    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id,
                           @Valid @RequestBody CustomerDTO dto) {
        return service.update(id, dto);
    }

    // ✅ Get Customer by ID
    @GetMapping("/{id}")
    public Customer getById(@PathVariable Long id) {
        return service.get(id);
    }

    // ✅ Get all customers (table view)
    @GetMapping
    public List<Customer> getAll() {
        return service.getAll();
    }

    // ❌ Optional delete (if needed)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        // you can add delete in service if required
    }
}