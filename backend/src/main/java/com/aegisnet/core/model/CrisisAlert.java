package com.aegisnet.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "crisis_alerts")
@Data
@NoArgsConstructor
public class CrisisAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // "MASS_ENTRAPMENT_RISK", "FLOOD_ESCALATION"
    
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private Double epicenterLat;
    private Double epicenterLng;
    
    private Double impactRadiusKm;
    
    private Integer escalationProbability; // 0-100%
    private Integer casualtyRiskScore;     // 0-100
    
    private LocalDateTime predictedEscalationTime;
    
    @ElementCollection
    private List<String> recommendedPreventiveActions; // ["Close Murree Expressway", "Dispatch Drones"]
    
    private String status; // "PREDICTED", "ACTIVE", "MITIGATED", "RESOLVED"
    
    private LocalDateTime createdAt;
}
