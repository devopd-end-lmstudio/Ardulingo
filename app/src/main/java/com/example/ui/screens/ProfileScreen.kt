package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.database.UserStats
import com.example.ui.theme.*
import com.example.ui.viewmodel.ProgressViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProgressViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.userStats.collectAsStateWithLifecycle()
    val completedList by viewModel.completedLessons.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    var showResetDialog by remember { mutableStateOf(false) }

    // Sandbox Simulator State
    var selectedSketch by remember { mutableIntStateOf(0) } // 0: Blink LED, 1: Analog Read, 2: Wi-Fi Scanner
    var isSimulating by remember { mutableStateOf(false) }
    var simulationProgress by remember { mutableFloatStateOf(0f) }
    val serialMonitorLogs = remember { mutableStateListOf<String>() }

    val sketches = listOf(
        "1. Blink LED Sketch" to """
void setup() {
  pinMode(13, OUTPUT);
}

void loop() {
  digitalWrite(13, HIGH);
  delay(1000);
  digitalWrite(13, LOW);
  delay(1000);
}
        """.trimIndent(),
        "2. Analog Potentiometer" to """
void setup() {
  Serial.begin(115200);
  pinMode(34, INPUT); // ADC1
}

void loop() {
  int value = analogRead(34);
  Serial.print("Potentiometer value: ");
  Serial.println(value);
  delay(500);
}
        """.trimIndent(),
        "3. ESP32 Wi-Fi Client" to """
#include <WiFi.h>

void setup() {
  Serial.begin(115200);
  WiFi.begin("ArduNet", "pass123");
}

void loop() {
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("Network Connected!");
    Serial.print("Local IP: ");
    Serial.println(WiFi.localIP());
  } else {
    Serial.println("Reconnecting...");
  }
  delay(2000);
}
        """.trimIndent()
    )

    // Simulation simulation execution flow
    LaunchedEffect(isSimulating, selectedSketch) {
        if (!isSimulating) {
            simulationProgress = 0f
            return@LaunchedEffect
        }

        serialMonitorLogs.clear()
        serialMonitorLogs.add("[System] Initializing compile toolchain...")
        delay(600)
        serialMonitorLogs.add("[System] Verifying headers and libraries...")
        delay(600)
        simulationProgress = 0.4f
        serialMonitorLogs.add("[Compiler] Success! ROM size: 23% used.")
        delay(500)
        simulationProgress = 0.8f
        serialMonitorLogs.add("[Uploader] Writing sketch to flash...")
        delay(800)
        simulationProgress = 1.0f
        serialMonitorLogs.add("[Uploader] Done! Resetting board...")
        delay(500)
        serialMonitorLogs.add("================ SERIAL MONITOR ================")

        when (selectedSketch) {
            0 -> {
                // Blink loop simulation
                for (i in 1..4) {
                    serialMonitorLogs.add("[Pin 13] DIGITAL WRITE -> HIGH (LED ON)")
                    delay(1000)
                    serialMonitorLogs.add("[Pin 13] DIGITAL WRITE -> LOW (LED OFF)")
                    delay(1000)
                }
            }
            1 -> {
                // Analog reading potentiometer simulation
                val readings = listOf(512, 1024, 2048, 3072, 4095)
                for (reading in readings) {
                    serialMonitorLogs.add("Potentiometer value: $reading")
                    delay(1000)
                }
            }
            2 -> {
                // Wi-Fi connecting status simulation
                serialMonitorLogs.add("WiFi connecting...")
                delay(1200)
                serialMonitorLogs.add("Network Connected!")
                serialMonitorLogs.add("Local IP: 192.168.1.144")
                delay(1500)
                serialMonitorLogs.add("Network Ping OK: 22ms")
                delay(1500)
                serialMonitorLogs.add("IP Lease Renewed.")
            }
        }
        serialMonitorLogs.add("[System] Simulation completed.")
        isSimulating = false
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Achievement Header Title
        item {
            Text(
                text = "YOUR PROFILE & STATS",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                color = BrandTeal,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Stats Dashboard Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(BrandTeal.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_mascot),
                                contentDescription = "Profile Avatar",
                                modifier = Modifier.size(56.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Mascot's Apprentice",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                            )
                            Text(
                                text = "Current level: ${stats?.level ?: 1}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    // Big stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ProfileStatNumber(title = "Total XP", value = "${stats?.xp ?: 0}", icon = Icons.Default.OfflineBolt, color = InfoBlue)
                        ProfileStatNumber(title = "Day Streak", value = "${stats?.streak ?: 0} days", icon = Icons.Default.LocalFireDepartment, color = GoldYellow)
                        ProfileStatNumber(title = "Gems", value = "${stats?.gems ?: 0}", icon = Icons.Default.Favorite, color = GemPink)
                    }

                    // Progress metric
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Lessons progress: ${completedList.distinctBy { it.lessonId }.size} completed",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        LinearProgressIndicator(
                            progress = {
                                val completed = completedList.distinctBy { it.lessonId }.size.toFloat()
                                if (completed > 0) completed / 8f else 0.05f
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = BrandGreen,
                            trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }

        // Sandbox section title
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Terminal, contentDescription = "Sandbox", tint = BrandOrange)
                Text(
                    text = "ARDUINO CODE SIMULATOR",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Interactive Code Sandbox Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Choose a micro-sketch, compile, and execute on our virtual core board!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Sketch selection tabs
                    ScrollableTabRow(
                        selectedTabIndex = selectedSketch,
                        edgePadding = 0.dp,
                        divider = {},
                        containerColor = Color.Transparent,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        sketches.forEachIndexed { i, (name, _) ->
                            Tab(
                                selected = selectedSketch == i,
                                onClick = { selectedSketch = i },
                                text = { Text(name.take(15) + "...", fontWeight = FontWeight.Bold) }
                            )
                        }
                    }

                    // Code Editor Display
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkBackground),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = sketches[selectedSketch].first,
                                    color = BrandTeal,
                                    style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace)
                                )
                                Icon(imageVector = Icons.Default.Code, contentDescription = "Code", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = sketches[selectedSketch].second,
                                color = TextLight,
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // Upload Simulation Button
                    Button(
                        onClick = { isSimulating = true },
                        enabled = !isSimulating,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("compile_simulate_button")
                    ) {
                        Icon(imageVector = Icons.Default.DeveloperBoard, contentDescription = "Simulate Upload")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = if (isSimulating) "UPLOADING..." else "COMPILE & UPLOAD", fontWeight = FontWeight.Bold)
                    }

                    // Simulated board hardware state
                    if (isSimulating || serialMonitorLogs.isNotEmpty()) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "VIRTUAL HARDWARE BOARD STATUS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Power LED
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(BrandOrange)
                                        )
                                        Text(text = "PWR", style = MaterialTheme.typography.labelSmall)
                                    }

                                    // TX RX Blinking LED simulation
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(if (isSimulating) GoldYellow else Color.Gray)
                                        )
                                        Text(text = "TX/RX", style = MaterialTheme.typography.labelSmall)
                                    }

                                    // GPIO 13 Led status blinking simulation
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        var pinLedState by remember { mutableStateOf(false) }
                                        // Blink pin 13 if simulating sketch 0
                                        LaunchedEffect(isSimulating, selectedSketch) {
                                            if (isSimulating && selectedSketch == 0) {
                                                while (true) {
                                                    pinLedState = !pinLedState
                                                    delay(1000)
                                                }
                                            } else {
                                                pinLedState = false
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(if (pinLedState) BrandGreen else Color.Gray)
                                        )
                                        Text(text = "GPIO13 LED", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Serial Monitor Output Screen
                        Text(text = "Serial Monitor Output:", style = MaterialTheme.typography.labelSmall)
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                reverseLayout = true
                            ) {
                                items(serialMonitorLogs.reversed().size) { index ->
                                    Text(
                                        text = serialMonitorLogs.reversed()[index],
                                        color = if (serialMonitorLogs.reversed()[index].contains("Error") || serialMonitorLogs.reversed()[index].contains("Reconnecting")) GemPink else BrandGreen,
                                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Settings reset progress row
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, GemPink.copy(alpha = 0.4f)),
                colors = CardDefaults.cardColors(containerColor = GemPink.copy(alpha = 0.04f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showResetDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Reset All Study Data", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = GemPink)
                        Text(text = "Clear your streaks, gems, XP, and unlock states.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = "Reset Stats", tint = GemPink)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    // Reset Progress Alert Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(text = "Are you absolutely sure?") },
            text = { Text(text = "This will permanently erase your learning achievements, completed lesson marks, accumulated gems, and Morse Code tapper high scores. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetProgress()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GemPink)
                ) {
                    Text("Yes, Reset Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

@Composable
fun ProfileStatNumber(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(24.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
