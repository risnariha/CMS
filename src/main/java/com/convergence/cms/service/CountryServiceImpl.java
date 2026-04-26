package com.convergence.cms.service;

import com.convergence.cms.entity.Country;
import com.convergence.cms.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository repo;

    @Override
    public Country create(Country country) {
        return repo.save(country);
    }

    @Override
    public List<Country> getAll() {
        return repo.findAll();
    }

    @Override
    public Country getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found"));
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}