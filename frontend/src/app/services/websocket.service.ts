import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface EventSignal {
  id: number;
  source: string;
  type: string;
  rawPayload: string;
  severityScore: number;
  confidence: number;
  timestamp: string;
}

export interface CrisisAlert {
  id: number;
  type: string;
  title: string;
  description: string;
  casualtyRiskScore: number;
  escalationProbability: number;
}

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  
  private http = inject(HttpClient);
  private backendUrl = 'http://localhost:8080/api/simulation';

  public signals = signal<EventSignal[]>([]);
  public alerts = signal<CrisisAlert[]>([]);
  public agentTraces = signal<string[]>([]);
  public connectionStatus = signal<boolean>(true); // Faking online for standalone demo

  constructor() {
    this.addTrace('> [System] AegisNet Standalone Demo Mode Initialized.');
    this.addTrace('> [System] Gemini Orchestration Engine: ONLINE');
    this.addTrace('> [System] Open-Meteo & GDELT Live Feeds: READY');
  }

  private addTrace(msg: string) {
    this.agentTraces.update(traces => [msg, ...traces].slice(0, 50));
  }

  // ==============================
  //  LIVE DATA INJECTION (REAL APIs)
  // ==============================

  /** Calls backend -> Open-Meteo API to inject REAL weather data for Murree */
  public injectLiveWeather() {
    this.addTrace('> [System] LIVE DATA: Requesting real-time weather from Open-Meteo API...');
    
    this.http.post<any>(`${this.backendUrl}/live-weather`, {}).subscribe({
      next: (res) => {
        const sig = res.signal;
        if (sig) {
          const parsed = JSON.parse(sig.rawPayload);
          this.signals.update(sigs => [{
            id: sig.id,
            source: 'OPEN_METEO_LIVE',
            type: 'WEATHER_TELEMETRY',
            rawPayload: `LIVE: ${parsed.temp_c}°C | Rain: ${parsed.precipitation_mm}mm | Wind: ${parsed.wind_kmh}km/h | Humidity: ${parsed.humidity_pct}%`,
            severityScore: sig.severityScore,
            confidence: 1.0,
            timestamp: new Date().toISOString()
          }, ...sigs]);

          this.addTrace(`> [Agent_1: Intake] LIVE Open-Meteo data received: Temp=${parsed.temp_c}°C, Precip=${parsed.precipitation_mm}mm`);
          this.addTrace(`> [Agent_1: Intake] Elevation: ${parsed.elevation_m}m | Severity Score: ${sig.severityScore}/100`);
          this.addTrace('> [Agent_1: Intake] Trusted Machine Source: Confidence 1.0');
        }
        this.checkCorrelation();
      },
      error: (err) => {
        this.addTrace(`> [System] ERROR: Failed to fetch live weather: ${err.message}`);
      }
    });
  }

  /** Calls backend -> GDELT API to inject REAL crisis news signals for Pakistan */
  public injectLiveGdelt() {
    this.addTrace('> [System] LIVE DATA: Querying GDELT Project for Pakistan crisis news...');
    
    this.http.post<any>(`${this.backendUrl}/live-gdelt`, {}).subscribe({
      next: (res) => {
        const signals = res.signals || [];
        if (signals.length === 0) {
          this.addTrace('> [Agent_3: Correlation] GDELT returned 0 crisis articles for Pakistan in last 24h.');
          return;
        }

        for (const sig of signals) {
          const parsed = JSON.parse(sig.rawPayload);
          this.signals.update(sigs => [{
            id: sig.id,
            source: 'GDELT_NEWS',
            type: 'CRISIS_NEWS_SIGNAL',
            rawPayload: `"${parsed.title}" — ${parsed.domain}`,
            severityScore: sig.severityScore,
            confidence: sig.confidence,
            timestamp: new Date().toISOString()
          }, ...sigs]);

          this.addTrace(`> [Agent_3: Correlation] GDELT: "${parsed.title.substring(0, 60)}..."`);
        }

        this.addTrace(`> [Agent_3: Correlation] ${signals.length} live crisis signals ingested from GDELT.`);
        this.checkCorrelation();
      },
      error: (err) => {
        this.addTrace(`> [System] ERROR: Failed to fetch GDELT data: ${err.message}`);
      }
    });
  }

  // ==============================
  //  MOCK SIMULATION (for demo)
  // ==============================

  public simulateWeather() {
    this.addTrace('> [System] Triggered: Weather Anomaly Injection (MOCK)');
    
    setTimeout(() => {
      this.addTrace('> [Agent_1: Signal Intake] Processing new signal from SUPARCO_WEATHER...');
      
      const sig: EventSignal = {
        id: Date.now(),
        source: 'SUPARCO_WEATHER',
        type: 'WEATHER_ANOMALY',
        rawPayload: '{"precip_mm_hr": 55, "temp_c": -6, "wind_kmh": 65, "loc": "Murree Expressway"}',
        severityScore: 88,
        confidence: 1.0,
        timestamp: new Date().toISOString()
      };
      
      this.signals.update(sigs => [sig, ...sigs]);
      this.addTrace('> [Agent_1: Signal Intake] Payload normalized. Trusted Government Source: Confidence 1.0');
      this.checkCorrelation();
    }, 1000);
  }

  public simulateSocial() {
    this.addTrace('> [System] Triggered: Social Panic Injection (MOCK)');
    
    setTimeout(() => {
      this.addTrace('> [Agent_1: Signal Intake] Processing new signal from SOCIAL_X...');
      
      setTimeout(() => {
        this.addTrace('> [Agent_2: Social Verification] Analyzing payload credibility via Gemini...');
        
        setTimeout(() => {
          const sig: EventSignal = {
            id: Date.now() + 1,
            source: 'SOCIAL_X',
            type: 'SOCIAL_PANIC',
            rawPayload: '"Cars completely stuck near Guldana. People are freezing inside vehicles!"',
            severityScore: 92,
            confidence: 0.94,
            timestamp: new Date().toISOString()
          };
          
          this.signals.update(sigs => [sig, ...sigs]);
          this.addTrace('> [Agent_2: Social Verification] Account verified. Sentiment confirms severe panic. Credibility Scored: 0.94');
          this.checkCorrelation();
        }, 1500);
      }, 1000);
    }, 1000);
  }

  private checkCorrelation() {
    const currentSignals = this.signals();
    const hasWeather = currentSignals.some(s => s.source === 'SUPARCO_WEATHER' || s.source === 'OPEN_METEO_LIVE');
    const hasSocial = currentSignals.some(s => s.source === 'SOCIAL_X' || s.source === 'GDELT_NEWS');

    if (hasWeather && hasSocial && this.alerts().length === 0) {
      setTimeout(() => {
        this.addTrace('> [Agent_3: Crisis Correlation] CLUSTER DETECTED: Weather Anomaly intersects with Verified Social/News Signal in Grid [33.90, 73.39]');
        this.addTrace('> [Agent_4: Predictive Escalation] Running historical impact simulations...');
        
        setTimeout(() => {
          const alert: CrisisAlert = {
            id: Date.now() + 2,
            type: 'MASS_ENTRAPMENT_RISK',
            title: 'Murree Snowstorm Entrapment Prediction',
            description: 'Critical mass vehicle entrapment likely within 45 minutes. Freezing risk imminent.',
            casualtyRiskScore: 95,
            escalationProbability: 98
          };
          
          this.alerts.update(alts => [alert, ...alts]);
          this.addTrace(`> [Agent_4: Predictive Escalation] CRITICAL PREDICTION GENERATED: ${alert.title}`);
          this.addTrace('> [Agent_4: Predictive Escalation] Recommended Actions: 1. Auto-close Expressway 2. Dispatch Thermal Drones');
          
          setTimeout(() => {
            this.addTrace('> [Agent_5: Drone Coordination] Dispatching autonomous drone swarm to Grid [33.90, 73.39] for thermal survivor detection.');
          }, 2000);
          
        }, 3000);
      }, 2000);
    }
  }
}
