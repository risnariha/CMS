package com.convergence.cms.service;

import com.convergence.cms.dto.AddressDTO;
import com.convergence.cms.dto.CustomerDTO;
import com.convergence.cms.entity.*;
import com.convergence.cms.repository.*;
import com.convergence.cms.util.ExcelHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepo;
    private final CityRepository cityRepo;
    private final CountryRepository countryRepo;

    @Override
    public Customer create(CustomerDTO dto) {

        if (customerRepo.findByNic(dto.getNic()).isPresent()) {
            throw new RuntimeException("NIC already exists");
        }

        return customerRepo.save(map(dto));
    }

    @Override
    public Customer update(Long id, CustomerDTO dto) {

        Customer existing = customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customerRepo.findByNic(dto.getNic()).ifPresent(c -> {
            if (!c.getId().equals(id)) {
                throw new RuntimeException("NIC already exists");
            }
        });

        Customer updated = map(dto);
        updated.setId(existing.getId());

        return customerRepo.save(updated);
    }

    @Override
    public Customer get(Long id) {
        return customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @Override
    public Customer getByNic(String nic) {
        return customerRepo.findByNic(nic)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @Override
    public List<Customer> getAll() {
        return customerRepo.findAll();
    }

    @Override
    public void delete(Long id) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customerRepo.delete(customer);
    }

    @Override
    public void bulkUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is required");
        }

        List<Customer> customers = ExcelHelper.excelToCustomers(file);
        customerRepo.saveAll(customers);
    }

    // =========================
    // DTO → ENTITY MAPPING
    // =========================
    private Customer map(CustomerDTO dto) {

        Customer c = new Customer();
        c.setName(dto.getName());
        c.setDob(dto.getDob());
        c.setNic(dto.getNic());
        c.setMobileNumbers(dto.getMobileNumbers() == null
                ? new ArrayList<>()
                : new ArrayList<>(dto.getMobileNumbers()));

        List<Address> addresses = new ArrayList<>();

        if (dto.getAddresses() != null) {
            for (AddressDTO aDto : dto.getAddresses()) {

                Address a = new Address();
                a.setLine1(aDto.getLine1());
                a.setLine2(aDto.getLine2());

                City city = cityRepo.findByName(aDto.getCity())
                        .orElseGet(() -> {
                            City newCity = new City();
                            newCity.setName(aDto.getCity());
                            return cityRepo.save(newCity);
                        });

                Country country = countryRepo.findByName(aDto.getCountry())
                        .orElseGet(() -> {
                            Country newCountry = new Country();
                            newCountry.setName(aDto.getCountry());
                            return countryRepo.save(newCountry);
                        });

                a.setCity(city);
                a.setCountry(country);

                addresses.add(a);
            }
        }

        c.setAddresses(addresses);
        c.setFamilyMembers(resolveFamilyMembers(dto));

        return c;
    }

    private List<Customer> resolveFamilyMembers(CustomerDTO dto) {
        List<Customer> familyMembers = new ArrayList<>();

        if (dto.getFamilyMemberIds() == null) {
            return familyMembers;
        }

        for (Long familyMemberId : dto.getFamilyMemberIds()) {
            Customer familyMember = customerRepo.findById(familyMemberId)
                    .orElseThrow(() -> new RuntimeException("Family member not found: " + familyMemberId));
            familyMembers.add(familyMember);
        }

        return familyMembers;
    }
}
