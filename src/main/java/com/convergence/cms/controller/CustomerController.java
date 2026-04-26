package com.convergence.cms.controller;

import com.convergence.cms.dto.CustomerDTO;
import com.convergence.cms.entity.Customer;
import com.convergence.cms.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    public Customer create(@Valid @RequestBody CustomerDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id,
                           @Valid @RequestBody CustomerDTO dto) {
        return service.update(id, dto);
    }

    @GetMapping("/{id}")
    public Customer get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/nic/{nic}")
    public Customer getByNic(@PathVariable String nic) {
        return service.getByNic(nic);
    }

    @GetMapping
    public List<Customer> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // ✅ BULK UPLOAD API
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        service.bulkUpload(file);
        return "Upload successful";
    }
}