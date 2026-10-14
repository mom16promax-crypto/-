package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AnnouncementEntity
import com.example.data.model.ComplaintEntity
import com.example.data.model.SurveyEntity
import com.example.data.model.SurveyQuestionEntity
import com.example.data.model.SurveyResponseEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GuidanceDao {

    // --- Users ---
    @Query("SELECT * FROM users WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = 'ADMIN' AND username = :username LIMIT 1")
    suspend fun getAdmin(username: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    // --- Surveys ---
    @Query("SELECT * FROM surveys ORDER BY createdAt DESC")
    fun getAllSurveys(): Flow<List<SurveyEntity>>

    @Query("SELECT * FROM surveys WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveSurveys(): Flow<List<SurveyEntity>>

    @Query("SELECT * FROM surveys WHERE id = :id LIMIT 1")
    suspend fun getSurveyById(id: Long): SurveyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurvey(survey: SurveyEntity): Long

    @Update
    suspend fun updateSurvey(survey: SurveyEntity)

    @Query("DELETE FROM surveys WHERE id = :id")
    suspend fun deleteSurvey(id: Long)

    // --- Survey Questions ---
    @Query("SELECT * FROM survey_questions WHERE surveyId = :surveyId ORDER BY questionOrder ASC")
    fun getQuestionsForSurvey(surveyId: Long): Flow<List<SurveyQuestionEntity>>

    @Query("SELECT * FROM survey_questions WHERE surveyId = :surveyId ORDER BY questionOrder ASC")
    suspend fun getQuestionsForSurveySync(surveyId: Long): List<SurveyQuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: SurveyQuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<SurveyQuestionEntity>)

    @Query("DELETE FROM survey_questions WHERE surveyId = :surveyId")
    suspend fun deleteQuestionsForSurvey(surveyId: Long)

    // --- Survey Responses ---
    @Query("SELECT * FROM survey_responses ORDER BY submittedAt DESC")
    fun getAllResponses(): Flow<List<SurveyResponseEntity>>

    @Query("SELECT * FROM survey_responses WHERE surveyId = :surveyId ORDER BY submittedAt DESC")
    fun getResponsesForSurvey(surveyId: Long): Flow<List<SurveyResponseEntity>>

    @Query("SELECT * FROM survey_responses WHERE surveyId = :surveyId ORDER BY submittedAt DESC")
    suspend fun getResponsesForSurveySync(surveyId: Long): List<SurveyResponseEntity>

    @Query("SELECT * FROM survey_responses WHERE surveyId = :surveyId AND parentPhone = :parentPhone LIMIT 1")
    suspend fun getResponseByParentAndSurvey(surveyId: Long, parentPhone: String): SurveyResponseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponse(response: SurveyResponseEntity): Long

    // --- Announcements ---
    @Query("SELECT * FROM announcements ORDER BY isUrgent DESC, createdAt DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity): Long

    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun deleteAnnouncement(id: Long)

    // --- Complaints & Guidance Consultations ---
    @Query("SELECT * FROM complaints ORDER BY createdAt DESC")
    fun getAllComplaints(): Flow<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints WHERE parentPhone = :parentPhone ORDER BY createdAt DESC")
    fun getComplaintsForParent(parentPhone: String): Flow<List<ComplaintEntity>>

    @Query("SELECT * FROM complaints WHERE id = :id LIMIT 1")
    suspend fun getComplaintById(id: Long): ComplaintEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaint(complaint: ComplaintEntity): Long

    @Update
    suspend fun updateComplaint(complaint: ComplaintEntity)

    @Query("DELETE FROM complaints WHERE id = :id")
    suspend fun deleteComplaint(id: Long)
}
