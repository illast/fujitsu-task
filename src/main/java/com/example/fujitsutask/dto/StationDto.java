package com.example.fujitsutask.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StationDto {

    private Integer id;
    private String name;
    private Integer wmocode;
    private Double airtemperature;
    private Double windspeed;
    private String phenomenon;
    private Long timestamp;
}
