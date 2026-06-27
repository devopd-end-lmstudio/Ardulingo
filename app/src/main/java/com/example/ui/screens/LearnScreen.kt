package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.database.CompletedLesson
import com.example.data.database.UserStats
import com.example.data.model.Curriculum
import com.example.data.model.Lesson
import com.example.ui.theme.*
import com.example.ui.viewmodel.ProgressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    viewModel: ProgressViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.userStats.collectAsStateWithLifecycle()
    val completedList by viewModel.completedLessons.collectAsStateWithLifecycle()

    val modules = listOf(
        Curriculum.MODULE_ARDUINO to BrandTeal,
        Curriculum.MODULE_ESP32 to BrandOrange,
        Curriculum.MODULE_MORSE to BrandGreen
    )

    Scaffold(
        topBar = {
            LearnTopHeader(stats = stats ?: UserStats())
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Welcome card
            item {
                WelcomeCard(stats = stats ?: UserStats())
            }

            // Staggered modules list
            modules.forEach { (moduleName, moduleColor) ->
                val moduleLessons = Curriculum.getLessonsByModule(moduleName)
                
                item {
                    ModuleHeader(title = moduleName, color = moduleColor)
                }

                itemsIndexed(moduleLessons) { index, lesson ->
                    val isCompleted = completedList.any { it.lessonId == lesson.id }
                    
                    // Staggering effect (offset left, center, right)
                    val offsetDp = when (index % 3) {
                        0 -> 0.dp
                        1 -> 40.dp
                        else -> (-40.dp)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LessonNode(
                            lesson = lesson,
                            isCompleted = isCompleted,
                            themeColor = moduleColor,
                            modifier = Modifier
                                .offset(x = offsetDp)
                                .testTag("lesson_node_${lesson.id}"),
                            onClick = { viewModel.startLesson(lesson) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun LearnTopHeader(stats: UserStats) {
    Surface(
        tonalElevation = 6.dp,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Streak
            HeaderStatItem(
                icon = Icons.Default.LocalFireDepartment,
                value = "${stats.streak}d",
                color = GoldYellow,
                contentDescription = "Streak Count"
            )

            // XP
            HeaderStatItem(
                icon = Icons.Default.OfflineBolt,
                value = "${stats.xp} XP",
                color = InfoBlue,
                contentDescription = "XP Count"
            )

            // Level
            HeaderStatItem(
                icon = Icons.Default.Stars,
                value = "Lvl ${stats.level}",
                color = DarkCozyPurple,
                contentDescription = "Level"
            )

            // Gems
            HeaderStatItem(
                icon = Icons.Default.Favorite, // Ruby/Gem heart representation
                value = "${stats.gems}",
                color = GemPink,
                contentDescription = "Gems"
            )
        }
    }
}

@Composable
fun HeaderStatItem(
    icon: ImageVector,
    value: String,
    color: Color,
    contentDescription: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
    }
}

@Composable
fun WelcomeCard(stats: UserStats) {
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                // Friendly mascot image
                Image(
                    painter = painterResource(id = R.drawable.img_mascot),
                    contentDescription = "Mascot Owl",
                    modifier = Modifier.size(72.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Welcome to ArduLingo!",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Unlock your superpowers. Tap on lessons to master Arduino, ESP32, and Morse Code!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ModuleHeader(title: String, color: Color) {
    val textColor = if (color == BrandOrange) Color(0xFF21005D) else Color.White
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = color),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = textColor,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun LessonNode(
    lesson: Lesson,
    isCompleted: Boolean,
    themeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTooltip by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Staggered node representation
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = if (isCompleted) {
                            listOf(BrandGreen, BrandGreenDark)
                        } else {
                            listOf(themeColor, themeColor.copy(alpha = 0.8f))
                        }
                    )
                )
                .border(
                    width = 4.dp,
                    color = if (isCompleted) BrandGreenDark.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.8f),
                    shape = CircleShape
                )
                .clickable(onClick = {
                    showTooltip = !showTooltip
                    onClick()
                }),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isCompleted) Icons.Default.Check else Icons.Default.PlayArrow,
                contentDescription = if (isCompleted) "Completed" else "Start Lesson",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            modifier = Modifier
                .width(180.dp)
                .clickable { onClick() }
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Text(
                    text = lesson.difficulty,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = when (lesson.difficulty) {
                            "Beginner" -> BrandTeal
                            "Intermediate" -> Color(0xFF8F6C00) // Dark gold/mustard for intermediate contrast
                            else -> GemPink
                        }
                    )
                )
            }
        }
    }
}
