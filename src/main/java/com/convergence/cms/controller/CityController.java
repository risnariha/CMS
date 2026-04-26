package com.convergence.cms.controller;

import com.convergence.cms.entity.City;
import com.convergence.cms.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityRepository cityRepository;

    @PostMapping
    public City create(@RequestBody City city) {
        return cityRepository.save(city);
    }

    @GetMapping
    public List<City> getAll() {
        return cityRepository.findAll();
    }

    @GetMapping("/{id}")
    public City get(@PathVariable Long id) {
        return cityRepository.findById(id).orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        cityRepository.deleteById(id);
    }
}