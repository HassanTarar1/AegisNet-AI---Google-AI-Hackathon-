<div align="center">
  <h1>🛡️ AegisNet AI</h1>
  <p><strong>Autonomous Crisis Intelligence & Response Ecosystem</strong></p>
  <p><i>Google Antigravity Challenge 3</i></p>
</div>

---

## 🌍 Overview

**AegisNet AI** is an enterprise-grade, multi-agent artificial intelligence ecosystem designed to predict, validate, monitor, and coordinate disaster responses *before* catastrophic escalation occurs. 

Moving beyond traditional reactive emergency systems, AegisNet proactively ingests multi-source environmental and social data to intercept mass-casualty events using a swarm of autonomous AI agents powered by **Google Gemini 2.0 Flash**.

## 🧠 The Multi-Agent Ecosystem

AegisNet operates using a sophisticated pipeline of AI agents that communicate autonomously via event streams:

```mermaid
graph TD
    %% Inputs
    C_Rep[Citizen Voice/Text Reports] --> A1
    S_Sig[Social Media Panic Signals] --> A2
    Env[Sensor & Weather Data] --> A3
    
    %% Agents
    subgraph On-Device Mobile AI (Gemini 2.0 Flash)
        A1[Multilingual NLP Agent]
        A2[Social Verification Agent]
        A3[Crisis Correlation Agent]
        A1 --> |Translated & Classified Incident| A4
        A2 --> |Credibility Score: 0.0 - 1.0| A4
        A3 --> |Risk Clusters| A4
        A4{Predictive Escalation Agent}
    end
    
    %% Outputs & Command Center
    A4 -->|Critical Alert Trigger| EOC[EOC Tactical Command Center]
    
    subgraph Response Orchestration
        EOC --> R1[Resource Allocation Advisor]
        R1 -->|Optimized Dispatch Strategy| Teams[Rescue Teams & Medics]
        EOC --> R2[Drone Coordination Agent]
        R2 -->|Thermal Scanning Routes| Drones[UAV Swarms]
    end
    
    %% Styling
    classDef ai fill:#0b5345,stroke:#2ecc71,stroke-width:2px,color:#fff;
    classDef input fill:#1c2833,stroke:#5dade2,stroke-width:1px,color:#fff;
    classDef output fill:#78281f,stroke:#e74c3c,stroke-width:2px,color:#fff;
    
    class A1,A2,A3,A4,R1,R2 ai;
    class C_Rep,S_Sig,Env input;
    class EOC,Teams,Drones output;
```

1. **Multilingual NLP Agent:** Normalizes raw, noisy multi-lingual signals (Urdu/Pashto/English) from hardware sensors and citizen reports into structured data.
2. **Social Verification Agent (Crisis Credibility Engine):** Analyzes social media panic signals for authenticity, filtering out spam and misinformation to output a mathematical credibility score.
3. **Crisis Correlation Agent:** Detects overlapping risk clusters (e.g., severe weather + traffic gridlock).
4. **Predictive Escalation Agent (The Core Brain):** Evaluates clustered events against historical models to predict casualty risks and generate preventive action mandates.
5. **Resource & Drone Coordination Agents:** Simulates the dispatch of autonomous drone swarms for thermal scanning and optimally allocates ground rescue teams.

## ❄️ Core Demo Scenario: Murree Snowstorm Prevention
Our hackathon demonstration revolves around preventing the tragic Murree snowstorm entrapment scenario.

The system simultaneously ingests a severe weather anomaly (heavy snowfall) and verifies spiking social media panic regarding traffic. The **Predictive Escalation Agent** immediately calculates an 85% probability of mass vehicle entrapment and autonomously:
- Triggers a **Critical Crisis Alert**.
- Generates a dashboard overlay for the Emergency Operations Center.
- Simulates the dispatch of thermal-imaging drones.
- Recommends immediate expressway closure.

## 🛠️ Technology Stack

AegisNet is a modular, event-driven microservices platform built for enterprise scalability:

- **AI Orchestration:** Google Generative AI SDK (Gemini 2.0 Flash), Spring AI
- **Backend (The Core):** Java 21, Spring Boot, WebSockets (STOMP)
- **Data Layer:** PostgreSQL (PostGIS for Spatial queries), Redis, Docker
- **Command Center (Frontend):** Angular 18, TailwindCSS (Military-grade Dark Mode), RxStomp
- **Citizen Application (Mobile):** Android, Kotlin, Jetpack Compose

## 🚀 How to Run Locally

### Prerequisites
- Docker & Docker Compose
- Java 21 & Maven
- Node.js & Angular CLI
- Android Studio (For Mobile App)
- Google Cloud Platform Account (Vertex AI access)

### 1. Start the Database Layer
```bash
docker-compose up -d
```

### 2. Start the AI Orchestration Backend
Provide your GCP credentials and start the Spring Boot server:
```bash
cd backend
export GCP_PROJECT_ID="your-project-id"
export GCP_LOCATION="us-central1"
mvn spring-boot:run
```

### 3. Launch the Tactical Command Center
Install dependencies and run the Angular dashboard:
```bash
cd frontend
npm install
ng serve --open
```

### 4. Launch the Mobile Application
1. Open **Android Studio**.
2. Open the `mobile` folder inside this repository.
3. Allow Gradle to sync.
4. Run `app` on your Android Emulator.

## 📊 Live Observability
AegisNet prioritizes **Explainable AI**. The Command Center dashboard features a live "Agent Trace" panel. Every decision, confidence score, and verification step made by Gemini is streamed in real-time to the UI, allowing human operators to trust the AI's autonomous predictions.

---
*Built for the Google AI Hackathon.*
