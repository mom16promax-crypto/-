package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ComplaintEntity
import com.example.ui.GuidanceViewModel
import com.example.ui.Strings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ComplaintsScreen(viewModel: GuidanceViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val complaints by viewModel.complaints.collectAsStateWithLifecycle()
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()
    val isAdmin = currentUser?.role == "ADMIN"

    var showFileComplaintDialog by remember { mutableStateOf(false) }
    var selectedComplaintForResponse by remember { mutableStateOf<ComplaintEntity?>(null) }
    var selectedStatusFilter by remember { mutableStateOf("All") }

    val statusFilters = if (isArabic) {
        listOf("الكل", "قيد المراجعة", "جاري الاطلاع", "قيد المتابعة", "تم الحل والإغلاق")
    } else {
        listOf("All", "Pending Review", "In Review", "In Progress", "Resolved")
    }

    val relevantComplaints = if (isAdmin) {
        complaints.filter {
            if (selectedStatusFilter == "All" || selectedStatusFilter == "الكل") true
            else {
                when (selectedStatusFilter) {
                    "قيد المراجعة", "Pending Review" -> it.status == "Pending Review"
                    "جاري الاطلاع", "In Review" -> it.status == "In Review"
                    "قيد المتابعة", "In Progress" -> it.status == "In Progress"
                    "تم الحل والإغلاق", "Resolved" -> it.status == "Resolved"
                    else -> true
                }
            }
        }
    } else {
        complaints.filter { it.parentPhone == currentUser?.phoneNumber }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("complaints_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isAdmin) Strings.adminLineHeader(isArabic) else Strings.directLineHeader(isArabic),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isAdmin) Strings.adminLineSubtitle(isArabic) else Strings.directLineSubtitle(isArabic),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!isAdmin) {
                Button(
                    onClick = { showFileComplaintDialog = true },
                    modifier = Modifier.testTag("file_complaint_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(Strings.newIssue(isArabic))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (isAdmin) {
            // Status Filter Chips for Admin
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(statusFilters) { status ->
                    val isSelected = selectedStatusFilter == status || (status == "الكل" && selectedStatusFilter == "All")
                    Surface(
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable { selectedStatusFilter = status }
                    ) {
                        Text(
                            text = status,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (relevantComplaints.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isAdmin) {
                            if (isArabic) "لا توجد حالات حالياً مطابقة لهذا التصنيف" else "No issues currently matching this filter"
                        } else {
                            if (isArabic) "لم تقم برفع أي استشارة أو شكوى حتى الآن" else "No complaints or inquiries filed yet"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!isAdmin) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isArabic)
                                "اضغط على 'استشارة / شكوى جديدة' بأعلى الشاشة للتواصل السري المباشر مع المرشد الطلابي."
                            else
                                "Tap 'New Issue' above to submit any concern regarding academic, behavioral, or emotional guidance.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(relevantComplaints, key = { it.id }) { item ->
                    ComplaintCard(
                        complaint = item,
                        isAdmin = isAdmin,
                        isArabic = isArabic,
                        onRespond = { selectedComplaintForResponse = item },
                        onDelete = { viewModel.deleteComplaint(item.id) }
                    )
                }
            }
        }
    }

    if (showFileComplaintDialog) {
        FileComplaintDialog(
            viewModel = viewModel,
            isArabic = isArabic,
            onDismiss = { showFileComplaintDialog = false }
        )
    }

    selectedComplaintForResponse?.let { complaint ->
        CounselorResponseDialog(
            complaint = complaint,
            isArabic = isArabic,
            onDismiss = { selectedComplaintForResponse = null },
            onSubmitResponse = { newStatus, responseText ->
                viewModel.updateComplaintResponse(complaint.id, newStatus, responseText)
                selectedComplaintForResponse = null
            }
        )
    }
}

@Composable
private fun ComplaintCard(
    complaint: ComplaintEntity,
    isAdmin: Boolean,
    isArabic: Boolean,
    onRespond: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("complaint_card_${complaint.id}"),
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
                // Category & Urgency Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = complaint.category,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    val urgencyColor = when (complaint.urgency) {
                        "Urgent", "عاجل جداً" -> Color(0xFFEF4444)
                        "Important", "هام" -> Color(0xFFF59E0B)
                        else -> Color(0xFF6B7280)
                    }
                    val urgencyLabel = when (complaint.urgency) {
                        "Urgent" -> Strings.urgencyUrgent(isArabic)
                        "Important" -> Strings.urgencyImportant(isArabic)
                        else -> Strings.urgencyNormal(isArabic)
                    }
                    Surface(
                        color = urgencyColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = urgencyLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = urgencyColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Status Badge
                StatusBadge(status = complaint.status, isArabic = isArabic)
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (isAdmin) {
                Text(
                    text = if (isArabic)
                        "من: ${complaint.parentName} (الطالب: ${complaint.studentName} • ${complaint.grade})"
                    else
                        "From: ${complaint.parentName} (Student: ${complaint.studentName} • ${complaint.grade})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (complaint.requestCallback) {
                    Text(
                        text = if (isArabic)
                            "📞 مطلوب اتصال هاتفي: ${complaint.callbackNumber.ifBlank { complaint.parentPhone }}"
                        else
                            "📞 Callback Requested: ${complaint.callbackNumber.ifBlank { complaint.parentPhone }}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0284C7),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            } else {
                Text(
                    text = if (isArabic)
                        "الطالب: ${complaint.studentName} (${complaint.grade})"
                    else
                        "Student: ${complaint.studentName} (${complaint.grade})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = complaint.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Counselor Official Response Section
            if (complaint.counselorResponse.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SupportAgent,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Strings.counselorNoteHeader(isArabic),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = complaint.counselorResponse,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        if (complaint.respondedAt > 0L) {
                            val rDate = SimpleDateFormat("yyyy/MM/dd • HH:mm", Locale.getDefault()).format(Date(complaint.respondedAt))
                            Text(
                                text = if (isArabic) "تاريخ الرد: $rDate" else "Responded on $rDate",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dateStr = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date(complaint.createdAt))
                Text(
                    text = if (isArabic) "تاريخ الرفع: $dateStr" else "Filed on $dateStr",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )

                if (isAdmin) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                        }
                        Button(
                            onClick = onRespond,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("respond_button_${complaint.id}")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (complaint.counselorResponse.isBlank()) Strings.respondAndUpdate(isArabic) else (if (isArabic) "تعديل الرد" else "Update Note"))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String, isArabic: Boolean) {
    val (bgColor, textColor, icon) = when (status) {
        "Pending Review", "قيد المراجعة" -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), Icons.Default.HourglassEmpty)
        "In Review", "جاري الاطلاع" -> Triple(Color(0xFFDBEAFE), Color(0xFF1D4ED8), Icons.Default.Visibility)
        "In Progress", "قيد المتابعة" -> Triple(Color(0xFFFED7AA), Color(0xFFC2410C), Icons.Default.Pending)
        "Resolved", "تم الحل والإغلاق" -> Triple(Color(0xFFD1FAE5), Color(0xFF047857), Icons.Default.CheckCircle)
        else -> Triple(Color(0xFFF3F4F6), Color(0xFF374151), Icons.Default.Info)
    }

    val displayLabel = when (status) {
        "Pending Review" -> Strings.statusPending(isArabic)
        "In Review" -> Strings.statusInReview(isArabic)
        "In Progress" -> Strings.statusInProgress(isArabic)
        "Resolved" -> Strings.statusResolved(isArabic)
        else -> status
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = displayLabel,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FileComplaintDialog(
    viewModel: GuidanceViewModel,
    isArabic: Boolean,
    onDismiss: () -> Unit
) {
    var category by remember {
        mutableStateOf(if (isArabic) Strings.catAcademic(true) else "Academic Support")
    }
    var urgency by remember {
        mutableStateOf(if (isArabic) "عادي" else "Normal")
    }
    var description by remember { mutableStateOf("") }
    var requestCallback by remember { mutableStateOf(false) }
    var callbackNumber by remember { mutableStateOf("") }

    val categories = if (isArabic) {
        listOf(
            Strings.catAcademic(true),
            Strings.catBehavior(true),
            Strings.catPsychology(true),
            Strings.catBullying(true),
            Strings.catHealth(true),
            Strings.catGeneral(true)
        )
    } else {
        listOf(
            "Academic Support",
            "Behavioral & Conduct",
            "Psychological & Stress",
            "Peer Relations & Bullying",
            "Attendance & Health",
            "General Inquiries"
        )
    }

    val urgencies = if (isArabic) {
        listOf("عادي", "هام", "عاجل جداً")
    } else {
        listOf("Normal", "Important", "Urgent")
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
                            text = Strings.directLineHeader(isArabic),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = Strings.directLineSubtitle(isArabic),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Text(Strings.concernCategory(isArabic), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = catExp,
                        onDismissRequest = { catExp = false }
                    ) {
                        categories.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c) },
                                onClick = {
                                    category = c
                                    catExp = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(Strings.urgencyLevel(isArabic), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    urgencies.forEach { u ->
                        val isSel = urgency == u
                        Button(
                            onClick = { urgency = u },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) {
                                    when (u) {
                                        "Urgent", "عاجل جداً" -> Color(0xFFEF4444)
                                        "Important", "هام" -> Color(0xFFF59E0B)
                                        else -> MaterialTheme.colorScheme.primary
                                    }
                                } else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = u,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(Strings.describeConcern(isArabic)) },
                    placeholder = {
                        Text(if (isArabic) "اكتب تفاصيل الموضوع لمساعدة المرشد الطلابي الأستاذ جعفر مهدي في تقييم الحالة بدقة..." else "Provide details to help counselor Mr. Jaafar Mahdi assess the situation...")
                    },
                    minLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("complaint_description_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { requestCallback = !requestCallback }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = requestCallback,
                        onCheckedChange = { requestCallback = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Strings.requestCallback(isArabic),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (requestCallback) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = callbackNumber,
                        onValueChange = { callbackNumber = it },
                        label = { Text(if (isArabic) "رقم هاتف الاتصال البديل (اختياري)" else "Callback Phone Number (optional if different)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        viewModel.submitComplaint(
                            category = category,
                            urgency = urgency,
                            description = description,
                            requestCallback = requestCallback,
                            callbackNumber = callbackNumber
                        ) {
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_complaint_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(Strings.submitToOffice(isArabic), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CounselorResponseDialog(
    complaint: ComplaintEntity,
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onSubmitResponse: (String, String) -> Unit
) {
    var status by remember { mutableStateOf(complaint.status) }
    var responseText by remember { mutableStateOf(complaint.counselorResponse) }

    val statusOptions = if (isArabic) {
        listOf("قيد المراجعة", "جاري الاطلاع", "قيد المتابعة", "تم الحل والإغلاق")
    } else {
        listOf("Pending Review", "In Review", "In Progress", "Resolved")
    }

    val quickResponses = if (isArabic) {
        listOf(
            "تم عقد جلسة إرشاد وتوجيه فردية مع الطالب في مكتب الإرشاد.",
            "تم التنسيق مع معلم المادة لوضع خطة دعم أكاديمي ومتابعة خاصة.",
            "تم التواصل هاتفياً مع ولي الأمر والاتفاق على خطوات المتابعة المنزلية.",
            "تمت دراسة الحالة ومعالجة الموضوع بخصوصية تامة وإغلاق الطلب."
        )
    } else {
        listOf(
            "Individual counseling session scheduled with student.",
            "Coordinated with subject instructor for academic plan.",
            "Parent consultation call completed. Action plan established.",
            "Situation thoroughly investigated and resolved amicably."
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) "مراجعة وتوجيه المرشد الطلابي" else "Counselor Review & Response",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isArabic) "مقدمة من ولي الأمر: ${complaint.parentName} للطالب: ${complaint.studentName}" else "Filed by ${complaint.parentName} for ${complaint.studentName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (isArabic) "تفاصيل المشكلة (${complaint.category} • ${complaint.urgency}):" else "Parent's Concern (${complaint.category} • ${complaint.urgency}):",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = complaint.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (complaint.requestCallback) {
                            Text(
                                text = if (isArabic) "📞 مطلوب اتصال: ${complaint.callbackNumber.ifBlank { complaint.parentPhone }}" else "Callback requested: ${complaint.callbackNumber.ifBlank { complaint.parentPhone }}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF0284C7),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(if (isArabic) "تحديث حالة القضية:" else "Update Case Status:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                var statExp by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = statExp,
                    onExpandedChange = { statExp = !statExp }
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statExp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = statExp,
                        onDismissRequest = { statExp = false }
                    ) {
                        statusOptions.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = {
                                    status = s
                                    statExp = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(if (isArabic) "نماذج ردود وتوجيهات سريعة:" else "Quick Guidance Templates:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                quickResponses.forEach { qr ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { responseText = qr }
                            .padding(vertical = 3.dp)
                    ) {
                        Text(
                            text = "+ $qr",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = responseText,
                    onValueChange = { responseText = it },
                    label = { Text(if (isArabic) "ملاحظة وتوجيه المرشد (تظهر مباشرة لولي الأمر)" else "Counselor Response Note (Visible to Parent)") },
                    placeholder = { Text(if (isArabic) "اكتب التوجيهات والإجراءات المتخذة لدعم الطالب..." else "Write your guidance recommendations and action steps...") },
                    minLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("counselor_response_input")
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val finalStatus = when (status) {
                            "قيد المراجعة" -> "Pending Review"
                            "جاري الاطلاع" -> "In Review"
                            "قيد المتابعة" -> "In Progress"
                            "تم الحل والإغلاق" -> "Resolved"
                            else -> status
                        }
                        onSubmitResponse(finalStatus, responseText)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_counselor_response_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(Strings.saveResponse(isArabic), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
