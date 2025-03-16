package com.code.city.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.code.city.dto.SuggestionDto;
import com.code.city.entity.City;
import com.code.city.service.CityService;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class CityController {

    private final CityService cityService;

    public CityController (CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/suggestions")
    public Map<String, List<SuggestionDto>> getCities (
        @RequestParam(value = "q", required = false) String query,
        @RequestParam(value = "latitude", required = false) Double latitude,
        @RequestParam(value = "longitude", required = false) Double longitude) {
            List<City> cities = cityService.getSuggestions(query, latitude, longitude);

            List<SuggestionDto> suggestionDtos = cities.stream()
                .map(city -> {
                    SuggestionDto dto = new SuggestionDto();
                    dto.setName(city.getName());
                    dto.setLatitude(String.valueOf(city.getLatitude()));
                    dto.setLongitude(String.valueOf(city.getLongitude()));
                    dto.setScore(Math.round(city.getScore() * 100) / 100.0);
                    return dto;
                }).collect(Collectors.toList());
            
            Map<String, List<SuggestionDto>> response = new HashMap<>();
            response.put("suggestions", suggestionDtos);

            return response;
        }
}
