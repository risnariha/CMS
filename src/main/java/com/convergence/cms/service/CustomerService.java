package com.convergence.cms.service;

import com.convergence.cms.dto.CustomerDTO;
import com.convergence.cms.entity.Customer;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CustomerService {

    Customer create(CustomerDTO dto);

    Customer update(Long id, CustomerDTO dto);

    Customer get(Long id);

    Customer getByNic(String nic);

    List<Customer> getAll();

    void delete(Long id);

    void bulkUpload(MultipartFile file);
}