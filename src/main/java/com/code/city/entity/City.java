package com.code.city.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class City {

    private String name;
    private String country;
    private double latitude;
    private double longitude;
    private long population;
    private double score;

    public String getFullName() {
        return String.format("%s, %s", name, country);
    }

}
