package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SurveyEntity
import com.example.data.model.SurveyQuestionEntity
import com.example.data.model.SurveyResponseEntity
import com.example.ui.GuidanceViewModel
import com.example.ui.Strings
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SurveysScreen(viewModel: GuidanceViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val surveys by viewModel.surveys.collectAsStateWithLifecycle()
    val allResponses by viewModel.allResponses.collectAsStateWithLifecycle()
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()
    val isAdmin = currentUser?.role == "ADMIN"

    var showCreateSurveyDialog by remember { mutableStateOf(false) }
    var viewingResponsesSurvey by remember { mutableStateOf<SurveyEntity?>(null) }
    var activeSurveyToAnswer by remember { mutableStateOf<SurveyEntity?>(null) }
    var exportReportSurvey by remember { mutableStateOf<SurveyEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("surveys_screen")
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isAdmin) Strings.adminSurveysHeader(isArabic) else Strings.surveysHeader(isArabic),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isAdmin) Strings.adminSurveysSubtitle(isArabic) else Strings.surveysSubtitle(isArabic),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isAdmin) {
                Button(
                    onClick = { showCreateSurveyDialog = true },
                    modifier = Modifier.testTag("create_survey_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(Strings.newSurvey(isArabic))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (surveys.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No surveys available yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(surveys, key = { it.id }) { survey ->
                    val responsesForThisSurvey = allResponses.filter { it.surveyId == survey.id }
                    val hasCompleted = currentUser?.let { user ->
                        responsesForThisSurvey.any { it.parentPhone == user.phoneNumber }
                    } ?: false

                    SurveyCard(
                        survey = survey,
                        isAdmin = isAdmin,
                        hasCompleted = hasCompleted,
                        responseCount = responsesForThisSurvey.size,
                        isArabic = isArabic,
                        onAnswerSurvey = { activeSurveyToAnswer = survey },
                        onViewResponses = { viewingResponsesSurvey = survey },
                        onExportReport = { exportReportSurvey = survey },
                        onToggleActive = { viewModel.toggleSurveyStatus(survey) },
                        onDelete = { viewModel.deleteSurvey(survey.id) }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showCreateSurveyDialog) {
        CreateSurveyDialog(
            isArabic = isArabic,
            onDismiss = { showCreateSurveyDialog = false },
            onCreate = { title, desc, cat, target, questions ->
                viewModel.createSurvey(title, desc, cat, target, questions)
                showCreateSurveyDialog = false
            }
        )
    }

    activeSurveyToAnswer?.let { survey ->
        AnswerSurveyDialog(
            survey = survey,
            viewModel = viewModel,
            isArabic = isArabic,
            onDismiss = { activeSurveyToAnswer = null }
        )
    }

    viewingResponsesSurvey?.let { survey ->
        ViewResponsesDialog(
            survey = survey,
            viewModel = viewModel,
            isArabic = isArabic,
            onDismiss = { viewingResponsesSurvey = null },
            onExport = {
                viewingResponsesSurvey = null
                exportReportSurvey = survey
            }
        )
    }

    exportReportSurvey?.let { survey ->
        ExportReportDialog(
            survey = survey,
            viewModel = viewModel,
            isArabic = isArabic,
            onDismiss = { exportReportSurvey = null }
        )
    }
}

@Composable
private fun SurveyCard(
    survey: SurveyEntity,
    isAdmin: Boolean,
    hasCompleted: Boolean,
    responseCount: Int,
    isArabic: Boolean,
    onAnswerSurvey: () -> Unit,
    onViewResponses: () -> Unit,
    onExportReport: () -> Unit,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("survey_card_${survey.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category & Target Badges
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = survey.category,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = survey.targetAudience,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Status Indicator
                Surface(
                    color = if (survey.isActive) Color(0xFF10B981).copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (survey.isActive) (if (isArabic) "نشطة" else "Active") else (if (isArabic) "مغلقة" else "Closed"),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (survey.isActive) Color(0xFF047857) else Color.DarkGray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = survey.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = survey.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(10.dp))

            if (isAdmin) {
                // Admin Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${Strings.responsesCount(isArabic)} $responseCount",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = onViewResponses,
                            modifier = Modifier.testTag("view_responses_${survey.id}")
                        ) {
                            Icon(Icons.Default.Analytics, contentDescription = Strings.viewAnalytics(isArabic), tint = MaterialTheme.colorScheme.primary)
                        }

                        IconButton(
                            onClick = onExportReport,
                            modifier = Modifier.testTag("export_survey_${survey.id}")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = Strings.exportExcel(isArabic), tint = Color(0xFF10B981))
                        }

                        IconButton(onClick = onToggleActive) {
                            Icon(
                                imageVector = if (survey.isActive) Icons.Default.ToggleOn else Icons.Default.ToggleOff,
                                contentDescription = "Toggle Active",
                                tint = if (survey.isActive) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }

                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                        }
                    }
                }
            } else {
                // Parent Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (hasCompleted) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Strings.completedByYou(isArabic),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857)
                            )
                        }
                        OutlinedButton(
                            onClick = onAnswerSurvey,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(Strings.updateAnswers(isArabic))
                        }
                    } else {
                        Text(
                            text = if (survey.isActive) Strings.pendingYourResponse(isArabic) else Strings.surveyClosed(isArabic),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = onAnswerSurvey,
                            enabled = survey.isActive,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("answer_survey_${survey.id}")
                        ) {
                            Text(Strings.startSurvey(isArabic))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateSurveyDialog(
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onCreate: (String, String, String, String, List<SurveyQuestionEntity>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Academic Well-being") }
    var targetAudience by remember { mutableStateOf("All Parents") }

    val categories = listOf("Academic Well-being", "School Climate & Safety", "Mental Health & Counseling", "Extracurricular & Arts", "Parent Feedback")
    val audiences = listOf("All Parents", "Newly Registered Accounts only", "Middle School", "High School")

    val questions = remember {
        mutableStateListOf(
            SurveyQuestionEntity(surveyId = 0, questionOrder = 1, questionText = "", questionType = "RATING", optionsString = "5 Stars")
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Build Dynamic Survey",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Survey Title") },
                    placeholder = { Text("e.g. Midterm Student Guidance & Needs Survey") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_survey_title")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Instructions") },
                    placeholder = { Text("Describe the purpose for parent participants...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category selector
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Category", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        var catExp by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = catExp,
                            onExpandedChange = { catExp = !catExp }
                        ) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExp) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = catExp,
                                onDismissRequest = { catExp = false }
                            ) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            category = cat
                                            catExp = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Audience selector
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Target Audience", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        var audExp by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = audExp,
                            onExpandedChange = { audExp = !audExp }
                        ) {
                            OutlinedTextField(
                                value = targetAudience,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = audExp) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = audExp,
                                onDismissRequest = { audExp = false }
                            ) {
                                audiences.forEach { aud ->
                                    DropdownMenuItem(
                                        text = { Text(aud) },
                                        onClick = {
                                            targetAudience = aud
                                            audExp = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Questions (${questions.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = {
                            questions.add(
                                SurveyQuestionEntity(
                                    surveyId = 0,
                                    questionOrder = questions.size + 1,
                                    questionText = "",
                                    questionType = "RATING",
                                    optionsString = ""
                                )
                            )
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Question")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Questions builder list
                questions.forEachIndexed { index, q ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Question ${index + 1}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (questions.size > 1) {
                                    IconButton(
                                        onClick = { questions.removeAt(index) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove Question", tint = Color.Red)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = q.questionText,
                                onValueChange = { newTxt ->
                                    questions[index] = q.copy(questionText = newTxt)
                                },
                                label = { Text("Question Prompt") },
                                placeholder = { Text("e.g. How satisfied are you with counselor support?") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Answer Type:", style = MaterialTheme.typography.labelSmall)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("RATING" to "Star Rating", "YES_NO" to "Yes / No", "MULTIPLE_CHOICE" to "Multiple Choice", "TEXT" to "Text").forEach { (typeKey, typeLabel) ->
                                    val isSelected = q.questionType == typeKey
                                    Surface(
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .clickable {
                                                val defaultOpts = if (typeKey == "MULTIPLE_CHOICE") "Option 1||Option 2||Option 3" else ""
                                                questions[index] = q.copy(questionType = typeKey, optionsString = defaultOpts)
                                            }
                                    ) {
                                        Text(
                                            text = typeLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            if (q.questionType == "MULTIPLE_CHOICE") {
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = q.optionsString,
                                    onValueChange = { opts ->
                                        questions[index] = q.copy(optionsString = opts)
                                    },
                                    label = { Text("Choices (separate with ||)") },
                                    placeholder = { Text("Very Effective||Somewhat||Needs Revision") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        onCreate(title, description, category, targetAudience, questions)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_create_survey_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Publish Survey", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AnswerSurveyDialog(
    survey: SurveyEntity,
    viewModel: GuidanceViewModel,
    isArabic: Boolean,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var questions by remember { mutableStateOf<List<SurveyQuestionEntity>>(emptyList()) }
    val answersMap = remember { mutableStateMapOf<Long, String>() }

    LaunchedEffect(survey.id) {
        val qList = viewModel.getQuestionsForSurveySync(survey.id)
        questions = qList
        // Pre-fill rating defaults
        qList.forEach { q ->
            if (q.questionType == "RATING" && !answersMap.containsKey(q.id)) {
                answersMap[q.id] = "5"
            }
            if (q.questionType == "YES_NO" && !answersMap.containsKey(q.id)) {
                answersMap[q.id] = "Yes"
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = survey.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Guidance Office Questionnaire • ${survey.category}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Text(
                    text = survey.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                questions.forEachIndexed { idx, q ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Q${idx + 1}. ${q.questionText}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            when (q.questionType) {
                                "RATING" -> {
                                    val currentRating = answersMap[q.id]?.toIntOrNull() ?: 5
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        (1..5).forEach { star ->
                                            IconButton(
                                                onClick = { answersMap[q.id] = star.toString() },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (star <= currentRating) Icons.Default.Star else Icons.Default.StarBorder,
                                                    contentDescription = "$star Stars",
                                                    tint = if (star <= currentRating) Color(0xFFF59E0B) else Color.Gray,
                                                    modifier = Modifier.size(32.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "$currentRating / 5",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                "YES_NO" -> {
                                    val selected = answersMap[q.id] ?: "Yes"
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        listOf("Yes", "No").forEach { opt ->
                                            val isOptSelected = selected.equals(opt, ignoreCase = true)
                                            Button(
                                                onClick = { answersMap[q.id] = opt },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isOptSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                                ),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text(
                                                    text = opt,
                                                    color = if (isOptSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                                    fontWeight = if (isOptSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        }
                                    }
                                }
                                "MULTIPLE_CHOICE" -> {
                                    val options = q.optionsString.split("||").filter { it.isNotBlank() }
                                    val currentChoice = answersMap[q.id] ?: options.firstOrNull() ?: ""
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        options.forEach { opt ->
                                            val isSelected = currentChoice == opt
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .clickable { answersMap[q.id] = opt }
                                                    .padding(vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = isSelected,
                                                    onClick = { answersMap[q.id] = opt }
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = opt,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                                else -> { // TEXT
                                    val currentText = answersMap[q.id] ?: ""
                                    OutlinedTextField(
                                        value = currentText,
                                        onValueChange = { answersMap[q.id] = it },
                                        placeholder = { Text("Type your answer here...") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        viewModel.submitSurveyResponse(survey.id, answersMap) {
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_survey_answers_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit Questionnaire Response", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ViewResponsesDialog(
    survey: SurveyEntity,
    viewModel: GuidanceViewModel,
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onExport: () -> Unit
) {
    var questions by remember { mutableStateOf<List<SurveyQuestionEntity>>(emptyList()) }
    var responses by remember { mutableStateOf<List<SurveyResponseEntity>>(emptyList()) }

    LaunchedEffect(survey.id) {
        questions = viewModel.getQuestionsForSurveySync(survey.id)
        responses = viewModel.getResponsesForSurveySync(survey.id)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Survey Analytics & Responses",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${survey.title} • ${responses.size} Total Submissions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onExport,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export Excel Report")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Aggregated Question Metrics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                questions.forEachIndexed { idx, q ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Q${idx + 1}. ${q.questionText} (${q.questionType})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Calculate aggregate
                            if (q.questionType == "RATING") {
                                var sum = 0.0
                                var count = 0
                                responses.forEach { r ->
                                    val obj = try { JSONObject(r.answersJson) } catch (e: Exception) { JSONObject() }
                                    val ratingVal = obj.optString(q.id.toString()).toDoubleOrNull()
                                    if (ratingVal != null) {
                                        sum += ratingVal
                                        count++
                                    }
                                }
                                val avg = if (count > 0) sum / count else 0.0
                                Text(
                                    text = "Average Rating: ${String.format(Locale.US, "%.1f", avg)} / 5.0 ($count ratings logged)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFD97706)
                                )
                            } else {
                                Text(
                                    text = "${responses.size} answers recorded for this question",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Individual Submissions (${responses.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (responses.isEmpty()) {
                    Text(
                        text = "No parent responses submitted yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    responses.forEachIndexed { rIdx, resp ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${resp.parentName} (${resp.studentName} • ${resp.grade})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    val dateStr = SimpleDateFormat("MMM d, HH:mm", Locale.US).format(Date(resp.submittedAt))
                                    Text(dateStr, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                val ansObj = try { JSONObject(resp.answersJson) } catch (e: Exception) { JSONObject() }
                                questions.forEach { q ->
                                    val answer = ansObj.optString(q.id.toString(), "—")
                                    Text(
                                        text = "• ${q.questionText}: $answer",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExportReportDialog(
    survey: SurveyEntity,
    viewModel: GuidanceViewModel,
    isArabic: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var csvReport by remember { mutableStateOf("") }
    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(survey.id, isArabic) {
        csvReport = viewModel.generateSurveyCsvReport(survey, isArabic)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isArabic) "سحب تقرير Excel للاستبيان" else "Dynamic Survey Export",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isArabic) "جدول بيانات بصيغة Excel / CSV متوافقة" else "Excel / CSV Compatible Spreadsheet",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF047857)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Text(
                    text = if (isArabic)
                        "تم توليد التقرير بترميز UTF-8 المتوافق مع برامج الجداول الحسابية (Excel / Google Sheets) متضمناً أسماء أولياء الأمور، بيانات الطلاب، الصفوف، وتوقيت الإرسال مع أعمدة منفصلة لإجابة كل سؤال."
                    else
                        "Report generated with UTF-8 BOM encoding for Microsoft Excel & Sheets, including parent names, student classes, timestamps, and per-question columns.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Direct Action: Export & Save Excel File
                Button(
                    onClick = {
                        viewModel.exportAndShareExcelFile(context, survey, isArabic)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("download_excel_file_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "📊 سحب وتنزيل ملف Excel (.csv)" else "📊 Export & Download Excel (.csv)",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isArabic) "معاينة محتوى جدول البيانات:" else "Spreadsheet Data Preview:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Table preview box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = csvReport.ifBlank { if (isArabic) "جاري توليد تقرير جدول البيانات..." else "Generating spreadsheet report..." },
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Guidance Survey Report", csvReport)
                            clipboard.setPrimaryClip(clip)
                            copied = true
                            viewModel.showToast(if (isArabic) "تم نسخ بيانات الجدول للحافظة بنجاح!" else "Report copied to clipboard!")
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (copied) (if (isArabic) "تم النسخ!" else "Copied!") else (if (isArabic) "نسخ النص" else "Copy CSV"))
                    }

                    Button(
                        onClick = {
                            viewModel.exportAndShareExcelFile(context, survey, isArabic)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isArabic) "مشاركة التقرير" else "Share Report")
                    }
                }
            }
        }
    }
}
