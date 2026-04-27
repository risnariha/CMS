package com.convergence.cms.util;

import com.convergence.cms.dto.AddressDTO;
import com.convergence.cms.dto.CustomerDTO;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelHelper {

    private static final List<String> NAME_HEADERS = Arrays.asList("name", "customername");
    private static final List<String> DOB_HEADERS = Arrays.asList("dob", "dateofbirth", "birthdate");
    private static final List<String> NIC_HEADERS = Arrays.asList("nic", "idnumber", "nationalid");
    private static final List<String> MOBILE_HEADERS = Arrays.asList("mobilenumbers", "mobilenumber", "mobile", "phone", "phonenumber");
    private static final List<String> FAMILY_HEADERS = Arrays.asList("familymemberids", "familymembers", "family");
    private static final List<String> LINE1_HEADERS = Arrays.asList("line1", "addressline1", "address1");
    private static final List<String> LINE2_HEADERS = Arrays.asList("line2", "addressline2", "address2");
    private static final List<String> CITY_HEADERS = Arrays.asList("city", "cityname");
    private static final List<String> COUNTRY_HEADERS = Arrays.asList("country", "countryname");

    private static final List<String> DATE_PATTERNS = Arrays.asList(
            "yyyy-MM-dd",
            "dd/MM/yyyy",
            "MM/dd/yyyy",
            "dd-MM-yyyy",
            "MM-dd-yyyy"
    );

    public static List<CustomerDTO> excelToCustomerDtos(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (!rows.hasNext()) {
                throw new RuntimeException("Workbook is empty");
            }

            Map<String, Integer> headerMap = readHeaderIndex(rows.next());
            validateHeaders(headerMap);

            List<CustomerDTO> customers = new ArrayList<>();
            DataFormatter formatter = new DataFormatter();

            while (rows.hasNext()) {
                Row row = rows.next();
                if (isRowEmpty(row, formatter)) {
                    continue;
                }

                CustomerDTO customer = new CustomerDTO();
                customer.setName(readRequiredString(row, headerMap, formatter, NAME_HEADERS, "name"));
                customer.setNic(readRequiredString(row, headerMap, formatter, NIC_HEADERS, "nic"));
                customer.setDob(readDate(row, headerMap, DOB_HEADERS, "dob"));
                customer.setMobileNumbers(splitCsv(readOptionalString(row, headerMap, formatter, MOBILE_HEADERS)));
                customer.setFamilyMemberIds(parseLongList(readOptionalString(row, headerMap, formatter, FAMILY_HEADERS)));

                AddressDTO address = readAddress(row, headerMap, formatter);
                if (address != null) {
                    customer.setAddresses(Collections.singletonList(address));
                } else {
                    customer.setAddresses(Collections.emptyList());
                }

                customers.add(customer);
            }

            return customers;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("File processing error: " + ex.getMessage(), ex);
        }
    }

    private static Map<String, Integer> readHeaderIndex(Row headerRow) {
        Map<String, Integer> headerMap = new HashMap<>();
        DataFormatter formatter = new DataFormatter();

        for (Cell cell : headerRow) {
            String header = formatter.formatCellValue(cell).trim();
            if (!header.isEmpty()) {
                headerMap.put(normalizeHeader(header), cell.getColumnIndex());
            }
        }

        return headerMap;
    }

    private static void validateHeaders(Map<String, Integer> headerMap) {
        assertHeaderExists(headerMap, NAME_HEADERS, "name");
        assertHeaderExists(headerMap, DOB_HEADERS, "dob");
        assertHeaderExists(headerMap, NIC_HEADERS, "nic");
    }

    private static AddressDTO readAddress(Row row, Map<String, Integer> headerMap, DataFormatter formatter) {
        String line1 = readOptionalString(row, headerMap, formatter, LINE1_HEADERS);
        String line2 = readOptionalString(row, headerMap, formatter, LINE2_HEADERS);
        String city = readOptionalString(row, headerMap, formatter, CITY_HEADERS);
        String country = readOptionalString(row, headerMap, formatter, COUNTRY_HEADERS);

        boolean hasAddressData = !line1.isEmpty() || !line2.isEmpty() || !city.isEmpty() || !country.isEmpty();
        if (!hasAddressData) {
            return null;
        }

        if (line1.isEmpty() || city.isEmpty() || country.isEmpty()) {
            throw new RuntimeException("Address rows must include line1, city and country");
        }

        AddressDTO address = new AddressDTO();
        address.setLine1(line1);
        address.setLine2(line2);
        address.setCity(city);
        address.setCountry(country);
        return address;
    }

    private static String readRequiredString(Row row, Map<String, Integer> headerMap, DataFormatter formatter,
                                             List<String> columns, String displayName) {
        String value = readOptionalString(row, headerMap, formatter, columns);
        if (value.isEmpty()) {
            throw new RuntimeException("Column '" + displayName + "' is required");
        }
        return value;
    }

    private static String readOptionalString(Row row, Map<String, Integer> headerMap, DataFormatter formatter, List<String> columns) {
        Integer index = findColumnIndex(headerMap, columns);
        if (index == null) {
            return "";
        }

        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return "";
        }

        return formatter.formatCellValue(cell).trim();
    }

    private static Date readDate(Row row, Map<String, Integer> headerMap, List<String> columns, String displayName) {
        Integer index = findColumnIndex(headerMap, columns);
        if (index == null) {
            throw new RuntimeException("Missing required column: " + displayName);
        }

        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            throw new RuntimeException("Column '" + displayName + "' is required");
        }

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        }

        String value = new DataFormatter().formatCellValue(cell).trim();
        if (value.isEmpty()) {
            throw new RuntimeException("Column '" + displayName + "' is required");
        }

        for (String pattern : DATE_PATTERNS) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(pattern);
                format.setLenient(false);
                return format.parse(value);
            } catch (ParseException ignored) {
            }
        }

        throw new RuntimeException("Unsupported date format for dob: " + value);
    }

    private static List<String> splitCsv(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String[] parts = value.split(",");
        List<String> items = new ArrayList<>();

        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                items.add(trimmed);
            }
        }

        return items;
    }

    private static List<Long> parseLongList(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> ids = new ArrayList<>();
        for (String part : value.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                try {
                    ids.add(Long.parseLong(trimmed));
                } catch (NumberFormatException ex) {
                    throw new RuntimeException("Invalid familyMemberIds value: " + trimmed);
                }
            }
        }
        return ids;
    }

    private static boolean isRowEmpty(Row row, DataFormatter formatter) {
        if (row == null) {
            return true;
        }

        for (Cell cell : row) {
            if (!formatter.formatCellValue(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static String normalizeHeader(String header) {
        return header.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT);
    }

    private static void assertHeaderExists(Map<String, Integer> headerMap, List<String> aliases, String displayName) {
        if (findColumnIndex(headerMap, aliases) == null) {
            throw new RuntimeException("Missing required column: " + displayName);
        }
    }

    private static Integer findColumnIndex(Map<String, Integer> headerMap, List<String> aliases) {
        for (String alias : aliases) {
            Integer index = headerMap.get(normalizeHeader(alias));
            if (index != null) {
                return index;
            }
        }
        return null;
    }
}
