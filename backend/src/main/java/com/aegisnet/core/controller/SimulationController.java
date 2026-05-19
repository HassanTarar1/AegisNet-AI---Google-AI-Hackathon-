package com.aegisnet.core.controller;

import com.aegisnet.core.model.EventSignal;
import com.aegisnet.core.service.GdeltService;
import com.aegisnet.core.service.MockIngestionService;
import com.aegisnet.core.service.OpenMeteoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/simulation")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SimulationController {

    private final MockIngestionService mockIngestionService;
    private final OpenMeteoService openMeteoService;
    private final GdeltService gdeltService;

    // --- MOCK ENDPOINTS (original) ---

    @PostMapping("/weather")
    public ResponseEntity<String> triggerWeatherAnomaly() {
        mockIngestionService.simulateMurreeWeatherAnomaly();
        return ResponseEntity.ok("Weather Anomaly Simulated");
    }

    @PostMapping("/social")
    public ResponseEntity<String> triggerSocialPanic() {
        mockIngestionService.simulateSocialPanic();
        return ResponseEntity.ok("Social Panic Simulated");
    }

    // --- LIVE DATA ENDPOINTS (real APIs) ---

    @PostMapping("/live-weather")
    public ResponseEntity<Map<String, Object>> triggerLiveWeather() {
        try {
            EventSignal signal = openMeteoService.fetchAndIngestLiveWeather();
            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Live weather data ingested from Open-Meteo",
                "signal", signal
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "ERROR",
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/live-gdelt")
    public ResponseEntity<Map<String, Object>> triggerLiveGdelt() {
        try {
            List<EventSignal> signals = gdeltService.fetchAndIngestCrisisNews();
            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", signals.size() + " crisis news signals ingested from GDELT",
                "signals", signals
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "ERROR",
                "message", e.getMessage()
            ));
        }
    }
}
