package com.example.fujitsutask.repository;

import com.example.fujitsutask.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Integer> {
    Station findTopByNameOrderByIdDesc(String name);
}
