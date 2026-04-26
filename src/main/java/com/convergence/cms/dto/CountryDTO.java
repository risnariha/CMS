package com.convergence.cms.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CountryDTO {

    private Long id;

    @NotBlank(message = "Country name is required")
    private String name;
}