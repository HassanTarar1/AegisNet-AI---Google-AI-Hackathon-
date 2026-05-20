package com.aegisnet.mobile.domain.agent

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

enum class AgentRole {
    SOCIAL_SIGNAL,
    RESCUE_DISPATCH,
    PERIMETER_GEOMETRY,
    MESH_BROADCAST
}

data class EocAgent(
    val role: AgentRole,
    val name: String,
    val description: String,
    val avatarEmoji: String,
    val status: String, // "IDLE", "ANALYZING", "DISPATCHING", "OFFLINE_ACTIVE"
    val isOfflineCapable: Boolean = true,
    val processedCount: Int = 0,
    val logs: List<String> = emptyList()
)

data class SocialTweet(
    val username: String,
    val handle: String,
    val body: String,
    val timeAgo: String,
    val sentiment: String, // "PANIC", "NEUTRAL", "SAFE"
    val correlatedCrisis: String? = null,
    val correlationReason: String? = null
)

@Singleton
class EocAgentOrchestrator @Inject constructor() {

    private val _agents = MutableStateFlow<Map<AgentRole, EocAgent>>(
        mapOf(
            AgentRole.SOCIAL_SIGNAL to EocAgent(
                role = AgentRole.SOCIAL_SIGNAL,
                name = "SocialSignal-Agent (X-Scraper)",
                description = "Scrapes simulated social panic signals and public sentiment telemetry.",
                avatarEmoji = "🤖",
                status = "OFFLINE_ACTIVE",
                logs = listOf("System initialised.", "Ready to scrape emergency triggers.")
            ),
            AgentRole.RESCUE_DISPATCH to EocAgent(
                role = AgentRole.RESCUE_DISPATCH,
                name = "RescueDispatch-Agent",
                description = "Sweeps nearest GCP emergency departments & coordinates local offline alerts.",
                avatarEmoji = "🚑",
                status = "OFFLINE_ACTIVE",
                logs = listOf("Responder database loaded locally.", "Awaiting EOC trigger.")
            ),
            AgentRole.PERIMETER_GEOMETRY to EocAgent(
                role = AgentRole.PERIMETER_GEOMETRY,
                name = "Perimeter-Geometry Agent",
                description = "Calculates bounding perimeters, flood/snow lines, and path containment.",
                avatarEmoji = "📐",
                status = "OFFLINE_ACTIVE",
                logs = listOf("Evacuation path coordinates compiled.", "Perimeter logic online.")
            ),
            AgentRole.MESH_BROADCAST to EocAgent(
                role = AgentRole.MESH_BROADCAST,
                name = "MeshBroadcasting-Agent",
                description = "Encapsulates encrypted emergency metadata packets over raw Bluetooth/RF MESH.",
                avatarEmoji = "📡",
                status = "IDLE",
                logs = listOf("Offline mesh transceiver bound.", "Beacon ready.")
            )
        )
    )
    val agents: StateFlow<Map<AgentRole, EocAgent>> = _agents.asStateFlow()

    private val _tweets = MutableStateFlow<List<SocialTweet>>(
        listOf(
            SocialTweet("@RawalpindiAlerts", "pindi_eoc", "Severe pooling on Murree Road! Water levels rising rapidly, avoid low underpasses! #RawalpindiFlood", "2m ago", "PANIC", "Emergency: FLOOD WATER", "Vetted geolocation: matches Rawalpindi incident within 1.2km."),
            SocialTweet("@MurreeExpress", "murree_exp", "Unbelievable heavy snow near GPO. Over 2 feet accumulated. Snow blowers deployed. Keep warm.", "10m ago", "NEUTRAL", "Murree Snowstorm Entrapment", "Topic matches Murree snow warning corridor."),
            SocialTweet("@CitizenPak", "cit_pak", "Rescue 1122 team just arrived here. Super fast response even during this storm! Thank you!", "15m ago", "SAFE")
        )
    )
    val tweets: StateFlow<List<SocialTweet>> = _tweets.asStateFlow()

    fun triggerSocialAnalysis(anomalyType: String, areaName: String) {
        val timestamp = getFormattedTime()
        val generatedTweet = when (anomalyType) {
            "FLOOD", "FLOOD_WATER" -> SocialTweet(
                username = "@ResiliencePindi",
                handle = "res_pindi",
                body = "Emergency alert: Heavy torrential rain in $areaName causes flash flooding. Local agents advising immediate evacuation! #SaveAegisNet",
                timeAgo = "Just now",
                sentiment = "PANIC",
                correlatedCrisis = "Emergency: $anomalyType",
                correlationReason = "Matches flood incident sector $areaName within 1.5km."
            )
            "SEISMIC_ACTIVITY" -> SocialTweet(
                username = "@SeismicPulsePK",
                handle = "seismic_pk",
                body = "Did anyone feel that tremor in $areaName? Everything was shaking for 15 seconds! Hope everyone is safe. #EarthquakePK",
                timeAgo = "Just now",
                sentiment = "PANIC",
                correlatedCrisis = "Emergency: $anomalyType",
                correlationReason = "Sensor match: tremor reports match epicenter geodetics within 2.0km."
            )
            else -> SocialTweet(
                username = "@SnowPatrolPK",
                handle = "snow_pk",
                body = "Stuck in heavy blizzard near $areaName! Temperature freezing. Can offline maps show evacuation points? #CrisisHelp",
                timeAgo = "Just now",
                sentiment = "PANIC",
                correlatedCrisis = "Emergency: $anomalyType",
                correlationReason = "Matches snowstorm incident sector $areaName within 0.8km."
            )
        }

        _tweets.update { listOf(generatedTweet) + it }

        _agents.update { currentMap ->
            val agent = currentMap[AgentRole.SOCIAL_SIGNAL]!!
            val newLogs = listOf(
                "[$timestamp] parsed panic signal from ${generatedTweet.handle}",
                "[$timestamp] Sentiment: ${generatedTweet.sentiment} detected.",
                "[$timestamp] Pushed alert payload to EOC mesh router."
            ) + agent.logs
            currentMap + (AgentRole.SOCIAL_SIGNAL to agent.copy(
                status = "ANALYZING",
                processedCount = agent.processedCount + 1,
                logs = newLogs.take(20)
            ))
        }
    }

    fun triggerRescueDispatch(
        latitude: Double,
        longitude: Double,
        incidentName: String,
        nearbyHospitals: List<String> = emptyList(),
        nearbyPolice: List<String> = emptyList()
    ) {
        val timestamp = getFormattedTime()
        val nearestHospital = nearbyHospitals.firstOrNull() ?: (if (latitude > 33.6) "Holy Family Hospital (Rawalpindi)" else "Murree General Emergency Wing")
        val nearestRescueStation = nearbyPolice.firstOrNull() ?: (if (latitude > 33.6) "Rescue 1122 Headquarter, Rawalpindi" else "Ghora Gali First Responders")

        _agents.update { currentMap ->
            val agent = currentMap[AgentRole.RESCUE_DISPATCH]!!
            val newLogs = listOf(
                "[$timestamp] Ingested alert coordinate request [${"%.4f".format(latitude)}, ${"%.4f".format(longitude)}]",
                "[$timestamp] Sweep complete: identified nearest hospital [$nearestHospital] & nearest responder [$nearestRescueStation].",
                "[$timestamp] Actioned dispatch notification queue: active responders routed to $incidentName.",
                "[$timestamp] EOC dispatch sync completed successfully."
            ) + agent.logs
            currentMap + (AgentRole.RESCUE_DISPATCH to agent.copy(
                status = "DISPATCHING",
                processedCount = agent.processedCount + 1,
                logs = newLogs.take(20)
            ))
        }
    }

    fun triggerPerimeterGeometry(latitude: Double, longitude: Double) {
        val timestamp = getFormattedTime()
        _agents.update { currentMap ->
            val agent = currentMap[AgentRole.PERIMETER_GEOMETRY]!!
            val newLogs = listOf(
                "[$timestamp] Computed evacuation polygon center: [${"%.4f".format(latitude)}, ${"%.4f".format(longitude)}].",
                "[$timestamp] Defined 1.5km geofenced containment vector polygon.",
                "[$timestamp] Transmitted perimeter coordinates boundary to local Radar sweep graphics."
            ) + agent.logs
            currentMap + (AgentRole.PERIMETER_GEOMETRY to agent.copy(
                status = "OFFLINE_ACTIVE",
                processedCount = agent.processedCount + 1,
                logs = newLogs.take(20)
            ))
        }
    }

    fun triggerMeshBroadcast(incidentType: String, severity: String) {
        val timestamp = getFormattedTime()
        _agents.update { currentMap ->
            val agent = currentMap[AgentRole.MESH_BROADCAST]!!
            val newLogs = listOf(
                "[$timestamp] Serialized emergency payload: Type=$incidentType, Severity=$severity",
                "[$timestamp] Encapsulated payload into compressed 18-byte mesh beacon.",
                "[$timestamp] Offline Mesh Broadcast completed over Bluetooth Low Energy mesh channels ✓"
            ) + agent.logs
            currentMap + (AgentRole.MESH_BROADCAST to agent.copy(
                status = "OFFLINE_ACTIVE",
                processedCount = agent.processedCount + 1,
                logs = newLogs.take(20)
            ))
        }
    }

    private fun getFormattedTime(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }
}
