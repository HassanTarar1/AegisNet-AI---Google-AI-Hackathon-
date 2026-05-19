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
import java.util.ArrayList;
import java.util.List;

/**
 * GDELT Project Integration Service
 * Fetches REAL-TIME global crisis news from the GDELT DOC 2.0 API (100% free, no API key required).
 * Filters for crisis/disaster-related articles mentioning Pakistan or Murree.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GdeltService {

    private final EventSignalRepository eventSignalRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // GDELT DOC 2.0 API — searches global news articles in real-time
    private static final String GDELT_DOC_API =
            "https://api.gdeltproject.org/api/v2/doc/doc?query=%s&mode=ArtList&maxrecords=%d&format=json&timespan=24h";

    /**
     * Fetches real crisis/disaster news articles mentioning Pakistan from GDELT.
     * Each article is converted into an EventSignal for the correlation pipeline.
     */
    public List<EventSignal> fetchAndIngestCrisisNews() {
        // Search for disaster/crisis related news about Pakistan
        String query = "(crisis OR disaster OR flood OR earthquake OR storm OR emergency) Pakistan";
        String url = String.format(GDELT_DOC_API, encodeQuery(query), 5);

        log.info("[Agent_3: Correlation] Fetching LIVE crisis news from GDELT Project...");

        List<EventSignal> signals = new ArrayList<>();

        try {
            RestTemplate restTemplate = new RestTemplate();
            String jsonResponse = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode articles = root.path("articles");

            if (articles.isMissingNode() || !articles.isArray() || articles.isEmpty()) {
                log.info("[Agent_3: Correlation] No matching crisis articles found in GDELT for Pakistan.");
                return signals;
            }

            for (JsonNode article : articles) {
                String title = article.path("title").asText("Unknown");
                String articleUrl = article.path("url").asText("");
                String source = article.path("domain").asText("Unknown");
                String seenDate = article.path("seendate").asText("");
                String language = article.path("language").asText("English");

                // Build the payload with real article data
                String rawPayload = String.format(
                    "{\"source\": \"GDELT (LIVE)\", \"title\": \"%s\", \"domain\": \"%s\", " +
                    "\"url\": \"%s\", \"language\": \"%s\", \"seen_date\": \"%s\"}",
                    escapeJson(title), escapeJson(source), escapeJson(articleUrl), language, seenDate
                );

                EventSignal signal = new EventSignal();
                signal.setSource("GDELT_NEWS");
                signal.setType("CRISIS_NEWS_SIGNAL");
                signal.setRawPayload(rawPayload);
                signal.setLatitude(30.3753); // Pakistan centroid
                signal.setLongitude(69.3451);
                signal.setLocationDescription("Pakistan (GDELT Global News)");
                signal.setSeverityScore(calculateNewsSeverity(title));
                signal.setTimestamp(LocalDateTime.now());
                signal.setProcessed(false);
                signal.setConfidence(0.75); // News articles need verification

                eventSignalRepository.save(signal);
                signals.add(signal);

                log.info("[Agent_3: Correlation] GDELT signal ingested: \"{}\" from {}", title, source);

                // Push each signal to frontend
                messagingTemplate.convertAndSend("/topic/signals", signal);
            }

            log.info("[Agent_3: Correlation] {} crisis news signals ingested from GDELT.", signals.size());
            return signals;

        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("429")) {
                log.warn("[Agent_3: Correlation] GDELT rate limit hit. Please wait 5 seconds before retrying.");
                return signals; // Return empty list gracefully
            }
            log.error("[Agent_3: Correlation] Failed to fetch from GDELT: {}", msg);
            return signals; // Return empty list instead of crashing
        }
    }

    /**
     * Calculates a rough severity score based on keywords in the article title.
     */
    private int calculateNewsSeverity(String title) {
        String lower = title.toLowerCase();
        int score = 30; // base score for any crisis article

        if (lower.contains("dead") || lower.contains("killed") || lower.contains("casualties")) score += 30;
        if (lower.contains("emergency") || lower.contains("urgent")) score += 20;
        if (lower.contains("flood") || lower.contains("earthquake") || lower.contains("storm")) score += 15;
        if (lower.contains("rescue") || lower.contains("trapped")) score += 15;
        if (lower.contains("warning") || lower.contains("alert")) score += 10;

        return Math.min(score, 100);
    }

    private String encodeQuery(String query) {
        return query.replace(" ", "%20").replace("(", "%28").replace(")", "%29");
    }

    private String escapeJson(String value) {
        return value.replace("\"", "'").replace("\\", "/");
    }
}
