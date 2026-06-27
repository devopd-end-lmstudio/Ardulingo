package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibrationEffect
import android.view.MotionEvent
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.ProgressViewModel
import java.util.Locale

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MorseScreen(
    viewModel: ProgressViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.userStats.collectAsStateWithLifecycle()
    val sessionScore by viewModel.morseSessionScore.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf(0) } // 0: Tapping Game, 1: Translator, 2: Cheat Sheet

    // Morse Code Alphabet Dictionary
    val morseMap = remember {
        mapOf(
            'A' to ".-", 'B' to "-...", 'C' to "-.-.", 'D' to "-..", 'E' to ".",
            'F' to "..-.", 'G' to "--.", 'H' to "....", 'I' to "..", 'J' to ".---",
            'K' to "-.-", 'L' to ".-..", 'M' to "--", 'N' to "-.", 'O' to "---",
            'P' to ".--.", 'Q' to "--.-", 'R' to ".-.", 'S' to "...", 'T' to "-",
            'U' to "..-", 'V' to "...-", 'W' to ".--", 'X' to "-..-", 'Y' to "-.--",
            'Z' to "--.."
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
            ) {
                Text(
                    text = "MORSE CODE TRAINER",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    modifier = Modifier.padding(16.dp),
                    color = BrandTeal
                )
                
                SecondaryTabRow(selectedTabIndex = activeTab) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Tapping Key", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Translator", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = { Text("Cheat Sheet", fontWeight = FontWeight.Bold) }
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (activeTab) {
                0 -> MorseTapperGame(
                    viewModel = viewModel,
                    morseMap = morseMap,
                    statsHighScore = stats?.morseHighScore ?: 0,
                    sessionScore = sessionScore
                )
                1 -> MorseTranslatorTab(morseMap = morseMap)
                2 -> MorseCheatSheetTab(morseMap = morseMap)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MorseTapperGame(
    viewModel: ProgressViewModel,
    morseMap: Map<Char, String>,
    statsHighScore: Int,
    sessionScore: Int
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    // Game Words List
    val targetWords = remember { listOf("LED", "SOS", "ESP", "PIN", "ADC", "PWM", "MCU", "HIGH", "LOW", "BUS") }
    var currentTargetIndex by remember { mutableIntStateOf(0) }
    val targetWord = targetWords[currentTargetIndex]

    // Current sequence tapped by user
    var tappedSequence by remember { mutableStateOf("") }
    var interpretedWord by remember { mutableStateOf("") }

    // Touch down timing trackers
    var pressStartTime by remember { mutableLongStateOf(0L) }
    var keyIsDown by remember { mutableStateOf(false) }

    // Animate tap state
    val scale = if (keyIsDown) 0.9f else 1.0f

    // Helper to add Dot / Dash
    fun registerMark(isDash: Boolean) {
        tappedSequence += if (isDash) "-" else "."
        
        // Feed haptic click
        try {
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val duration = if (isDash) 150L else 50L
                    vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator.vibrate(if (isDash) 150 else 50)
                }
            }
        } catch (e: Exception) {
            // Safe fallback
        }
    }

    // Helper to evaluate compiled input against target
    fun submitTappedWord() {
        val expectedMorse = targetWord.map { morseMap[it] ?: "" }.joinToString(" ")
        val normalizedTapped = tappedSequence.trim()

        if (normalizedTapped == expectedMorse) {
            viewModel.earnMorsePoints()
            // Next target word
            currentTargetIndex = (currentTargetIndex + 1) % targetWords.size
            tappedSequence = ""
            interpretedWord = ""
        } else {
            // Incorrect, reset
            tappedSequence = ""
            interpretedWord = ""
        }
    }

    // Parse tapped sequence to English characters
    LaunchedEffect(tappedSequence) {
        val parts = tappedSequence.split(" ")
        interpretedWord = parts.map { part ->
            morseMap.entries.firstOrNull { it.value == part }?.key ?: '?'
        }.joinToString("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // High Score / Game progress
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = "Tapping High Score", tint = GoldYellow)
                Text(text = "High Score: $statsHighScore", fontWeight = FontWeight.Bold)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(imageVector = Icons.Default.OfflineBolt, contentDescription = "Active Score", tint = BrandTeal)
                Text(text = "XP Streak: $sessionScore", fontWeight = FontWeight.Bold, color = BrandTeal)
            }
        }

        // Instructions and Target Word
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "TAP OUT THIS MICROCONTROLLER WORD:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = targetWord,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp
                    )
                )

                // Render expected dot/dash string
                val expectedMorseStr = targetWord.map { morseMap[it] ?: "" }.joinToString("   ")
                Text(
                    text = expectedMorseStr,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Live Feed Tapped Sequence
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = "YOUR INPUT:", style = MaterialTheme.typography.labelSmall)
            Text(
                text = tappedSequence.ifEmpty { "Waiting for first tap..." },
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black
                ),
                color = if (tappedSequence.isEmpty()) Color.Gray else BrandOrange,
                textAlign = TextAlign.Center
            )

            AnimatedVisibility(visible = interpretedWord.isNotEmpty()) {
                Text(
                    text = "Translates to: $interpretedWord",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = BrandGreen
                )
            }
        }

        // telegraph / buzzer pad tapper
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(GoldYellow, GoldYellow.copy(alpha = 0.8f))
                    )
                )
                .border(6.dp, Color.White, CircleShape)
                .pointerInteropFilter { event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            pressStartTime = System.currentTimeMillis()
                            keyIsDown = true
                        }

                        MotionEvent.ACTION_UP -> {
                            keyIsDown = false
                            val duration = System.currentTimeMillis() - pressStartTime
                            // 250ms separates Dot and Dash
                            registerMark(isDash = duration >= 250)
                        }
                    }
                    true
                }
                .testTag("morse_telegraph_key"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = "Buzzer",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = "TAP KEY",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
                )
            }
        }

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Space button (separates characters)
            OutlinedButton(
                onClick = { tappedSequence += " " },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.SpaceBar, contentDescription = "Space")
                Spacer(modifier = Modifier.width(4.dp))
                Text("SPACE", fontWeight = FontWeight.Bold)
            }

            // Clear button
            OutlinedButton(
                onClick = {
                    tappedSequence = ""
                    interpretedWord = ""
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GemPink),
                border = BorderStroke(1.dp, GemPink),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear")
                Spacer(modifier = Modifier.width(4.dp))
                Text("CLEAR", fontWeight = FontWeight.Bold)
            }

            // Submit Button
            Button(
                onClick = { submitTappedWord() },
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("morse_submit_button")
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Submit")
                Spacer(modifier = Modifier.width(4.dp))
                Text("SUBMIT", fontWeight = FontWeight.Bold)
            }
        }

        // Save progress banner
        if (sessionScore > 0) {
            Card(
                colors = CardDefaults.cardColors(containerColor = BrandGreen.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.recordMorseStreakAndXP() }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "You earned streak XP! Tap to lock in and save.",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = BrandGreenDark
                    )
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = "Save Progress", tint = BrandGreenDark)
                }
            }
        }
    }
}

@Composable
fun MorseTranslatorTab(morseMap: Map<Char, String>) {
    var textInput by remember { mutableStateOf("") }
    var translatedOutput by remember { mutableStateOf("") }

    // Translate English Text to Morse Code
    LaunchedEffect(textInput) {
        translatedOutput = textInput.uppercase(Locale.getDefault()).map { char ->
            if (char == ' ') "/" else (morseMap[char] ?: "?")
        }.joinToString(" ")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "English to Morse Code Translator",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            placeholder = { Text("Type English text here (e.g., 'Arduino Rules')...") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandTeal)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "MORSE CODE TRANSLATION:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = translatedOutput.ifEmpty { "Waiting for input..." },
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black
                    ),
                    color = if (translatedOutput.isEmpty()) Color.Gray else BrandOrange
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Basic morse space standard tips
        Text(
            text = "💡 Morse spacing tips:\n• Dots (·) are short presses, Dashes (-) are long presses.\n• Letters are separated by a space.\n• Words are separated by a forward slash (/).",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun MorseCheatSheetTab(morseMap: Map<Char, String>) {
    val itemsList = remember { morseMap.toList().sortedBy { it.first } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "International Morse Code Alphabet",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(itemsList) { (char, morse) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = char.toString(),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = BrandTeal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = morse,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = BrandOrange
                        )
                    }
                }
            }
        }
    }
}
