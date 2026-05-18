package com.aegisnet.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "drone_telemetry")
@Data
@NoArgsConstructor
public class DroneTelemetry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String droneId; // e.g. "DRONE_MURREE_01"
    
    private Double currentLat;
    private Double currentLng;
    private Double altitudeMeters;
    
    private String status; // "IN_TRANSIT", "SCANNING", "RETURNING", "OFFLINE"
    
    private Integer batteryLevel; // 0-100
    
    // Extracted from thermal imaging
    private Integer strandedHumansDetected;
    private Integer strandedVehiclesDetected;
    private Double estimatedFloodDepthMeters;
    
    private LocalDateTime timestamp;
}
