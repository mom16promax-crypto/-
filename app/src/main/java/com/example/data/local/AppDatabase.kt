package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AnnouncementEntity
import com.example.data.model.ComplaintEntity
import com.example.data.model.SurveyEntity
import com.example.data.model.SurveyQuestionEntity
import com.example.data.model.SurveyResponseEntity
import com.example.data.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        SurveyEntity::class,
        SurveyQuestionEntity::class,
        SurveyResponseEntity::class,
        AnnouncementEntity::class,
        ComplaintEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun guidanceDao(): GuidanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "guidance_counseling_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
