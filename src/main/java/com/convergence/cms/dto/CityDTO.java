package com.convergence.cms.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CityDTO {

    private Long id;

    @NotBlank(message = "City name is required")
    private String name;
}