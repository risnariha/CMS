package com.convergence.cms.service;

import com.convergence.cms.dto.CityDTO;
import com.convergence.cms.entity.City;

import java.util.List;

public interface CityService {

    City create(CityDTO city);

    List<City> getAll();

    City getById(Long id);

    void delete(Long id);
}
