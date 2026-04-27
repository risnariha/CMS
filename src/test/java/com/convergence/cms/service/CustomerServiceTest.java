package com.convergence.cms.service;

import com.convergence.cms.dto.CustomerDTO;
import com.convergence.cms.entity.Customer;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.convergence.cms.repository.CityRepository;
import com.convergence.cms.repository.CountryRepository;
import com.convergence.cms.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
        MultipartFile file = new MockMultipartFile(
                "file",
                "customers.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                createWorkbookBytes()
        );

        Mockito.when(customerRepo.findByNic("123V")).thenReturn(Optional.empty());
        Mockito.when(customerRepo.findByNic("456V")).thenReturn(Optional.empty());
        Mockito.when(customerRepo.save(Mockito.any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.bulkUpload(file);

        Mockito.verify(customerRepo, Mockito.times(2))
                .save(Mockito.any(Customer.class));
    }

    private byte[] createWorkbookBytes() {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Customers");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("name");
            header.createCell(1).setCellValue("dob");
            header.createCell(2).setCellValue("nic");

            Row first = sheet.createRow(1);
            first.createCell(0).setCellValue("John Doe");
            first.createCell(1).setCellValue("1999-01-01");
            first.createCell(2).setCellValue("123V");

            Row second = sheet.createRow(2);
            second.createCell(0).setCellValue("Jane Smith");
            second.createCell(1).setCellValue("2000-02-02");
            second.createCell(2).setCellValue("456V");

            workbook.write(output);
            return output.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
