package com.example.fujitsutask.service;

import com.example.fujitsutask.dto.StationDto;
import com.example.fujitsutask.mapper.StationMapper;
import com.example.fujitsutask.model.Station;
import com.example.fujitsutask.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StationService {

    private final StationRepository stationRepository;
    private final StationMapper stationMapper;

    private static final HashMap<String, Float> RBF = new HashMap<>();
    private static final HashSet<String> WPEF_RAIN = new HashSet<>();
    private static final HashSet<String> WPEF_SNOW = new HashSet<>();
    private static final HashSet<String> WPEF_FORBIDDEN = new HashSet<>();

    private static final float ATEF_LOW_TEMP_FEE = 1f;
    private static final float ATEF_MID_TEMP_FEE = 0.5f;
    private static final float WSEF_FEE = 1f;
    private static final float WPEF_RAIN_FEE = 1f;
    private static final float WPEF_SNOW_FEE = 0.5f;


    static {
        RBF.put("Tallinn Car", 4f);
        RBF.put("Tallinn Scooter", 3.5f);
        RBF.put("Tallinn Bike", 3f);
        RBF.put("Tartu Car", 3.5f);
        RBF.put("Tartu Scooter", 3f);
        RBF.put("Tartu Bike", 2.5f);
        RBF.put("Pärnu Car", 3f);
        RBF.put("Pärnu Scooter", 2.5f);
        RBF.put("Pärnu Bike", 2f);

        WPEF_SNOW.add("Light snow shower");
        WPEF_SNOW.add("Moderate snow shower");
        WPEF_SNOW.add("Heavy snow shower");
        WPEF_SNOW.add("Light sleet");
        WPEF_SNOW.add("Moderate sleet");
        WPEF_SNOW.add("Light snowfall");
        WPEF_SNOW.add("Moderate snowfall");
        WPEF_SNOW.add("Heavy snowfall");
        WPEF_SNOW.add("Blowing snow");
        WPEF_SNOW.add("Drifting snow");

        WPEF_RAIN.add("Light shower");
        WPEF_RAIN.add("Moderate shower");
        WPEF_RAIN.add("Heavy shower");
        WPEF_RAIN.add("Light rain");
        WPEF_RAIN.add("Moderate rain");
        WPEF_RAIN.add("Heavy rain");

        WPEF_FORBIDDEN.add("Glaze");
        WPEF_FORBIDDEN.add("Hail");
        WPEF_FORBIDDEN.add("Thunder");
        WPEF_FORBIDDEN.add("Thunderstorm");

    }

    /**
     * Return a list of all stations.
     */
    public List<StationDto> getStations() {
        List<Station> stations = stationRepository.findAll();
        return stationMapper.toDtoList(stations);
    }

    /**
     * Save station to database.
     */
    public void addStation(StationDto stationDto) {
        Station station = stationMapper.dtoToEntity(stationDto);
        stationRepository.save(station);
    }

    /**
     * Delivery fee = RBF + ATEF + WSEF + WPEF
     * RBF - regional base fee;
     * ATEF - air temperature extra fee;
     * WSEF - wind speed extra fee;
     * WPEF - weather phenomenon extra fee.
     * @param city city name.
     * @param vehicle vehicle type.
     * @return calculated fee.
     */
    public float calculateFee(String city, String vehicle) {
        float fee = calculateRBF(city, vehicle);

        if (vehicle.equals("Scooter") || vehicle.equals("Bike")) {
            Station station = stationRepository.findTopByNameContainingIgnoreCaseOrderByIdDesc(city);

            System.out.println("ID: " + station.getId());
            System.out.println("Name: " + station.getName());
            System.out.println("Air temperature: " + station.getAirtemperature());
            System.out.println("Wind speed: " + station.getWindspeed());
            System.out.println("Weather phenomenon: " + station.getPhenomenon());

            fee += calculateATEF(station.getAirtemperature())
                    + calculateWPEF(station.getPhenomenon());

            if (vehicle.equals("Bike")) {
                fee += calculateWSEF(station.getWindspeed());
            }
        }

        return fee;
    }

    /**
     * Return RBF based on city and vehicle type.
     */
    private float calculateRBF(String city, String vehicle) {
        if (RBF.containsKey(city + " " + vehicle)) return RBF.get(city + " " + vehicle);
        return 0;
    }

    /**
     * Return ATEF based on air temperature.
     */
    private float calculateATEF(Double airtemperature) {
        if (airtemperature < -10) return ATEF_LOW_TEMP_FEE;
        if (airtemperature >= -10 && airtemperature < 0) return ATEF_MID_TEMP_FEE;
        return 0;
    }

    /**
     * Return WSEF based on wind speed.
     */
    private float calculateWSEF(Double windspeed) {
        if (windspeed >= 10 && windspeed < 20) return WSEF_FEE;
        if (windspeed >= 20) return -1; // TODO "Usage of selected vehicle type is forbidden" error message
        return 0;
    }

    /**
     * Return WPEF based on phenomenon.
     */
    private float calculateWPEF(String phenomenon) {
        if (WPEF_SNOW.contains(phenomenon)) return WPEF_SNOW_FEE;
        if (WPEF_RAIN.contains(phenomenon)) return WPEF_RAIN_FEE;
        if (WPEF_FORBIDDEN.contains(phenomenon)) return -1; // TODO "Usage of selected vehicle type is forbidden" error message
        return 0;
    }
}
