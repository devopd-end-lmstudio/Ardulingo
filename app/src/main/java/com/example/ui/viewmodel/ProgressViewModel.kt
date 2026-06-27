package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.database.CompletedLesson
import com.example.data.database.UserStats
import com.example.data.model.Lesson
import com.example.data.model.Question
import com.example.data.model.QuestionType
import com.example.data.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProgressViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProgressRepository
    
    val userStats: StateFlow<UserStats?>
    val completedLessons: StateFlow<List<CompletedLesson>>

    // Active lesson play states
    private val _activeLesson = MutableStateFlow<Lesson?>(null)
    val activeLesson: StateFlow<Lesson?> = _activeLesson.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedOptionIndex = MutableStateFlow<Int?>(null) // For MULTIPLE_CHOICE or CODE_FIX
    val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex.asStateFlow()

    private val _typedAnswer = MutableStateFlow("") // For FILL_IN_BLANK
    val typedAnswer: StateFlow<String> = _typedAnswer.asStateFlow()

    private val _tappedBlocks = MutableStateFlow<List<String>>(emptyList()) // For TAP_BLOCKS
    val tappedBlocks: StateFlow<List<String>> = _tappedBlocks.asStateFlow()

    private val _isAnswerChecked = MutableStateFlow(false)
    val isAnswerChecked: StateFlow<Boolean> = _isAnswerChecked.asStateFlow()

    private val _isAnswerCorrect = MutableStateFlow(false)
    val isAnswerCorrect: StateFlow<Boolean> = _isAnswerCorrect.asStateFlow()

    private val _scoreCount = MutableStateFlow(0)
    val scoreCount: StateFlow<Int> = _scoreCount.asStateFlow()

    private val _showCompletionScreen = MutableStateFlow(false)
    val showCompletionScreen: StateFlow<Boolean> = _showCompletionScreen.asStateFlow()

    // Morse trainer tab state
    private val _morseSessionScore = MutableStateFlow(0)
    val morseSessionScore: StateFlow<Int> = _morseSessionScore.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ProgressRepository(database.progressDao())
        
        userStats = repository.userStats.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserStats()
        )
        
        completedLessons = repository.completedLessons.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Pre-create user stats if not exist
        viewModelScope.launch {
            repository.getOrCreateStats()
        }
    }

    fun startLesson(lesson: Lesson) {
        _activeLesson.value = lesson
        _currentQuestionIndex.value = 0
        _selectedOptionIndex.value = null
        _typedAnswer.value = ""
        _tappedBlocks.value = emptyList()
        _isAnswerChecked.value = false
        _isAnswerCorrect.value = false
        _scoreCount.value = 0
        _showCompletionScreen.value = false
    }

    fun selectOption(index: Int) {
        if (_isAnswerChecked.value) return
        _selectedOptionIndex.value = index
    }

    fun updateTypedAnswer(answer: String) {
        if (_isAnswerChecked.value) return
        _typedAnswer.value = answer
    }

    fun toggleTapBlock(block: String) {
        if (_isAnswerChecked.value) return
        val current = _tappedBlocks.value.toMutableList()
        if (current.contains(block)) {
            current.remove(block)
        } else {
            current.add(block)
        }
        _tappedBlocks.value = current
    }

    fun removeTappedBlockAt(index: Int) {
        if (_isAnswerChecked.value) return
        val current = _tappedBlocks.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _tappedBlocks.value = current
        }
    }

    fun checkAnswer() {
        val lesson = _activeLesson.value ?: return
        val question = lesson.questions.getOrNull(_currentQuestionIndex.value) ?: return

        var correct = false
        when (question.type) {
            QuestionType.MULTIPLE_CHOICE -> {
                correct = _selectedOptionIndex.value?.toString() == question.correctAnswer
            }
            QuestionType.FILL_IN_BLANK -> {
                correct = _typedAnswer.value.trim().equals(question.correctAnswer.trim(), ignoreCase = true)
            }
            QuestionType.CODE_FIX -> {
                correct = _selectedOptionIndex.value?.toString() == question.correctAnswer
            }
            QuestionType.TAP_BLOCKS -> {
                // Check if items tapped match the correct order
                // Normalize by stripping comments/whitespace if needed, but our correctOrder list contains exact strings
                val cleanTapped = _tappedBlocks.value.map { it.trim() }
                val cleanCorrect = question.correctOrder.map { it.trim() }
                correct = cleanTapped == cleanCorrect
            }
        }

        _isAnswerCorrect.value = correct
        _isAnswerChecked.value = true
        if (correct) {
            _scoreCount.value += 1
        }
    }

    fun nextQuestion() {
        val lesson = _activeLesson.value ?: return
        val nextIndex = _currentQuestionIndex.value + 1

        if (nextIndex < lesson.questions.size) {
            _currentQuestionIndex.value = nextIndex
            _selectedOptionIndex.value = null
            _typedAnswer.value = ""
            _tappedBlocks.value = emptyList()
            _isAnswerChecked.value = false
            _isAnswerCorrect.value = false
        } else {
            // End of lesson! Submit progress
            viewModelScope.launch {
                val percentage = (_scoreCount.value.toFloat() / lesson.questions.size.toFloat() * 100).toInt()
                val xpEarned = if (percentage >= 80) lesson.xpReward else (lesson.xpReward / 2)
                repository.completeLesson(lesson.id, percentage, xpEarned)
                _showCompletionScreen.value = true
            }
        }
    }

    fun finishLesson() {
        _activeLesson.value = null
        _showCompletionScreen.value = false
    }

    fun cancelLesson() {
        _activeLesson.value = null
    }

    // Morse Training State Actions
    fun earnMorsePoints() {
        _morseSessionScore.value += 5
        viewModelScope.launch {
            repository.updateMorseHighScore(_morseSessionScore.value)
        }
    }

    fun recordMorseStreakAndXP() {
        if (_morseSessionScore.value > 0) {
            viewModelScope.launch {
                val stats = repository.getOrCreateStats()
                // Every 10 points in Morse Trainer gets 5 XP
                val xpEarned = (_morseSessionScore.value / 10) * 5
                if (xpEarned > 0) {
                    repository.completeLesson("morse_trainer_session", _morseSessionScore.value, xpEarned)
                }
                _morseSessionScore.value = 0
            }
        }
    }

    fun buyHint(cost: Int, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            val success = repository.useGems(cost)
            if (success) {
                onSuccess()
            } else {
                onFailure()
            }
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            repository.resetProgress()
        }
    }
}
