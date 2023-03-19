package com.example.fujitsutask.service;

import com.example.fujitsutask.dto.StationDto;
import com.example.fujitsutask.mapper.StationMapper;
import com.example.fujitsutask.model.Station;
import com.example.fujitsutask.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StationService {

    private final StationRepository stationRepository;
    private final StationMapper stationMapper;

    private static final HashMap<String, Float> rbf = new HashMap<>();

    static {
        rbf.put("Tallinn Car", 4f);
        rbf.put("Tallinn Scooter", 3.5f);
        rbf.put("Tallinn Bike", 3f);
        rbf.put("Tartu Car", 3.5f);
        rbf.put("Tartu Scooter", 3f);
        rbf.put("Tartu Bike", 2.5f);
        rbf.put("Pärnu Car", 3f);
        rbf.put("Pärnu Scooter", 2.5f);
        rbf.put("Pärnu Bike", 2f);
    }

    public List<StationDto> getStations() {
        List<Station> stations = stationRepository.findAll();
        return stationMapper.toDtoList(stations);
    }

    public void addStation(StationDto stationDto) {
        Station station = stationMapper.dtoToEntity(stationDto);
        stationRepository.save(station);
    }

    public float calculateFee(String city, String vehicle) {
//        float fee = 0;
//        fee += calculateRBF(city, vehicle);
        return calculateRBF(city, vehicle);
    }

    private float calculateRBF(String city, String vehicle) {
        if (rbf.containsKey(city + " " + vehicle)) {
            return rbf.get(city + " " + vehicle);
        }
        return 0;
    }
}
