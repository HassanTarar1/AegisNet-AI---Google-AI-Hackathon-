package com.aegisnet.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_signals")
@Data
@NoArgsConstructor
public class EventSignal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source; // "SUPARCO_WEATHER", "SOCIAL_X", "HOTLINE_1122"
    private String type;   // "WEATHER_ANOMALY", "TRAFFIC_JAM", "SOCIAL_PANIC"
    
    @Column(columnDefinition = "TEXT")
    private String rawPayload; // Raw text or JSON
    
    private Double latitude;
    private Double longitude;
    
    private String locationDescription; // e.g. "Murree Expressway"
    
    private Integer severityScore; // 0-100
    
    private LocalDateTime timestamp;
    
    private boolean processed = false;
    
    private Double confidence; // 0.0 - 1.0 (Credibility Engine Score)
}
