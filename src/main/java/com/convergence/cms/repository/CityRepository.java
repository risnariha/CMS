package com.convergence.cms.repository;

import com.convergence.cms.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByName(String name);

    Optional<City> findByNameAndCountry_Name(String name, String countryName);
}
