package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.Lesson
import com.example.data.model.Question
import com.example.data.model.QuestionType
import com.example.ui.theme.*
import com.example.ui.viewmodel.ProgressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    viewModel: ProgressViewModel,
    onBack: () -> Unit
) {
    val activeLesson by viewModel.activeLesson.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val selectedOptionIndex by viewModel.selectedOptionIndex.collectAsStateWithLifecycle()
    val typedAnswer by viewModel.typedAnswer.collectAsStateWithLifecycle()
    val tappedBlocks by viewModel.tappedBlocks.collectAsStateWithLifecycle()
    val isChecked by viewModel.isAnswerChecked.collectAsStateWithLifecycle()
    val isCorrect by viewModel.isAnswerCorrect.collectAsStateWithLifecycle()
    val showCompletion by viewModel.showCompletionScreen.collectAsStateWithLifecycle()
    val stats by viewModel.userStats.collectAsStateWithLifecycle()

    val lesson = activeLesson ?: return
    val question = lesson.questions.getOrNull(currentIndex) ?: return
    val totalQuestions = lesson.questions.size
    val progress = (currentIndex + 1).toFloat() / totalQuestions.toFloat()

    var showHintDialog by remember { mutableStateOf(false) }
    var hintRevealed by remember { mutableStateOf(false) }
    var hintMessage by remember { mutableStateOf("") }

    if (showCompletion) {
        LessonCompletionScreen(
            lesson = lesson,
            score = (viewModel.scoreCount.value.toFloat() / totalQuestions.toFloat() * 100).toInt(),
            onFinish = { viewModel.finishLesson() }
        )
    } else {
        Scaffold(
            topBar = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEADDFF))
                                .clickable { viewModel.cancelLesson() }
                                .testTag("lesson_close_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Lesson",
                                tint = Color(0xFF21005D),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = BrandTeal,
                            trackColor = Color(0xFFE7E0EC)
                        )

                        // Gems count
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFEADDFF))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Gems",
                                tint = GemPink,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "${stats?.gems ?: 0}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF21005D)
                            )
                        }
                    }
                }
            },
            bottomBar = {
                // Persistent Check / Continue bottom bar
                Surface(
                    tonalElevation = 8.dp,
                    color = when {
                        isChecked && isCorrect -> Color(0xFFE8DEF8)
                        isChecked && !isCorrect -> GemPink.copy(alpha = 0.12f)
                        else -> MaterialTheme.colorScheme.surface
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isChecked) {
                            // Check results details with Mascot feedback
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEADDFF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_mascot),
                                        contentDescription = "Mascot feedback",
                                        modifier = Modifier.size(56.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isCorrect) "Excellent Job!" else "Incorrect, keep studying!",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = if (isCorrect) Color(0xFF21005D) else GemPink
                                        )
                                    )
                                    Text(
                                        text = question.explanation,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.nextQuestion()
                                    hintRevealed = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCorrect) BrandTeal else GemPink
                                ),
                                shape = CircleShape,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("lesson_continue_button")
                            ) {
                                Text(
                                    text = "CONTINUE",
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.5.sp,
                                    fontSize = 16.sp
                                )
                            }
                        } else {
                            // Enable check button only when answered
                            val isAnswered = when (question.type) {
                                QuestionType.MULTIPLE_CHOICE -> selectedOptionIndex != null
                                QuestionType.FILL_IN_BLANK -> typedAnswer.isNotBlank()
                                QuestionType.CODE_FIX -> selectedOptionIndex != null
                                QuestionType.TAP_BLOCKS -> tappedBlocks.isNotEmpty()
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Hint Button (Costs 5 Gems)
                                OutlinedButton(
                                    onClick = {
                                        showHintDialog = true
                                    },
                                    border = BorderStroke(1.5.dp, BrandTeal),
                                    shape = CircleShape,
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandTeal),
                                    modifier = Modifier.height(52.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Lightbulb, contentDescription = "Hint", tint = BrandTeal)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "HINT (5)", fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.checkAnswer() },
                                    enabled = isAnswered,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BrandTeal
                                    ),
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .testTag("lesson_check_button")
                                ) {
                                    Text(
                                        text = "CHECK ANSWER",
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.5.sp,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tiny uppercase lavender tag header matching Sleek theme
                Text(
                    text = "${lesson.title.uppercase()} • ${question.type.name.replace("_", " ")}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = BrandTeal,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                // Big main prompt header
                Text(
                    text = question.instruction,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1D1B20),
                        lineHeight = 28.sp
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                val isCodePrompt = question.type == QuestionType.CODE_FIX || question.prompt.contains("void") || question.prompt.contains("{") || question.prompt.contains("digitalWrite")

                // Board Visualizer Component (Renders when dealing with physical IO like LED, pins, high/low, blink)
                val isHardwareQuestion = question.prompt.contains("Pin", ignoreCase = true) || question.prompt.contains("LED", ignoreCase = true) || question.prompt.contains("write", ignoreCase = true)
                if (isHardwareQuestion) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                        border = BorderStroke(1.dp, Color(0xFFE7E0EC)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Virtual micro-board mockup
                            Box(
                                modifier = Modifier
                                    .width(110.dp)
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF2D2D2D))
                                    .border(3.dp, Color(0xFF444444), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.Gray))
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.Gray))
                                    }

                                    // Interactive blinking/status LED representing GPIO pin action
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF8B0000))
                                                .border(2.dp, Color(0xFFFF5555), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFFF0000))
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "ESP32-S3\nDEV CORE",
                                            color = Color.White.copy(alpha = 0.5f),
                                            fontSize = 7.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .background(Color(0xFF333333))
                                    )
                                }
                            }

                            // Pin Tag
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8DEF8))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "GPIO PIN 02",
                                    color = Color(0xFF1D192B),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Code Display Container / Question Prompt Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCodePrompt) Color(0xFF1D1B20) else Color(0xFFF3EDF7)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (isCodePrompt) Color(0xFF49454F) else Color(0xFFE7E0EC))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = question.prompt,
                            style = if (isCodePrompt) {
                                MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    lineHeight = 20.sp
                                )
                            } else {
                                MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D1B20)
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // If hints are purchased
                        if (hintRevealed) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8DEF8)),
                                border = BorderStroke(1.dp, BrandTeal),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Lightbulb, contentDescription = "Hint Alert", tint = BrandTeal)
                                    Text(
                                        text = "Tip: ${question.explanation.take(60)}...",
                                        color = Color(0xFF21005D),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Interactive Inputs Area
                when (question.type) {
                    QuestionType.MULTIPLE_CHOICE, QuestionType.CODE_FIX -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            question.options.forEachIndexed { index, option ->
                                val isSelected = selectedOptionIndex == index
                                val borderStrokeColor = when {
                                    isChecked && question.correctAnswer == index.toString() -> BrandTeal
                                    isChecked && isSelected && !isCorrect -> GemPink
                                    isSelected -> BrandTeal
                                    else -> Color(0xFFE7E0EC)
                                }

                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) Color(0xFFE8DEF8) else Color.White
                                    ),
                                    border = BorderStroke(if (isSelected || isChecked) 2.dp else 1.5.dp, borderStrokeColor),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(enabled = !isChecked) { viewModel.selectOption(index) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelected) BrandTeal else Color(0xFFEADDFF)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${(index + 65).toChar()}", // A, B, C, D...
                                                color = if (isSelected) Color.White else Color(0xFF21005D),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Text(
                                            text = option,
                                            color = if (isSelected) Color(0xFF1D192B) else Color(0xFF49454F),
                                            style = if (question.type == QuestionType.CODE_FIX) {
                                                MaterialTheme.typography.bodyMedium.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            } else {
                                                MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    QuestionType.FILL_IN_BLANK -> {
                        OutlinedTextField(
                            value = typedAnswer,
                            onValueChange = { viewModel.updateTypedAnswer(it) },
                            enabled = !isChecked,
                            placeholder = { Text("Type missing code/concept here...") },
                            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 16.sp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandTeal,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("fill_blank_input")
                        )
                    }

                    QuestionType.TAP_BLOCKS -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Destination / Tapped Block Area
                            Text(
                                text = "Your Assembly Area (Tap to remove):",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .minHeight(72.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    tappedBlocks.forEachIndexed { i, block ->
                                        Card(
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(containerColor = BrandTeal),
                                            modifier = Modifier.clickable(enabled = !isChecked) {
                                                viewModel.removeTappedBlockAt(i)
                                            }
                                        ) {
                                            Text(
                                                text = block,
                                                color = Color.White,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Available Options Blocks
                            Text(
                                text = "Available Blocks:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                question.options.forEach { block ->
                                    val isUsed = tappedBlocks.contains(block)
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isUsed) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                                        ),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                        modifier = Modifier.clickable(enabled = !isChecked && !isUsed) {
                                            viewModel.toggleTapBlock(block)
                                        }
                                    ) {
                                        Text(
                                            text = block,
                                            color = if (isUsed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSurface,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Hint Dialog
    if (showHintDialog) {
        AlertDialog(
            onDismissRequest = { showHintDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Lightbulb, contentDescription = "Hint Icon", tint = BrandTeal)
                    Text(text = "Reveal Smart Tip?")
                }
            },
            text = {
                Text(text = "Spend 5 Gems to reveal a crucial tip for this lesson question. You currently have ${stats?.gems ?: 0} Gems.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.buyHint(
                            cost = 5,
                            onSuccess = {
                                hintRevealed = true
                                showHintDialog = false
                            },
                            onFailure = {
                                showHintDialog = false
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandTeal)
                ) {
                    Text("Buy for 5 Gems")
                }
            },
            dismissButton = {
                TextButton(onClick = { showHintDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

@Composable
fun LessonCompletionScreen(
    lesson: Lesson,
    score: Int,
    onFinish: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "LESSON COMPLETED!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = BrandTeal
                )
            )

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(BrandGreen.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_mascot),
                    contentDescription = "Cheerful Mascot",
                    modifier = Modifier.size(140.dp)
                )
            }

            Text(
                text = if (score >= 80) "Excellent Job! You are on fire!" else "Nice effort, keep learning!",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                textAlign = TextAlign.Center
            )

            // Result Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Score metric
                MetricCard(title = "Score", value = "$score%", color = BrandTeal)
                // XP gained
                MetricCard(
                    title = "XP Gained",
                    value = "+${if (score >= 80) lesson.xpReward else (lesson.xpReward / 2)}",
                    color = InfoBlue
                )
                // Gems gained
                MetricCard(title = "Gems", value = "+10", color = GemPink)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onFinish,
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("lesson_finish_button")
            ) {
                Text(
                    text = "CONTINUE",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = Modifier.width(96.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(2.dp, color)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = color
                )
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Simple FlowRow helper because standard flow row requires Accompanist in older layouts
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    // Custom layout for simple wrapping flow row
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
        }

        val layoutWidth = constraints.maxWidth
        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentRowWidth = 0

        placeables.forEach { placeable ->
            if (currentRowWidth + placeable.width > layoutWidth && currentRow.isNotEmpty()) {
                rows.add(currentRow)
                currentRow = mutableListOf()
                currentRowWidth = 0
            }
            currentRow.add(placeable)
            currentRowWidth += placeable.width + 16 // spacing
        }
        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
        }

        val totalHeight = rows.sumOf { row -> row.maxOf { it.height } } + (rows.size - 1) * 16
        layout(layoutWidth, totalHeight) {
            var yOffset = 0
            rows.forEach { row ->
                val rowHeight = row.maxOf { it.height }
                var xOffset = 0
                row.forEach { placeable ->
                    placeable.placeRelative(xOffset, yOffset)
                    xOffset += placeable.width + 16
                }
                yOffset += rowHeight + 16
            }
        }
    }
}

// Extension to allow minimum height on Box
fun Modifier.minHeight(min: androidx.compose.ui.unit.Dp) = this.defaultMinSize(minHeight = min)
