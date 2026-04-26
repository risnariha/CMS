package com.convergence.cms.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

@Data
public class CustomerDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Date of birth is required")
    private Date dob;

    @NotBlank(message = "NIC is required")
    private String nic;

    private List<String> mobileNumbers;

    private List<AddressDTO> addresses;

    private List<Long> familyMemberIds;
}