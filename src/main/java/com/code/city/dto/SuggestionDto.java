package com.code.city.dto;

import lombok.Data;

@Data
public class SuggestionDto {
    private String name;
    private String latitude;
    private String longitude;
    private double score;
}
