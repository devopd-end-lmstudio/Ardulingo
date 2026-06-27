package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.LessonScreen
import com.example.ui.screens.MorseScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ProgressViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ProgressViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val activeLesson by viewModel.activeLesson.collectAsStateWithLifecycle()

                if (activeLesson != null) {
                    LessonScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.cancelLesson() }
                    )
                } else {
                    MainAppLayout(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MainAppLayout(viewModel: ProgressViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("app_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(imageVector = Icons.Default.MenuBook, contentDescription = "Learn") },
                    label = { Text("Learn") },
                    modifier = Modifier.testTag("nav_tab_learn")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(imageVector = Icons.Default.Sensors, contentDescription = "Morse Key") },
                    label = { Text("Morse Key") },
                    modifier = Modifier.testTag("nav_tab_morse")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(imageVector = Icons.Default.DeveloperBoard, contentDescription = "Sandbox") },
                    label = { Text("Sandbox") },
                    modifier = Modifier.testTag("nav_tab_profile")
                )
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = { fadeIn().togetherWith(fadeOut()) },
            label = "screen_transitions",
            modifier = Modifier.padding(innerPadding)
        ) { tab ->
            when (tab) {
                0 -> LearnScreen(viewModel = viewModel)
                1 -> MorseScreen(viewModel = viewModel)
                2 -> ProfileScreen(viewModel = viewModel)
            }
        }
    }
}
