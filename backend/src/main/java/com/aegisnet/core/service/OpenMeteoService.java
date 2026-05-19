package com.aegisnet.core.service;

import com.aegisnet.core.model.EventSignal;
import com.aegisnet.core.repository.EventSignalRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

/**
 * Open-Meteo Integration Service
 * Fetches REAL-TIME weather data from the Open-Meteo API (100% free, no API key required).
 * Target: Murree, Pakistan (33.9070°N, 73.3943°E)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OpenMeteoService {

    private final EventSignalRepository eventSignalRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final double MURREE_LAT = 33.9070;
    private static final double MURREE_LNG = 73.3943;

    private static final String OPEN_METEO_URL =
            "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s" +
            "&current=temperature_2m,precipitation,wind_speed_10m,relative_humidity_2m,apparent_temperature,weather_code" +
            "&timezone=Asia/Karachi";

    /**
     * Fetches live weather from Open-Meteo for the Murree region and persists it as an EventSignal.
     */
    public EventSignal fetchAndIngestLiveWeather() {
        String url = String.format(OPEN_METEO_URL, MURREE_LAT, MURREE_LNG);
        log.info("[Agent_1: Intake] Fetching LIVE weather from Open-Meteo for Murree...");

        try {
            RestTemplate restTemplate = new RestTemplate();
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode current = root.path("current");

            double tempC = current.path("temperature_2m").asDouble();
            double precipMm = current.path("precipitation").asDouble();
            double windKmh = current.path("wind_speed_10m").asDouble();
            int humidity = current.path("relative_humidity_2m").asInt();
            double apparentTemp = current.path("apparent_temperature").asDouble();
            int weatherCode = current.path("weather_code").asInt();
            double elevation = root.path("elevation").asDouble();

            // Calculate severity based on real conditions
            int severity = calculateWeatherSeverity(tempC, precipMm, windKmh);

            // Build the raw payload with real data
            String rawPayload = String.format(
                "{\"source\": \"Open-Meteo (LIVE)\", \"temp_c\": %.1f, \"apparent_temp_c\": %.1f, " +
                "\"precipitation_mm\": %.2f, \"wind_kmh\": %.1f, \"humidity_pct\": %d, " +
                "\"weather_code\": %d, \"elevation_m\": %.0f, \"location\": \"Murree, Pakistan\"}",
                tempC, apparentTemp, precipMm, windKmh, humidity, weatherCode, elevation
            );

            EventSignal signal = new EventSignal();
            signal.setSource("OPEN_METEO_LIVE");
            signal.setType("WEATHER_TELEMETRY");
            signal.setRawPayload(rawPayload);
            signal.setLatitude(MURREE_LAT);
            signal.setLongitude(MURREE_LNG);
            signal.setLocationDescription("Murree, Pakistan (Elevation: " + elevation + "m)");
            signal.setSeverityScore(severity);
            signal.setTimestamp(LocalDateTime.now());
            signal.setProcessed(false);
            signal.setConfidence(1.0); // Machine/API source = trusted

            eventSignalRepository.save(signal);
            log.info("[Agent_1: Intake] LIVE weather signal persisted: Temp={}°C, Precip={}mm, Wind={}km/h, Severity={}/100",
                    tempC, precipMm, windKmh, severity);

            // Push to frontend via WebSocket
            messagingTemplate.convertAndSend("/topic/signals", signal);

            return signal;

        } catch (Exception e) {
            log.error("[Agent_1: Intake] Failed to fetch from Open-Meteo: {}", e.getMessage());
            throw new RuntimeException("Open-Meteo API call failed", e);
        }
    }

    /**
     * Calculates a severity score (0-100) based on real weather conditions.
     * Thresholds are tuned for the Murree region (mountain climate).
     */
    private int calculateWeatherSeverity(double tempC, double precipMm, double windKmh) {
        int score = 0;

        // Temperature severity (sub-zero = dangerous for entrapment)
        if (tempC <= -10) score += 40;
        else if (tempC <= -5) score += 30;
        else if (tempC <= 0) score += 20;
        else if (tempC <= 5) score += 10;

        // Precipitation severity (heavy snow/rain)
        if (precipMm >= 30) score += 35;
        else if (precipMm >= 15) score += 25;
        else if (precipMm >= 5) score += 15;
        else if (precipMm >= 1) score += 5;

        // Wind severity
        if (windKmh >= 60) score += 25;
        else if (windKmh >= 40) score += 15;
        else if (windKmh >= 20) score += 10;
        else if (windKmh >= 10) score += 5;

        return Math.min(score, 100);
    }
}
