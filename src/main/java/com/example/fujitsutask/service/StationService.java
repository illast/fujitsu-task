package com.example.fujitsutask.service;

import com.example.fujitsutask.dto.StationDto;
import com.example.fujitsutask.mapper.StationMapper;
import com.example.fujitsutask.model.Station;
import com.example.fujitsutask.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StationService {

    private final StationRepository stationRepository;
    private final StationMapper stationMapper;

    public List<StationDto> getStations() {
        List<Station> stations = stationRepository.findAll();
        return stationMapper.toDtoList(stations);
    }

    public void addStation(StationDto stationDto) {
        Station station = stationMapper.dtoToEntity(stationDto);
        stationRepository.save(station);
    }
}
