package com.convergence.cms.service;

import com.convergence.cms.dto.CustomerDTO;
import com.convergence.cms.entity.Customer;
import com.convergence.cms.repository.CityRepository;
import com.convergence.cms.repository.CountryRepository;
import com.convergence.cms.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CustomerServiceTest {

    private final CustomerRepository customerRepo = Mockito.mock(CustomerRepository.class);
    private final CityRepository cityRepo = Mockito.mock(CityRepository.class);
    private final CountryRepository countryRepo = Mockito.mock(CountryRepository.class);

    private final CustomerService service =
            new CustomerServiceImpl(customerRepo, cityRepo, countryRepo);

    @Test
    void testCreateCustomer() {
        CustomerDTO dto = new CustomerDTO();
        dto.setName("John Doe");
        dto.setNic("123V");

        Customer saved = new Customer();
        saved.setId(1L);
        saved.setName("John Doe");
        saved.setNic("123V");

        Mockito.when(customerRepo.findByNic("123V")).thenReturn(Optional.empty());
        Mockito.when(customerRepo.save(Mockito.any(Customer.class))).thenReturn(saved);

        Customer result = service.create(dto);

        assertNotNull(result);
        assertEquals("123V", result.getNic());
    }

    @Test
    void testGetByNic() {
        Customer customer = new Customer();
        customer.setNic("123V");

        Mockito.when(customerRepo.findByNic("123V")).thenReturn(Optional.of(customer));

        Customer result = service.getByNic("123V");

        assertNotNull(result);
        assertEquals("123V", result.getNic());
    }

    @Test
    void testBulkUpload() {
        String content =
                "name,nic\n" +
                        "John Doe,123V\n" +
                        "Jane Smith,456V\n";

        MultipartFile file = new MockMultipartFile(
                "file",
                "customers.csv",
                "text/csv",
                content.getBytes()
        );

        service.bulkUpload(file);

        Mockito.verify(customerRepo, Mockito.times(1))
                .saveAll(Mockito.anyList());
    }
}
