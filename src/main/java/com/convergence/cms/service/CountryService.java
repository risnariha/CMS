package com.convergence.cms.service;

import com.convergence.cms.entity.Country;

import java.util.List;

public interface CountryService {

    Country create(Country country);

    List<Country> getAll();

    Country getById(Long id);

    void delete(Long id);
}