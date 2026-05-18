package com.aegisnet.core.service;

import com.aegisnet.core.model.EventSignal;
import com.aegisnet.core.repository.EventSignalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MockIngestionService {

    private final EventSignalRepository eventSignalRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void simulateMurreeWeatherAnomaly() {
        EventSignal signal = new EventSignal();
        signal.setSource("SUPARCO_WEATHER");
        signal.setType("WEATHER_ANOMALY");
        signal.setRawPayload("{\"precipitation_rate_mm_hr\": 50, \"temperature_c\": -5, \"wind_speed_kmh\": 60}");
        signal.setLatitude(33.9070);
        signal.setLongitude(73.3943);
        signal.setLocationDescription("Murree Expressway");
        signal.setSeverityScore(85);
        signal.setTimestamp(LocalDateTime.now());
        signal.setProcessed(false);
        
        eventSignalRepository.save(signal);
        log.info("Simulated SUPARCO Weather Anomaly saved: {}", signal.getId());
        
        // Push to frontend
        messagingTemplate.convertAndSend("/topic/signals", signal);
    }

    public void simulateSocialPanic() {
        EventSignal signal = new EventSignal();
        signal.setSource("SOCIAL_X");
        signal.setType("SOCIAL_PANIC");
        signal.setRawPayload("{\"text\": \"Cars stuck in heavy snow on Murree road for 5 hours. People are freezing!\", \"user\": \"@local_citizen\"}");
        signal.setLatitude(33.9100);
        signal.setLongitude(73.3900);
        signal.setLocationDescription("Near Guldana, Murree");
        signal.setSeverityScore(90);
        signal.setTimestamp(LocalDateTime.now());
        signal.setProcessed(false);
        
        eventSignalRepository.save(signal);
        log.info("Simulated Social Panic Signal saved: {}", signal.getId());
        
        // Push to frontend
        messagingTemplate.convertAndSend("/topic/signals", signal);
    }
}
