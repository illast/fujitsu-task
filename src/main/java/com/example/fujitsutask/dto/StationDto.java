package com.example.fujitsutask.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StationDto {

    private Integer id;
    private String name;
    private Integer wmoCode;
    private Double airTemperature;
    private Double windSpeed;
    private String phenomenon;
    private Long timestamp;
}
