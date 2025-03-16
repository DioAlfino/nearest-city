package com.code.city.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.code.city.entity.City;

import jakarta.annotation.PostConstruct;

@Service
public class CityService {


    private List<City> cities = new ArrayList<>();


    @PostConstruct
    public void init() {
        try {
            loadCityFromTsv();
        } catch (Exception e) {
            // throw new RuntimeException("Failed to load cities data", e);
            System.err.println("Gagal memuat data kota: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadCityFromTsv() throws IOException {
        String filePath = "data/cities_canada-usa.tsv";
        ClassPathResource resource = new ClassPathResource(filePath);

         if (!resource.exists()) {
        throw new FileNotFoundException("File TSV tidak ditemukan di classpath: " + filePath);
    }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {

            String line;
            // skip header if it exists;
            boolean firstLane = true;

            while ((line = reader.readLine()) != null) {
                if (firstLane) {
                    firstLane = false;
                    // Skip if it's a header
                    if (line.contains("name") || line.contains("country") ) {
                        continue;
                    }
                }
                String[] data = line.split("\t");
                if (data.length >= 6) {
                    City city = new City();
                    city.setName(data[1]);
                    city.setCountry(data[8]);
                    city.setLatitude(Double.parseDouble(data[4]));
                    city.setLongitude(Double.parseDouble(data[5]));
                    city.setPopulation(Long.parseLong(data[14]));
                    cities.add(city);
                }
            }
        }
    }

    public List<City> getSuggestions(String query, Double latitude, Double longitude) {
        List<City> suggestions = new ArrayList<>();

        if (query == null || query.trim().isEmpty()) {
            return suggestions;
        }

        String lowerQuery = query.toLowerCase();

        for (City city : cities) {
            if (city.getName().toLowerCase().contains(lowerQuery)) {
                City suguestion = new City();
                suguestion.setName(city.getName());
                suguestion.setLatitude(city.getLatitude());
                suguestion.setLongitude(city.getLongitude());

                // calculate score based on how close the name matches
                double score = calculateScore(city, lowerQuery, latitude, longitude);
                suguestion.setScore(score);
                suggestions.add(suguestion);
            }
        }

        // sort by descending score
        suggestions.sort((c1, c2) -> Double.compare(c2.getScore(), c1.getScore()));

        // return top result with limit 10
        return suggestions.size() <= 10 ? suggestions : suggestions.subList(0, 10);
    }

    private double calculateScore(City city, String query, Double latitude, Double longitude) {
        double score = 0.0;
        String cityNameLower = city.getName().toLowerCase();
    
        // find the closer city name
        if (cityNameLower.equals(query)) {
            score = 1.0;
        } else if (cityNameLower.startsWith(query)) {
            score = 0.9;
        } else if (cityNameLower.contains(query)) {
            int index = cityNameLower.indexOf(query);
            score = 0.7 - (index * 0.02); // Biar lebih bervariasi
        } else {
            score = 0.3;
        }
    
        // population factor
        double populationFactor = Math.min(0.4, (Math.log10(city.getPopulation() + 1) / 2.5));
        score = score * 0.7 + populationFactor * 0.3;  // Perbesar pengaruh populasi
    
        // distance factor
        if (latitude != null && longitude != null) {
            double distance = calculateDistance(latitude, longitude, city.getLatitude(), city.getLongitude());
            double distanceFactor = Math.max(0, 1 - distance / 500); // Ubah 3000 -> 500 agar lebih variatif
            score = score * 0.6 + distanceFactor * 0.4;  // Perbesar pengaruh jarak
        }
    
        // make sure score no more than 1
        score = Math.max(0, Math.min(1, score));
    
        // Debugging output
        System.out.println("City: " + city.getName() + 
                           " | Query: " + query + 
                           " | Pop: " + city.getPopulation() + 
                           " | Distance: " + (latitude != null ? calculateDistance(latitude, longitude, city.getLatitude(), city.getLongitude()) : "N/A") + 
                           " | Score: " + score);
    
        return score;
    }
    
    private double calculateDistance (double latitude1, double longitude1, double latitude2, double longitude2) {
        // earth radius in km;
        final int radius = 6371;

        double latitudeDistance = Math.toRadians(latitude2 - latitude1);
        double longitudeDistance = Math.toRadians(longitude2 - longitude1);

        double a = Math.sin(latitudeDistance / 2) * Math.sin(latitudeDistance / 2) +
                   Math.cos(Math.toRadians(latitude1) * Math.cos(Math.toRadians(latitude2))) * 
                   Math.sin(longitudeDistance / 2) * Math.sin(longitudeDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return radius * c;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }
}
