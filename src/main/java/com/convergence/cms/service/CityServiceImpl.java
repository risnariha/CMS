package com.convergence.cms.service;

import com.convergence.cms.entity.City;
import com.convergence.cms.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository repo;

    @Override
    public City create(City city) {
        return repo.save(city);
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
