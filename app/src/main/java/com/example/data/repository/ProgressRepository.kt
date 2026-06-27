package com.example.data.repository

import com.example.data.database.CompletedLesson
import com.example.data.database.ProgressDao
import com.example.data.database.UserStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar

class ProgressRepository(private val progressDao: ProgressDao) {

    val userStats: Flow<UserStats?> = progressDao.getUserStats()
    val completedLessons: Flow<List<CompletedLesson>> = progressDao.getCompletedLessons()

    suspend fun getOrCreateStats(): UserStats {
        val current = progressDao.getUserStats().firstOrNull()
        if (current == null) {
            val defaultStats = UserStats()
            progressDao.insertUserStats(defaultStats)
            return defaultStats
        }
        return current
    }

    suspend fun completeLesson(lessonId: String, score: Int, xpEarned: Int) {
        // Mark lesson as completed
        val lesson = CompletedLesson(lessonId = lessonId, score = score)
        progressDao.insertCompletedLesson(lesson)

        // Update User stats
        val stats = getOrCreateStats()
        val newXp = stats.xp + xpEarned
        
        // Calculate Level (e.g., every 100 XP is a level)
        val newLevel = (newXp / 100) + 1
        
        // Streak calculation
        val currentTime = System.currentTimeMillis()
        val newStreak = calculateStreak(stats.lastActiveTime, currentTime, stats.streak)
        
        // Add Gems (e.g., 5 gems for completing a lesson)
        val newGems = stats.gems + 10

        val updatedStats = stats.copy(
            xp = newXp,
            level = if (newLevel > stats.level) newLevel else stats.level,
            streak = newStreak,
            lastActiveTime = currentTime,
            gems = newGems
        )
        progressDao.insertUserStats(updatedStats)
    }

    suspend fun updateMorseHighScore(score: Int) {
        val stats = getOrCreateStats()
        if (score > stats.morseHighScore) {
            val updatedStats = stats.copy(morseHighScore = score)
            progressDao.insertUserStats(updatedStats)
        }
    }

    suspend fun useGems(amount: Int): Boolean {
        val stats = getOrCreateStats()
        return if (stats.gems >= amount) {
            val updatedStats = stats.copy(gems = stats.gems - amount)
            progressDao.insertUserStats(updatedStats)
            true
        } else {
            false
        }
    }

    suspend fun resetProgress() {
        progressDao.clearCompletedLessons()
        progressDao.insertUserStats(UserStats(id = 1, xp = 0, streak = 0, gems = 50, level = 1, lastActiveTime = 0L, morseHighScore = 0))
    }

    private fun calculateStreak(lastActiveTime: Long, currentTime: Long, currentStreak: Int): Int {
        if (lastActiveTime == 0L) return 1

        val lastCal = Calendar.getInstance().apply { timeInMillis = lastActiveTime }
        val currCal = Calendar.getInstance().apply { timeInMillis = currentTime }

        // Strip time
        lastCal.set(Calendar.HOUR_OF_DAY, 0)
        lastCal.set(Calendar.MINUTE, 0)
        lastCal.set(Calendar.SECOND, 0)
        lastCal.set(Calendar.MILLISECOND, 0)

        currCal.set(Calendar.HOUR_OF_DAY, 0)
        currCal.set(Calendar.MINUTE, 0)
        currCal.set(Calendar.SECOND, 0)
        currCal.set(Calendar.MILLISECOND, 0)

        val diffDays = ((currCal.timeInMillis - lastCal.timeInMillis) / (24 * 60 * 60 * 1000)).toInt()

        return when (diffDays) {
            0 -> currentStreak // Same day, keep streak
            1 -> currentStreak + 1 // Consecutive day, increment
            else -> 1 // Gap of more than 1 day, reset to 1
        }
    }
}
