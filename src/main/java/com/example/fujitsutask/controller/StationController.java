package com.example.fujitsutask.controller;

import com.example.fujitsutask.dto.StationDto;
import com.example.fujitsutask.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class StationController {

    private final StationService stationService;

    @GetMapping("/api/stations")
    public List<StationDto> getStations() {
        return stationService.getStations();
    }

    @PostMapping("/api/stations")
    public void addStation(@RequestBody StationDto station) {
        stationService.addStation(station);
    }
}
