package com.convergence.cms.service;

import com.convergence.cms.entity.Customer;
import com.convergence.cms.repository.CustomerRepository;
import com.convergence.cms.util.ExcelHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repo;

    @Override
    public Customer create(Customer customer) {
        return repo.save(customer);
    }

    @Override
    public Customer update(Long id, Customer customer) {
        Customer existing = repo.findById(id).orElseThrow();
        existing.setName(customer.getName());
        existing.setDob(customer.getDob());
        return repo.save(existing);
    }

    @Override
    public Customer get(Long id) {
        return repo.findById(id).orElseThrow();
    }

    @Override
    public List<Customer> getAll() {
        return repo.findAll();
    }

    @Override
    public void bulkUpload(MultipartFile file) {
        List<Customer> customers = ExcelHelper.excelToCustomers(file);
        repo.saveAll(customers); // batch insert
    }
}