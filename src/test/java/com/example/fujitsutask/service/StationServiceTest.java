package com.example.fujitsutask.service;

import com.example.fujitsutask.dto.StationDto;
import com.example.fujitsutask.exception.ApplicationException;
import com.example.fujitsutask.mapper.StationMapper;
import com.example.fujitsutask.mapper.StationMapperImpl;
import com.example.fujitsutask.model.Station;
import com.example.fujitsutask.repository.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @Spy
    private StationMapper stationMapper = new StationMapperImpl();

    @InjectMocks
    private StationService stationService;

    @Test
    void testGetStations() {
        // Given
        List<Station> stations = new ArrayList<>();
        stations.add(Station.builder().id(1).name("Tallinn").build());
        stations.add(Station.builder().id(2).name("Tartu").build());
        stations.add(Station.builder().id(3).name("Pärnu").build());
        given(stationRepository.findAll()).willReturn(stations);

        // When
        List<StationDto> stationDtos = stationService.getStations();

        // Then
        then(stationRepository).should().findAll();
        then(stationMapper).should().toDtoList(stations);
        assertEquals(3, stationDtos.size());
        assertEquals("Tallinn", stationDtos.get(0).getName());
        assertEquals(1, stationDtos.get(0).getId());
        assertEquals("Tartu", stationDtos.get(1).getName());
        assertEquals(2, stationDtos.get(1).getId());
        assertEquals("Pärnu", stationDtos.get(2).getName());
        assertEquals(3, stationDtos.get(2).getId());
    }

    @Test
    void testCalculateFeeSimpleSuccess() {
        // Given
        String city = "Tallinn";
        String vehicle = "Scooter";
        Station station = Station.builder().id(1).name("Tallinn-Harku").airTemperature(10.0).windSpeed(0D).phenomenon("").build();
        given(stationRepository.findTopByNameContainingIgnoreCaseOrderByIdDesc(city)).willReturn(station);

        // When
        float fee = stationService.calculateFee(city, vehicle);

        // Then
        assertEquals(3.5f, fee); // 3.5 + 0 + 0 + 0 = 3.5
    }

    @Test
    void testCalculateFeeNormalSuccess() {
        // Given
        String city = "Tallinn";
        String vehicle = "Bike";
        Station station = Station.builder().id(1).name("Tallinn-Harku").airTemperature(-100.0).windSpeed(0D).phenomenon("").build();
        given(stationRepository.findTopByNameContainingIgnoreCaseOrderByIdDesc(city)).willReturn(station);

        // When
        float fee = stationService.calculateFee(city, vehicle);

        // Then
        assertEquals(4.0f, fee); // 3.0 + 1.0 + 0 + 0 = 3.5
    }

    @Test
    void testCalculateFeeComplexSuccess() {
        // Given
        String city = "Tallinn";
        String vehicle = "Scooter";
        Station station = Station.builder().id(1).name("Tallinn-Harku").airTemperature(-10.0).windSpeed(10.0).phenomenon("Light rain").build();
        given(stationRepository.findTopByNameContainingIgnoreCaseOrderByIdDesc(city)).willReturn(station);

        // When
        float fee = stationService.calculateFee(city, vehicle);

        // Then
        assertEquals(5.0f, fee); // 3.5 + 0.5 + 0.5 + 0.5 = 5.0
    }

    @Test
    void testCalculateFeeHighWindSpeedThrowsException() {
        // Given
        String city = "Tartu";
        String vehicle = "Bike";
        Station station = Station.builder().id(1).name("Tartu-Tõravere").airTemperature(-5.0).windSpeed(25.0).phenomenon("Drifting snow").build();
        given(stationRepository.findTopByNameContainingIgnoreCaseOrderByIdDesc(city)).willReturn(station);

        // When
        ApplicationException ex = assertThrows(ApplicationException.class, () -> stationService.calculateFee(city, vehicle));

        // Then
        assertEquals("Usage of selected vehicle type is forbidden", ex.getMessage());
    }

    @Test
    void testCalculateFeeHighForbiddenPhenomenonThrowsException() {
        // Given
        String city = "Pärnu";
        String vehicle = "Scooter";
        Station station = Station.builder().id(1).name("Pärnu").airTemperature(0D).windSpeed(0D).phenomenon("Thunderstorm").build();
        given(stationRepository.findTopByNameContainingIgnoreCaseOrderByIdDesc(city)).willReturn(station);

        // When
        ApplicationException ex = assertThrows(ApplicationException.class, () -> stationService.calculateFee(city, vehicle));

        // Then
        assertEquals("Usage of selected vehicle type is forbidden", ex.getMessage());
    }
}