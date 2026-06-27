package com.example.data.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val xp: Int = 0,
    val streak: Int = 0,
    val gems: Int = 50,
    val level: Int = 1,
    val lastActiveTime: Long = 0L,
    val morseHighScore: Int = 0
)

@Entity(tableName = "completed_lessons")
data class CompletedLesson(
    @PrimaryKey val lessonId: String,
    val score: Int,
    val completedAt: Long = System.currentTimeMillis()
)

@Dao
interface ProgressDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStats?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(stats: UserStats)

    @Query("SELECT * FROM completed_lessons")
    fun getCompletedLessons(): Flow<List<CompletedLesson>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedLesson(lesson: CompletedLesson)

    @Query("DELETE FROM completed_lessons")
    suspend fun clearCompletedLessons()
}

@Database(entities = [UserStats::class, CompletedLesson::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ardulingo_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
