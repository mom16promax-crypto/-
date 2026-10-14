package com.example.data.repository

import com.example.data.local.GuidanceDao
import com.example.data.model.AnnouncementEntity
import com.example.data.model.ComplaintEntity
import com.example.data.model.SurveyEntity
import com.example.data.model.SurveyQuestionEntity
import com.example.data.model.SurveyResponseEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GuidanceRepository(private val dao: GuidanceDao) {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    // --- Users ---
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()

    suspend fun getParentByPhone(phone: String): UserEntity? = withContext(Dispatchers.IO) {
        dao.getUserByPhone(phone.trim())
    }

    suspend fun registerOrUpdateParent(
        phone: String,
        name: String,
        studentName: String,
        grade: String
    ): UserEntity = withContext(Dispatchers.IO) {
        val existing = dao.getUserByPhone(phone.trim())
        if (existing != null) {
            val updated = existing.copy(
                parentName = name.ifBlank { existing.parentName },
                studentName = studentName.ifBlank { existing.studentName },
                grade = grade.ifBlank { existing.grade }
            )
            dao.insertUser(updated)
            updated
        } else {
            val newUser = UserEntity(
                role = "PARENT",
                phoneNumber = phone.trim(),
                parentName = name.trim(),
                studentName = studentName.trim(),
                grade = grade.trim(),
                isNewAccount = true
            )
            val id = dao.insertUser(newUser)
            newUser.copy(id = id)
        }
    }

    suspend fun verifyAdmin(username: String, pass: String): UserEntity? = withContext(Dispatchers.IO) {
        val admin = dao.getAdmin(username.trim())
        if (admin != null && admin.password == pass.trim()) {
            admin
        } else null
    }

    // --- Surveys ---
    val allSurveys: Flow<List<SurveyEntity>> = dao.getAllSurveys()
    val activeSurveys: Flow<List<SurveyEntity>> = dao.getActiveSurveys()

    suspend fun getSurveyById(id: Long): SurveyEntity? = withContext(Dispatchers.IO) {
        dao.getSurveyById(id)
    }

    fun getQuestionsForSurvey(surveyId: Long): Flow<List<SurveyQuestionEntity>> =
        dao.getQuestionsForSurvey(surveyId)

    suspend fun getQuestionsForSurveySync(surveyId: Long): List<SurveyQuestionEntity> =
        withContext(Dispatchers.IO) {
            dao.getQuestionsForSurveySync(surveyId)
        }

    suspend fun createSurveyWithQuestions(
        survey: SurveyEntity,
        questions: List<SurveyQuestionEntity>
    ): Long = withContext(Dispatchers.IO) {
        val surveyId = dao.insertSurvey(survey)
        val questionsWithId = questions.mapIndexed { index, q ->
            q.copy(surveyId = surveyId, questionOrder = index + 1)
        }
        dao.insertQuestions(questionsWithId)
        surveyId
    }

    suspend fun toggleSurveyActive(survey: SurveyEntity) = withContext(Dispatchers.IO) {
        dao.updateSurvey(survey.copy(isActive = !survey.isActive))
    }

    suspend fun deleteSurvey(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteQuestionsForSurvey(id)
        dao.deleteSurvey(id)
    }

    // --- Survey Responses ---
    val allResponses: Flow<List<SurveyResponseEntity>> = dao.getAllResponses()

    fun getResponsesForSurvey(surveyId: Long): Flow<List<SurveyResponseEntity>> =
        dao.getResponsesForSurvey(surveyId)

    suspend fun getResponsesForSurveySync(surveyId: Long): List<SurveyResponseEntity> =
        withContext(Dispatchers.IO) {
            dao.getResponsesForSurveySync(surveyId)
        }

    suspend fun hasParentCompletedSurvey(surveyId: Long, phone: String): Boolean =
        withContext(Dispatchers.IO) {
            dao.getResponseByParentAndSurvey(surveyId, phone) != null
        }

    suspend fun submitSurveyResponse(response: SurveyResponseEntity): Long =
        withContext(Dispatchers.IO) {
            dao.insertResponse(response)
        }

    // --- Announcements ---
    val allAnnouncements: Flow<List<AnnouncementEntity>> = dao.getAllAnnouncements()

    suspend fun createAnnouncement(announcement: AnnouncementEntity): Long =
        withContext(Dispatchers.IO) {
            dao.insertAnnouncement(announcement)
        }

    suspend fun deleteAnnouncement(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteAnnouncement(id)
    }

    // --- Complaints & Guidance Consultations ---
    val allComplaints: Flow<List<ComplaintEntity>> = dao.getAllComplaints()

    fun getComplaintsForParent(phone: String): Flow<List<ComplaintEntity>> =
        dao.getComplaintsForParent(phone)

    suspend fun submitComplaint(complaint: ComplaintEntity): Long =
        withContext(Dispatchers.IO) {
            dao.insertComplaint(complaint)
        }

    suspend fun updateComplaintResponse(
        id: Long,
        newStatus: String,
        response: String
    ) = withContext(Dispatchers.IO) {
        val current = dao.getComplaintById(id)
        if (current != null) {
            dao.updateComplaint(
                current.copy(
                    status = newStatus,
                    counselorResponse = response,
                    respondedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun deleteComplaint(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteComplaint(id)
    }

    // --- Pre-population of Starter Data ---
    private suspend fun seedInitialDataIfNeeded() {
        val admin = dao.getAdmin("admin")
        if (admin == null) {
            dao.insertUser(
                UserEntity(
                    role = "ADMIN",
                    username = "admin",
                    password = "admin123",
                    parentName = "Mr. Jaafar Mahdi",
                    studentName = "Lead School Counselor",
                    grade = "Guidance Dept Head"
                )
            )
        }

        val demoParent = dao.getUserByPhone("0501234567")
        if (demoParent == null) {
            dao.insertUser(
                UserEntity(
                    role = "PARENT",
                    phoneNumber = "0501234567",
                    parentName = "Sarah Al-Ahmad",
                    studentName = "Rami Al-Ahmad",
                    grade = "Grade 10 - Section B",
                    isNewAccount = false
                )
            )
        }

        val surveys = dao.getActiveSurveys()
        // Check if surveys already seeded
        val existingSurvey = dao.getSurveyById(1)
        if (existingSurvey == null) {
            // Seed Survey 1
            val s1 = SurveyEntity(
                title = "School Climate & Guidance Satisfaction 2026",
                description = "Evaluation of academic counseling support, emotional safety, and student guidance initiatives.",
                category = "Academic & School Climate",
                targetAudience = "All Parents",
                isActive = true
            )
            val s1Id = dao.insertSurvey(s1)
            dao.insertQuestions(
                listOf(
                    SurveyQuestionEntity(
                        surveyId = s1Id,
                        questionOrder = 1,
                        questionText = "How would you rate the school guidance counseling support this academic term?",
                        questionType = "RATING",
                        optionsString = "5 Stars"
                    ),
                    SurveyQuestionEntity(
                        surveyId = s1Id,
                        questionOrder = 2,
                        questionText = "Does your child feel safe, supported, and motivated in our school environment?",
                        questionType = "YES_NO",
                        optionsString = "Yes||No"
                    ),
                    SurveyQuestionEntity(
                        surveyId = s1Id,
                        questionOrder = 3,
                        questionText = "Which area would benefit most from counselor-led parent workshops?",
                        questionType = "MULTIPLE_CHOICE",
                        optionsString = "Study Habits & Time Management||Exam Stress & Mental Well-being||Peer Communication & Social Dynamics||College & Career Exploration"
                    ),
                    SurveyQuestionEntity(
                        surveyId = s1Id,
                        questionOrder = 4,
                        questionText = "Any suggestions or personal notes for Counselor Mr. Jaafar Mahdi?",
                        questionType = "TEXT",
                        optionsString = ""
                    )
                )
            )

            // Seed sample responses for Survey 1
            dao.insertResponse(
                SurveyResponseEntity(
                    surveyId = s1Id,
                    parentPhone = "0501234567",
                    parentName = "Sarah Al-Ahmad",
                    studentName = "Rami Al-Ahmad",
                    grade = "Grade 10 - Section B",
                    answersJson = "{\"1\":\"5\",\"2\":\"Yes\",\"3\":\"Study Habits & Time Management\",\"4\":\"Very grateful for Mr. Jaafar's continuous guidance with Rami.\"}",
                    submittedAt = System.currentTimeMillis() - 86400000L * 2
                )
            )
            dao.insertResponse(
                SurveyResponseEntity(
                    surveyId = s1Id,
                    parentPhone = "0559876543",
                    parentName = "Hassan Al-Otaibi",
                    studentName = "Noura Al-Otaibi",
                    grade = "Grade 9 - Section A",
                    answersJson = "{\"1\":\"4\",\"2\":\"Yes\",\"3\":\"Exam Stress & Mental Well-being\",\"4\":\"Looking forward to more exam prep workshops.\"}",
                    submittedAt = System.currentTimeMillis() - 86400000L * 1
                )
            )

            // Seed Survey 2
            val s2 = SurveyEntity(
                title = "Parental Consultation Preferences",
                description = "Help the counseling office organize convenient meeting hours and specialized communication channels.",
                category = "Office Administration",
                targetAudience = "All Parents",
                isActive = true
            )
            val s2Id = dao.insertSurvey(s2)
            dao.insertQuestions(
                listOf(
                    SurveyQuestionEntity(
                        surveyId = s2Id,
                        questionOrder = 1,
                        questionText = "What is your preferred format for counselor consultations?",
                        questionType = "MULTIPLE_CHOICE",
                        optionsString = "In-Person School Office||Direct Phone Consultation||Virtual Video Meeting (Meet/Zoom)"
                    ),
                    SurveyQuestionEntity(
                        surveyId = s2Id,
                        questionOrder = 2,
                        questionText = "Are the current guidance office hours (8:00 AM - 2:00 PM) convenient for your schedule?",
                        questionType = "YES_NO",
                        optionsString = "Yes||No"
                    )
                )
            )

            // Seed sample announcements
            dao.insertAnnouncement(
                AnnouncementEntity(
                    title = "Mandatory Parent-Counselor Orientation",
                    content = "The annual Academic Guidance & Counseling briefing will be held this Wednesday at 10:00 AM in the main auditorium. Mr. Jaafar Mahdi will present graduation pathways, anti-bullying protocols, and support programs.",
                    targetAudience = "All Parents",
                    category = "Urgent Alert",
                    isUrgent = true,
                    authorName = "Mr. Jaafar Mahdi"
                )
            )
            dao.insertAnnouncement(
                AnnouncementEntity(
                    title = "Workshop: Overcoming Exam Anxiety & Procrastination",
                    content = "Dear parents, exams can create undue stress for teenagers. We invite all families to join our interactive session on positive reinforcement and quiet home study setups this Sunday at 5:00 PM.",
                    targetAudience = "All Parents",
                    category = "Workshop",
                    isUrgent = false,
                    authorName = "Mr. Jaafar Mahdi"
                )
            )
            dao.insertAnnouncement(
                AnnouncementEntity(
                    title = "Welcome New Families: Guidance Office Introduction",
                    content = "Welcome to our school family! The Guidance & Counseling office offers confidential consultations, academic mentorship, and behavioral coaching. Feel free to reach out directly via the Complaints & Inquiries section.",
                    targetAudience = "Newly Registered Accounts only",
                    category = "Parent Advisory",
                    isUrgent = false,
                    authorName = "Mr. Jaafar Mahdi"
                )
            )

            // Seed sample complaints
            dao.insertComplaint(
                ComplaintEntity(
                    parentPhone = "0501234567",
                    parentName = "Sarah Al-Ahmad",
                    studentName = "Rami Al-Ahmad",
                    grade = "Grade 10 - Section B",
                    category = "Academic Support",
                    urgency = "Important",
                    description = "Rami is feeling overwhelmed with his advanced chemistry course load and needs advice on study timetables.",
                    requestCallback = true,
                    callbackNumber = "0501234567",
                    status = "In Progress",
                    counselorResponse = "Dear Mrs. Sarah, I met with Rami today to map out an effective spaced-repetition study schedule. His chemistry instructor has also scheduled a review session. Best regards, Mr. Jaafar Mahdi.",
                    createdAt = System.currentTimeMillis() - 86400000L * 3,
                    respondedAt = System.currentTimeMillis() - 86400000L * 1
                )
            )
            dao.insertComplaint(
                ComplaintEntity(
                    parentPhone = "0559876543",
                    parentName = "Hassan Al-Otaibi",
                    studentName = "Noura Al-Otaibi",
                    grade = "Grade 9 - Section A",
                    category = "Peer Relations & Bullying",
                    urgency = "Urgent",
                    description = "Noura mentioned some exclusion and teasing during physical education periods. Requesting counselor intervention.",
                    requestCallback = true,
                    callbackNumber = "0559876543",
                    status = "Pending Review",
                    counselorResponse = "",
                    createdAt = System.currentTimeMillis() - 3600000L * 5,
                    respondedAt = 0L
                )
            )
        }
    }
}
