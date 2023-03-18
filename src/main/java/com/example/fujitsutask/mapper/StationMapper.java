package com.example.fujitsutask.mapper;

import com.example.fujitsutask.dto.StationDto;
import com.example.fujitsutask.model.Station;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StationMapper {

    StationDto entityToDto(Station station);
    Station dtoToEntity(StationDto stationDto);

    List<StationDto> toDtoList(List<Station> stations);
}
