package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "ADMIN" or "PARENT"
    val phoneNumber: String = "",
    val username: String = "",
    val password: String = "",
    val parentName: String = "",
    val studentName: String = "",
    val grade: String = "",
    val isNewAccount: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "surveys")
data class SurveyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val targetAudience: String = "All Parents", // "All Parents", "Newly Registered Accounts", "High School", "Middle School"
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "survey_questions")
data class SurveyQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val surveyId: Long,
    val questionOrder: Int = 0,
    val questionText: String,
    val questionType: String, // "RATING", "MULTIPLE_CHOICE", "YES_NO", "TEXT"
    val optionsString: String = "" // Delimited by "||"
)

@Entity(tableName = "survey_responses")
data class SurveyResponseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val surveyId: Long,
    val parentPhone: String,
    val parentName: String,
    val studentName: String,
    val grade: String,
    val answersJson: String, // JSON or formatted key-value of responses
    val submittedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val targetAudience: String, // "All Parents", "Newly Registered Accounts", "High School", "Middle School"
    val category: String, // "Urgent Alert", "Counseling Session", "Workshop", "Parent Advisory", "Academic Notice"
    val isUrgent: Boolean = false,
    val authorName: String = "Mr. Jaafar Mahdi",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "complaints")
data class ComplaintEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val parentPhone: String,
    val parentName: String,
    val studentName: String,
    val grade: String,
    val category: String, // "Academic Support", "Behavioral & Conduct", "Psychological & Stress", "Peer Relations & Bullying", "Attendance & Health", "General Inquiries"
    val urgency: String, // "Urgent", "Important", "Normal"
    val description: String,
    val requestCallback: Boolean = false,
    val callbackNumber: String = "",
    val status: String = "Pending Review", // "Pending Review", "In Review", "In Progress", "Resolved"
    val counselorResponse: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val respondedAt: Long = 0L
)
