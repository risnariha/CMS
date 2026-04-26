package com.convergence.cms.controller;

import com.convergence.cms.entity.Country;
import com.convergence.cms.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryRepository countryRepository;

    @PostMapping
    public Country create(@RequestBody Country country) {
        return countryRepository.save(country);
    }

    @GetMapping
    public List<Country> getAll() {
        return countryRepository.findAll();
    }

    @GetMapping("/{id}")
    public Country get(@PathVariable Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found"));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        countryRepository.deleteById(id);
    }
}
