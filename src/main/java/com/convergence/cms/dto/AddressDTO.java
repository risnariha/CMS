package com.convergence.cms.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddressDTO {

    @NotBlank(message = "Address Line 1 is required")
    private String line1;

    private String line2;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Country is required")
    private String country;
}