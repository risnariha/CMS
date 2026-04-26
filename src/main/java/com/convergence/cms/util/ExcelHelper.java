package com.convergence.cms.util;

import com.convergence.cms.entity.Customer;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

public class ExcelHelper {

    public static List<Customer> excelToCustomers(MultipartFile file) {
        List<Customer> list = new ArrayList<>();

        try {
            InputStream is = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                Customer c = new Customer();
                c.setName(row.getCell(0).getStringCellValue());
                c.setNic(row.getCell(1).getStringCellValue());

                list.add(c);

                // MEMORY SAFE (batch flush idea)
                if (list.size() == 1000) {
                    // send to DB batch (optimize later)
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}