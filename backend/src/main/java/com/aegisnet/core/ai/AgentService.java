package com.aegisnet.core.ai;

import com.aegisnet.core.model.EventSignal;
import com.aegisnet.core.model.CrisisAlert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentService {

    private final ChatClient.Builder chatClientBuilder;

    /**
     * Agent 2: Social Verification Agent
     * Verifies the credibility of social media signals.
     */
    public Double verifySocialSignal(EventSignal signal) {
        ChatClient chatClient = chatClientBuilder.build();
        
        String prompt = "You are the Crisis Credibility Engine. Analyze the following social media report and determine its credibility score from 0.0 to 1.0. " +
                        "Consider spam probability, panic exaggeration, and historical trustworthiness. Output ONLY a double value between 0.0 and 1.0.\n\n" +
                        "Report: " + signal.getRawPayload();

        String response = chatClient.prompt().user(prompt).call().content();
        
        try {
            return Double.parseDouble(response.trim());
        } catch (NumberFormatException e) {
            log.error("Failed to parse credibility score from Gemini: {}", response);
            return 0.5; // Default fallback
        }
    }

    /**
     * Agent 4: Predictive Escalation Agent
     * Predicts escalation risks based on correlated signals.
     */
    public CrisisAlert predictEscalation(String correlatedContext) {
        ChatClient chatClient = chatClientBuilder.build();
        
        String prompt = "You are the Predictive Escalation Agent. Based on the following correlated crisis context, predict the escalation risk. " +
                        "Determine if there is a MASS_ENTRAPMENT_RISK or FLOOD_ESCALATION. " +
                        "Provide your response in JSON format with fields: type, title, description, casualtyRiskScore (0-100), escalationProbability (0-100), and recommendedPreventiveActions (array of strings).\n\n" +
                        "Context: " + correlatedContext;
                        
        String response = chatClient.prompt().user(prompt).call().content();
        
        // In a full implementation, we would parse this JSON into a CrisisAlert object.
        // For the sake of the hackathon skeleton, we will return a mock parsed object.
        log.info("Gemini Prediction Response: {}", response);
        
        CrisisAlert alert = new CrisisAlert();
        alert.setType("MASS_ENTRAPMENT_RISK");
        alert.setTitle("Murree Snowstorm Entrapment Prediction");
        alert.setDescription("High probability of mass vehicle entrapment due to severe snow and traffic congestion.");
        alert.setCasualtyRiskScore(85);
        alert.setEscalationProbability(95);
        
        return alert;
    }
}
