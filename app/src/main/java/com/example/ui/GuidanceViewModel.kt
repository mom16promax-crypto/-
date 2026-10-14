package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AnnouncementEntity
import com.example.data.model.ComplaintEntity
import com.example.data.model.SurveyEntity
import com.example.data.model.SurveyQuestionEntity
import com.example.data.model.SurveyResponseEntity
import com.example.data.model.UserEntity
import com.example.data.repository.GuidanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GuidanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GuidanceRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = GuidanceRepository(db.guidanceDao())
    }

    // Current active logged in user. Defaults to Demo Parent so users see app working immediately, with instant switcher
    private val _currentUser = MutableStateFlow<UserEntity?>(
        UserEntity(
            id = 2,
            role = "PARENT",
            phoneNumber = "0501234567",
            parentName = "Sarah Al-Ahmad",
            studentName = "Rami Al-Ahmad",
            grade = "Grade 10 - Section B",
            isNewAccount = false
        )
    )
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Navigation Tab
    private val _currentTab = MutableStateFlow("dashboard")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    fun selectTab(tab: String) {
        _currentTab.value = tab
    }

    // Language State (Defaults to Arabic as requested)
    private val _isArabic = MutableStateFlow(true)
    val isArabic: StateFlow<Boolean> = _isArabic.asStateFlow()

    fun toggleLanguage() {
        _isArabic.value = !_isArabic.value
    }

    fun setArabic(enable: Boolean) {
        _isArabic.value = enable
    }

    // State flows from repository
    val surveys: StateFlow<List<SurveyEntity>> = repository.allSurveys
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allResponses: StateFlow<List<SurveyResponseEntity>> = repository.allResponses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val announcements: StateFlow<List<AnnouncementEntity>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val complaints: StateFlow<List<ComplaintEntity>> = repository.allComplaints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Feedback toast/snack message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun showToast(msg: String) {
        _userMessage.value = msg
    }

    // Auth Actions
    fun loginAsParent(phone: String, name: String, studentName: String, grade: String) {
        viewModelScope.launch {
            if (phone.isBlank()) {
                _userMessage.value = "Please enter a valid phone number"
                return@launch
            }
            val user = repository.registerOrUpdateParent(phone, name, studentName, grade)
            _currentUser.value = user
            _userMessage.value = "Welcome, ${user.parentName.ifBlank { "Parent" }}!"
        }
    }

    fun loginAsAdmin(user: String, pass: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            val admin = repository.verifyAdmin(user, pass)
            if (admin != null) {
                _currentUser.value = admin
                _userMessage.value = "Signed in as Counselor / Admin"
                onSuccess()
            } else {
                val err = "Invalid username or password. (Default: admin / admin123)"
                _userMessage.value = err
                onError(err)
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _userMessage.value = "Signed out successfully"
    }

    fun quickSwitchToAdmin() {
        viewModelScope.launch {
            val admin = repository.verifyAdmin("admin", "admin123")
            if (admin != null) {
                _currentUser.value = admin
                _userMessage.value = "Switched to Admin: Mr. Jaafar Mahdi"
            }
        }
    }

    fun quickSwitchToParent() {
        viewModelScope.launch {
            val parent = repository.getParentByPhone("0501234567") ?: UserEntity(
                id = 2,
                role = "PARENT",
                phoneNumber = "0501234567",
                parentName = "Sarah Al-Ahmad",
                studentName = "Rami Al-Ahmad",
                grade = "Grade 10 - Section B",
                isNewAccount = false
            )
            _currentUser.value = parent
            _userMessage.value = "Switched to Parent: Sarah Al-Ahmad"
        }
    }

    // Surveys Actions
    fun getQuestionsForSurvey(surveyId: Long) = repository.getQuestionsForSurvey(surveyId)

    suspend fun getQuestionsForSurveySync(surveyId: Long) = repository.getQuestionsForSurveySync(surveyId)

    suspend fun getResponsesForSurveySync(surveyId: Long) = repository.getResponsesForSurveySync(surveyId)

    fun createSurvey(
        title: String,
        description: String,
        category: String,
        targetAudience: String,
        questions: List<SurveyQuestionEntity>
    ) {
        viewModelScope.launch {
            if (title.isBlank()) {
                _userMessage.value = "Survey title cannot be empty"
                return@launch
            }
            if (questions.isEmpty()) {
                _userMessage.value = "Please add at least one question"
                return@launch
            }
            val newSurvey = SurveyEntity(
                title = title.trim(),
                description = description.trim(),
                category = category,
                targetAudience = targetAudience,
                isActive = true
            )
            repository.createSurveyWithQuestions(newSurvey, questions)
            _userMessage.value = "Survey created successfully!"
        }
    }

    fun toggleSurveyStatus(survey: SurveyEntity) {
        viewModelScope.launch {
            repository.toggleSurveyActive(survey)
            _userMessage.value = if (survey.isActive) "Survey closed" else "Survey activated"
        }
    }

    fun deleteSurvey(id: Long) {
        viewModelScope.launch {
            repository.deleteSurvey(id)
            _userMessage.value = "Survey deleted"
        }
    }

    fun submitSurveyResponse(
        surveyId: Long,
        answers: Map<Long, String>,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user == null || user.role != "PARENT") {
                _userMessage.value = "Only parents can submit responses"
                return@launch
            }
            val json = JSONObject()
            answers.forEach { (qId, ans) ->
                json.put(qId.toString(), ans)
            }
            val response = SurveyResponseEntity(
                surveyId = surveyId,
                parentPhone = user.phoneNumber,
                parentName = user.parentName.ifBlank { "Parent" },
                studentName = user.studentName.ifBlank { "Student" },
                grade = user.grade.ifBlank { "General" },
                answersJson = json.toString(),
                submittedAt = System.currentTimeMillis()
            )
            repository.submitSurveyResponse(response)
            _userMessage.value = "Thank you! Your survey response was recorded."
            onSuccess()
        }
    }

    // Announcements Actions
    fun createAnnouncement(
        title: String,
        content: String,
        targetAudience: String,
        category: String,
        isUrgent: Boolean
    ) {
        viewModelScope.launch {
            if (title.isBlank() || content.isBlank()) {
                _userMessage.value = "Title and content are required"
                return@launch
            }
            val item = AnnouncementEntity(
                title = title.trim(),
                content = content.trim(),
                targetAudience = targetAudience,
                category = category,
                isUrgent = isUrgent,
                authorName = "Mr. Jaafar Mahdi"
            )
            repository.createAnnouncement(item)
            _userMessage.value = "Announcement posted to $targetAudience"
        }
    }

    fun deleteAnnouncement(id: Long) {
        viewModelScope.launch {
            repository.deleteAnnouncement(id)
            _userMessage.value = "Announcement removed"
        }
    }

    // Complaints Actions
    fun submitComplaint(
        category: String,
        urgency: String,
        description: String,
        requestCallback: Boolean,
        callbackNumber: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user == null || user.role != "PARENT") {
                _userMessage.value = "Please sign in as parent to submit an inquiry"
                return@launch
            }
            if (description.isBlank()) {
                _userMessage.value = "Please describe your concern or inquiry"
                return@launch
            }
            val complaint = ComplaintEntity(
                parentPhone = user.phoneNumber,
                parentName = user.parentName.ifBlank { "Parent" },
                studentName = user.studentName.ifBlank { "Student" },
                grade = user.grade.ifBlank { "General" },
                category = category,
                urgency = urgency,
                description = description.trim(),
                requestCallback = requestCallback,
                callbackNumber = if (requestCallback) callbackNumber.ifBlank { user.phoneNumber } else "",
                status = "Pending Review"
            )
            repository.submitComplaint(complaint)
            _userMessage.value = "Inquiry sent to Counselor Mr. Jaafar Mahdi"
            onSuccess()
        }
    }

    fun updateComplaintResponse(
        id: Long,
        status: String,
        response: String
    ) {
        viewModelScope.launch {
            if (response.isBlank()) {
                _userMessage.value = "Counselor response note cannot be empty"
                return@launch
            }
            repository.updateComplaintResponse(id, status, response)
            _userMessage.value = "Updated inquiry status and sent response to parent"
        }
    }

    fun deleteComplaint(id: Long) {
        viewModelScope.launch {
            repository.deleteComplaint(id)
            _userMessage.value = "Inquiry record deleted"
        }
    }

    // Dynamic Excel / CSV Export Generator for Survey Report (with UTF-8 BOM for Microsoft Excel)
    suspend fun generateSurveyCsvReport(survey: SurveyEntity, isAr: Boolean = true): String {
        val questions = repository.getQuestionsForSurveySync(survey.id)
        val responses = repository.getResponsesForSurveySync(survey.id)

        val sb = StringBuilder()
        // Prepend UTF-8 BOM so Microsoft Excel opens Arabic without encoding issues
        sb.append("\uFEFF")

        if (isAr) {
            sb.append("تقرير نتائج استبانة التوجيه والإرشاد\n")
            sb.append("إعداد الأستاذ: جعفر مهدي\n")
            sb.append("عنوان الاستبانة: \"${survey.title}\"\n")
            sb.append("التصنيف: ${survey.category} | الفئة المستهدفة: ${survey.targetAudience}\n")
            val dateStr = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(survey.createdAt))
            sb.append("تاريخ الإنشاء: $dateStr | إجمالي إجابات أولياء الأمور: ${responses.size}\n\n")

            // Headers
            sb.append("رقم المشاركة,اسم ولي الأمر,اسم الطالب,الصف الدراسي,رقم الجوال,تاريخ ووقت المشاركة")
            questions.forEach { q ->
                val cleanQ = q.questionText.replace("\"", "\"\"").replace(",", " - ")
                sb.append(",\"س${q.questionOrder}: $cleanQ\"")
            }
            sb.append("\n")

            // Rows
            responses.forEachIndexed { idx, resp ->
                val submitDate = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(resp.submittedAt))
                sb.append("${idx + 1},\"${resp.parentName}\",\"${resp.studentName}\",\"${resp.grade}\",\"${resp.parentPhone}\",\"$submitDate\"")
                val ansObj = try { JSONObject(resp.answersJson) } catch (e: Exception) { JSONObject() }
                questions.forEach { q ->
                    val ans = ansObj.optString(q.id.toString(), "—").replace("\"", "\"\"").replace("\n", " ")
                    sb.append(",\"$ans\"")
                }
                sb.append("\n")
            }
        } else {
            sb.append("GUIDANCE & COUNSELING SURVEY REPORT\n")
            sb.append("Prepared by: Mr. Jaafar Mahdi\n")
            sb.append("Survey Title: \"${survey.title}\"\n")
            sb.append("Category: ${survey.category} | Target: ${survey.targetAudience}\n")
            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(survey.createdAt))
            sb.append("Created: $dateStr | Total Responses: ${responses.size}\n\n")

            // Headers
            sb.append("Response #,Parent Name,Student Name,Grade,Phone,Submitted Date")
            questions.forEach { q ->
                val cleanQ = q.questionText.replace("\"", "\"\"").replace(",", ";")
                sb.append(",\"Q${q.questionOrder}: $cleanQ\"")
            }
            sb.append("\n")

            // Rows
            responses.forEachIndexed { idx, resp ->
                val submitDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(resp.submittedAt))
                sb.append("${idx + 1},\"${resp.parentName}\",\"${resp.studentName}\",\"${resp.grade}\",\"${resp.parentPhone}\",\"$submitDate\"")
                val ansObj = try { JSONObject(resp.answersJson) } catch (e: Exception) { JSONObject() }
                questions.forEach { q ->
                    val ans = ansObj.optString(q.id.toString(), "N/A").replace("\"", "\"\"").replace("\n", " ")
                    sb.append(",\"$ans\"")
                }
                sb.append("\n")
            }
        }

        return sb.toString()
    }

    // Save and Share direct Excel / CSV file
    fun exportAndShareExcelFile(context: Context, survey: SurveyEntity, isAr: Boolean = true) {
        viewModelScope.launch {
            try {
                val csvContent = generateSurveyCsvReport(survey, isAr)
                val exportDir = File(context.cacheDir, "exports")
                if (!exportDir.exists()) {
                    exportDir.mkdirs()
                }

                val safeTitle = survey.title
                    .replace("[^a-zA-Z0-9ء-ي_\\-]".toRegex(), "_")
                    .take(30)
                val fileName = "استبيان_${safeTitle}_${System.currentTimeMillis()}.csv"
                val file = File(exportDir, fileName)

                FileOutputStream(file).use { fos ->
                    fos.write(csvContent.toByteArray(Charsets.UTF_8))
                }

                val authority = "${context.packageName}.fileprovider"
                val contentUri = FileProvider.getUriForFile(context, authority, file)

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    putExtra(Intent.EXTRA_SUBJECT, if (isAr) "تقرير إكسل - ${survey.title}" else "Excel Report - ${survey.title}")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val chooser = Intent.createChooser(
                    shareIntent,
                    if (isAr) "فتح / مشاركة ملف إكسل الاستبيان" else "Open / Share Excel Survey File"
                )
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)

                _userMessage.value = if (isAr) "تم تجهيز وسحب ملف إكسل للاستبيان بنجاح!" else "Excel report generated and ready to share!"
            } catch (e: Exception) {
                _userMessage.value = if (isAr) "تعذر سحب الملف: ${e.localizedMessage}" else "Failed to export: ${e.localizedMessage}"
            }
        }
    }
}
