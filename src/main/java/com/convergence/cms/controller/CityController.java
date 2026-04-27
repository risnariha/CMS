package com.convergence.cms.controller;

import com.convergence.cms.dto.CityDTO;
import com.convergence.cms.entity.City;
import com.convergence.cms.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @PostMapping
    public City create(@Valid @RequestBody CityDTO city) {
        return cityService.create(city);
    }

    @GetMapping
    public List<City> getAll() {
        return cityService.getAll();
    }

    @GetMapping("/{id}")
    public City get(@PathVariable Long id) {
        return cityService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        cityService.delete(id);
    }
}
