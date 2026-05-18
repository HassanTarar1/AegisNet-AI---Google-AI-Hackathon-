package com.aegisnet.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AegisNetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F172A) // aegis-dark
                ) {
                    CitizenDashboard()
                }
            }
        }
    }
}

@Composable
fun CitizenDashboard() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("AegisNet Mobile", color = Color.White, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        // Mock Alert Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("CRITICAL ALERT", color = Color(0xFFEF4444), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Murree Snowstorm Entrapment Risk", color = Color.White)
                Text("Avoid Expressway. Evacuation routes updated.", color = Color.LightGray)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { /* TODO: Report Incident API */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Report Emergency")
        }
    }
}

@Composable
fun AegisNetTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}
