package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.Strings

@Composable
fun ArchitectureDialog(
    isArabic: Boolean = true,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .testTag("architecture_dialog_surface"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
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
                            imageVector = Icons.Default.Architecture,
                            contentDescription = "Architecture",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isArabic) "التوثيق المعماري للنظام" else "Technical Architecture",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${Strings.appTitle(isArabic)} • ${Strings.preparedBy(isArabic)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_architecture_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                ArchitecturePillar(
                    icon = Icons.Default.Security,
                    title = if (isArabic) "1. نظام المصادقة المزدوج والصلاحيات" else "1. Dual Role Authentication Layer",
                    detail = if (isArabic)
                        "فصل مرن بين بوابة أولياء الأمور (رقم الجوال، اسم الطالب، الصف الدراسي) وبوابة المرشد الطلابي والإدارة (اسم المستخدم admin وكلمة المرور admin123). يشمل زر تحويل فوري للتجربة السريعة."
                    else
                        "Structured access separation between Parents (mobile number credential binding, student profile, grade cohort) and Counselor / Admin (username & pass authentication: admin / admin123). Supports instant role switching for rapid testing and audit."
                )

                ArchitecturePillar(
                    icon = Icons.Default.Storage,
                    title = if (isArabic) "2. قاعدة بيانات Room SQLite المدمجة" else "2. Persistent Room SQLite Database",
                    detail = if (isArabic)
                        "تخزين واسترجاع تفاعلي لكافة الجداول: المستخدمين، الاستبانات، الأسئلة الديناميكية، إجابات أولياء الأمور، التعاميم، وقضايا الطلاب والاستشارات باستخدام Coroutines و StateFlow."
                    else
                        "Robust local persistence via Android Jetpack Room with Kotlin Symbol Processing (KSP). Normalizes Users, Surveys, Dynamic Questions, Submitted Responses, Targeted Announcements, and Confidential Complaints."
                )

                ArchitecturePillar(
                    icon = Icons.Default.Assignment,
                    title = if (isArabic) "3. محرك الاستبانات التفاعلية الديناميكي" else "3. Dynamic Survey & Response Engine",
                    detail = if (isArabic)
                        "دعم 4 أنماط مختلفة من الأسئلة: مقياس النجوم (1 إلى 5)، الاختيار من متعدد، نعم/لا، والنصوص الحرة. حساب تلقائي للمتوسطات ونسب المشاركة الإحصائية."
                    else
                        "Configurable schema supporting 4 question types: Star Rating (1-5), Multiple Choice options, Yes/No decision branches, and Free-form text. Stores normalized JSON answer matrices with real-time response aggregation."
                )

                ArchitecturePillar(
                    icon = Icons.Default.Analytics,
                    title = if (isArabic) "4. خط أنابيب تصدير تقارير Excel و CSV" else "4. Dynamic Report Export Pipeline",
                    detail = if (isArabic)
                        "توليد تقارير جداول بيانات متوافقة مع Excel و SheetJS فورياً تحتوي على بيانات الطالب، وقت المشاركة، وأعمدة لكل سؤال مع إمكانية النسخ والمشاركة المباشرة."
                    else
                        "Emulates SheetJS / Excel export by dynamically serializing survey responses, student demographics, timestamps, and question columns into standards-compliant CSV and tabular spreadsheets with instant share and copy capabilities."
                )

                ArchitecturePillar(
                    icon = Icons.Default.Group,
                    title = if (isArabic) "5. الاستهداف الذكي والخط المباشر للإرشاد" else "5. Targeted Announcements & Direct Line",
                    detail = if (isArabic)
                        "محرك توجيه الإعلانات (لجميع أولياء الأمور، أو الحسابات المسجلة حديثاً فقط) مع مسار استشارات سرية يتتبع 4 مراحل لحالة الطلب (قيد المراجعة، جاري الاطلاع، قيد المتابعة، تم الحل) مع تدوين ردود المرشد."
                    else
                        "Audience targeting engine capable of filtering broadcasts to 'All Parents', 'Newly Registered Accounts', or specific grade bands. Includes a direct confidential counseling triage pipeline with 4-stage status tracking (Pending, In Review, In Progress, Resolved)."
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isArabic) "إسناد واعتماد المنصة" else "Platform Attribution",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isArabic)
                                "صُمم ونُفذ لخدمة التوجيه والإرشاد المدرسي. إعداد الأستاذ جعفر مهدي بهدف تعزيز التحصيل الأكاديمي، وتوفير بيئة نفسية آمنة ومحفزة، وتوثيق الشراكة التربوية بين المدرسة والأسرة."
                            else
                                "Designed and developed for School Guidance & Counseling. Prepared by Mr. Jaafar Mahdi to elevate student academic well-being, psychological safety, and parent-school partnership.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dismiss_architecture_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isArabic) "إغلاق التوثيق" else "Close Documentation")
                }
            }
        }
    }
}

@Composable
private fun ArchitecturePillar(
    icon: ImageVector,
    title: String,
    detail: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(24.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
