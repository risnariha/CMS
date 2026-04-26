package com.convergence.cms.util;

import com.convergence.cms.entity.Customer;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ExcelHelper {

    public static List<Customer> excelToCustomers(MultipartFile file) {

        List<Customer> customers = new ArrayList<>();

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {

                if (first) {
                    first = false;
                    continue;
                }

                String[] data = line.split(",");

                if (data.length >= 2) {
                    Customer c = new Customer();
                    c.setName(data[0].trim());
                    c.setNic(data[1].trim());
                    customers.add(c);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("File processing error: " + e.getMessage(), e);
        }

        return customers;
    }
}
