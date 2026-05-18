package com.aegisnet.core.repository;

import com.aegisnet.core.model.DroneTelemetry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DroneTelemetryRepository extends JpaRepository<DroneTelemetry, Long> {
    List<DroneTelemetry> findByDroneIdOrderByTimestampDesc(String droneId);
}
