package com.convergence.cms.service;

import com.convergence.cms.dto.CityDTO;
import com.convergence.cms.entity.City;
import com.convergence.cms.entity.Country;
import com.convergence.cms.repository.CityRepository;
import com.convergence.cms.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository repo;
    private final CountryRepository countryRepository;

    @Override
    public City create(CityDTO cityDto) {
        Country country = countryRepository.findByName(cityDto.getCountryName())
                .orElseGet(() -> {
                    Country newCountry = new Country();
                    newCountry.setName(cityDto.getCountryName());
                    return countryRepository.save(newCountry);
                });

        return repo.findByNameAndCountry_Name(cityDto.getName(), country.getName())
                .orElseGet(() -> {
                    City city = new City();
                    city.setName(cityDto.getName());
                    city.setCountry(country);
                    return repo.save(city);
                });
    }

    @Override
    public List<City> getAll() {
        return repo.findAll();
    }

    @Override
    public City getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("City not found"));
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
