package com.metro.ticket.repository;

import com.metro.ticket.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StationRepository
        extends JpaRepository<Station, Long> {

    Optional<Station> findByNameIgnoreCase(String name);
}