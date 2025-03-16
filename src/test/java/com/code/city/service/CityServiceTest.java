package com.code.city.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.code.city.entity.City;

public class CityServiceTest {
    
    private CityService cityService;

    @BeforeEach
    void setUp() {
        cityService = new CityService();

        List<City> mockCities = new ArrayList<>();
        // data simulation
        City city1 = new City();
        city1.setName("Toronto");
        city1.setCountry("Canada");
        city1.setLatitude(43.7);
        city1.setLongitude(-79.42);
        city1.setPopulation(3000000); 

        City city2 = new City();
        city2.setName("New York");
        city2.setCountry("USA");
        city2.setLatitude(40.71);
        city2.setLongitude(-74.01);
        city2.setPopulation(8000000);

        mockCities.add(city1);
        mockCities.add(city2);

        cityService.setCities(mockCities);
    }


    @Test
    void testGetSuggestionsByName() {
        List<City> results = cityService.getSuggestions("Toronto", null, null);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("Toronto", results.get(0).getName());
    }
}
