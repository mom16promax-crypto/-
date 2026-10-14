package com.example.ui

object Strings {
    fun appTitle(isAr: Boolean) = if (isAr) "تطبيق التوجيه والإرشاد" else "Guidance App"
    fun preparedBy(isAr: Boolean) = if (isAr) "إعداد الأستاذ جعفر مهدي" else "Prepared by Mr. Jaafar Mahdi"
    fun counselorTitle(isAr: Boolean) = if (isAr) "المرشد الطلابي ورئيس قسم التوجيه" else "School Counselor & Dept Head"
    
    // Auth & Roles
    fun parentAccess(isAr: Boolean) = if (isAr) "دخول ولي الأمر" else "Parent Access"
    fun adminAccess(isAr: Boolean) = if (isAr) "دخول المرشد / الإدارة" else "Admin Access"
    fun parentPortalBadge(isAr: Boolean) = if (isAr) "بوابة ولي الأمر" else "Parent View"
    fun adminPortalBadge(isAr: Boolean) = if (isAr) "بوابة الإدارة والمرشد" else "Admin Portal"
    fun switchRole(isAr: Boolean) = if (isAr) "تبديل الصلاحية" else "Switch Role"
    fun switchToAdmin(isAr: Boolean) = if (isAr) "التحويل كمرشد طلابي" else "Switch to Admin"
    fun switchToParent(isAr: Boolean) = if (isAr) "التحويل كولي أمر" else "Switch to Parent"
    fun mobileNumber(isAr: Boolean) = if (isAr) "رقم الجوال" else "Mobile Phone Number"
    fun parentNameLabel(isAr: Boolean) = if (isAr) "اسم ولي الأمر" else "Parent / Guardian Name"
    fun studentNameLabel(isAr: Boolean) = if (isAr) "اسم الطالب / الطالبة" else "Student Full Name"
    fun gradeCohortLabel(isAr: Boolean) = if (isAr) "الصف الدراسي" else "Student Grade Cohort"
    fun signInRegisterParent(isAr: Boolean) = if (isAr) "تسجيل الدخول / إنشاء حساب" else "Sign In / Register as Parent"
    fun username(isAr: Boolean) = if (isAr) "اسم المستخدم" else "Username"
    fun password(isAr: Boolean) = if (isAr) "كلمة المرور" else "Password"
    fun signInAdmin(isAr: Boolean) = if (isAr) "دخول المرشد (الأستاذ جعفر مهدي)" else "Sign In as Admin (Mr. Jaafar Mahdi)"
    fun demoShortcuts(isAr: Boolean) = if (isAr) "⚡ اختصارات التجربة السريعة" else "⚡ Instant Testing Shortcuts"
    fun demoParent(isAr: Boolean) = if (isAr) "تجربة كولي أمر" else "Demo Parent"
    fun demoAdmin(isAr: Boolean) = if (isAr) "تجربة كمرشد (إدارة)" else "Demo Admin"
    fun logout(isAr: Boolean) = if (isAr) "تسجيل الخروج" else "Log out"

    // Navigation Tabs
    fun tabHome(isAr: Boolean) = if (isAr) "الرئيسية" else "Home"
    fun tabSurveys(isAr: Boolean) = if (isAr) "الاستبانات" else "Surveys"
    fun tabAnnouncements(isAr: Boolean) = if (isAr) "الإعلانات" else "Notices"
    fun tabComplaints(isAr: Boolean) = if (isAr) "الخط المباشر" else "Direct Line"

    // Dashboard
    fun welcome(isAr: Boolean) = if (isAr) "مرحباً بك،" else "Welcome,"
    fun studentLabel(isAr: Boolean) = if (isAr) "الطالب:" else "Student:"
    fun realTimeStats(isAr: Boolean) = if (isAr) "الإحصائيات الفورية" else "Real-Time Statistics"
    fun activeSurveys(isAr: Boolean) = if (isAr) "الاستبانات النشطة" else "Active Surveys"
    fun completedResponses(isAr: Boolean) = if (isAr) "المشاركات المكتملة" else "Completed Responses"
    fun totalResponses(isAr: Boolean) = if (isAr) "إجمالي الاستجابات" else "Total Responses"
    fun announcementsCount(isAr: Boolean) = if (isAr) "إعلانات الإرشاد" else "Announcements"
    fun openInquiries(isAr: Boolean) = if (isAr) "الاستشارات المفتوحة" else "Open Inquiries"
    fun myConsultations(isAr: Boolean) = if (isAr) "استشاراتي" else "My Consultations"
    fun quickActions(isAr: Boolean) = if (isAr) "إجراءات سريعة" else "Quick Actions"
    fun counselorCorner(isAr: Boolean) = if (isAr) "ركن التوجيه والإرشاد • الأستاذ جعفر مهدي" else "Counselor's Corner • Mr. Jaafar Mahdi"
    fun counselorQuote(isAr: Boolean) = if (isAr)
        "\"يبدأ التوجيه الفعّال بالاستماع لأبنائنا الطلبة وبناء جسور الثقة مع أولياء الأمور قبل تقديم الحلول. مكتبنا وخطنا المباشر متاح دائماً لدعمكم.\""
    else
        "\"Guiding students toward emotional stability and academic confidence begins with empathy and regular collaboration between parents and educators. Our door and direct line remain open at all times.\""
    fun officeHours(isAr: Boolean) = if (isAr) "ساعات التواصل ومكتب الإرشاد" else "Guidance Office Contact & Hours"
    fun officeDetails(isAr: Boolean) = if (isAr) "الأحد - الخميس: 7:30 ص – 2:30 م • مكتب رقم 104" else "Sun - Thu: 7:30 AM – 2:30 PM • Office #104"

    // Surveys
    fun surveysHeader(isAr: Boolean) = if (isAr) "استبانات واستطلاعات الرأي" else "Guidance Questionnaires"
    fun surveysSubtitle(isAr: Boolean) = if (isAr) "شارك برأيك في استبانات المدرسة لتعزيز بيئة التعلم والدعم الطلابي" else "Share your voice to help improve student well-being"
    fun adminSurveysHeader(isAr: Boolean) = if (isAr) "منشئ ومحرر الاستبانات" else "Surveys & Form Builder"
    fun adminSurveysSubtitle(isAr: Boolean) = if (isAr) "تصميم استبانات تفاعلية وتحليل إجابات أولياء الأمور فـورياً" else "Design dynamic surveys and analyze parental feedback"
    fun newSurvey(isAr: Boolean) = if (isAr) "استبانة جديدة" else "New Survey"
    fun startSurvey(isAr: Boolean) = if (isAr) "بدء الإجابة" else "Start Survey"
    fun updateAnswers(isAr: Boolean) = if (isAr) "تعديل الإجابات" else "Update Answers"
    fun completedByYou(isAr: Boolean) = if (isAr) "تمت الإجابة بنجاح" else "Completed by you"
    fun pendingYourResponse(isAr: Boolean) = if (isAr) "بانتظار مشاركتك" else "Pending your response"
    fun surveyClosed(isAr: Boolean) = if (isAr) "الاستبانة مغلقة" else "Survey is closed"
    fun responsesCount(isAr: Boolean) = if (isAr) "عدد الإجابات:" else "Responses:"
    fun viewAnalytics(isAr: Boolean) = if (isAr) "تحليل النتائج" else "Analytics"
    fun exportExcel(isAr: Boolean) = if (isAr) "تصدير تقرير Excel" else "Export Excel"
    fun publishSurvey(isAr: Boolean) = if (isAr) "نشر الاستبانة" else "Publish Survey"
    fun submitSurveyResponse(isAr: Boolean) = if (isAr) "إرسال الإجابات للمرشد" else "Submit Questionnaire Response"

    // Announcements
    fun noticesHeader(isAr: Boolean) = if (isAr) "إعلانات ونشرات الإرشاد" else "School Announcements"
    fun noticesSubtitle(isAr: Boolean) = if (isAr) "التعاميم والتوجيهات الرسمية الصادرة من الأستاذ جعفر مهدي" else "Official notices & counseling bulletins from Mr. Jaafar Mahdi"
    fun newNotice(isAr: Boolean) = if (isAr) "إعلان جديد" else "New Notice"
    fun urgentAlert(isAr: Boolean) = if (isAr) "تنبيه عاجل" else "URGENT ALERT"
    fun markAsUrgent(isAr: Boolean) = if (isAr) "تمييز كتنبيه عاجل" else "Mark as Urgent Alert"
    fun targetAudienceLabel(isAr: Boolean) = if (isAr) "الفئة المستهدفة" else "Target Audience Option:"
    fun publishNotice(isAr: Boolean) = if (isAr) "نشر الإعلان" else "Publish Announcement"

    // Complaints & Consultations
    fun directLineHeader(isAr: Boolean) = if (isAr) "الخط المباشر للإرشاد والتوجيه" else "Direct Counseling Line"
    fun directLineSubtitle(isAr: Boolean) = if (isAr) "تواصل سري ومباشر مع المرشد الطلابي الأستاذ جعفر مهدي" else "Confidential consultations directly with Mr. Jaafar Mahdi"
    fun adminLineHeader(isAr: Boolean) = if (isAr) "لوحة متابعة الاستشارات والشكاوى" else "Counseling & Inquiries Panel"
    fun adminLineSubtitle(isAr: Boolean) = if (isAr) "مراجعة قضايا الطلاب وتقديم التوجيهات الرسمية لأولياء الأمور" else "Review parent issues and provide counselor guidance responses"
    fun newIssue(isAr: Boolean) = if (isAr) "استشارة / شكوى جديدة" else "New Issue"
    fun concernCategory(isAr: Boolean) = if (isAr) "تصنيف الحالة:" else "Category of Concern:"
    fun urgencyLevel(isAr: Boolean) = if (isAr) "مستوى الأهمية:" else "Urgency Level:"
    fun describeConcern(isAr: Boolean) = if (isAr) "تفاصيل الاستشارة أو الشكوى" else "Describe the situation or inquiry"
    fun requestCallback(isAr: Boolean) = if (isAr) "طلب اتصال هاتفي خاص من المرشد الأستاذ جعفر مهدي" else "Request a confidential phone callback from Mr. Jaafar Mahdi"
    fun submitToOffice(isAr: Boolean) = if (isAr) "إرسال إلى مكتب التوجيه والإرشاد" else "Submit to Guidance Office"
    fun counselorNoteHeader(isAr: Boolean) = if (isAr) "توجيه ورأي المرشد الطلابي • الأستاذ جعفر مهدي" else "Counselor Guidance Note • Mr. Jaafar Mahdi"
    fun respondAndUpdate(isAr: Boolean) = if (isAr) "كتابة الرد والتحديث" else "Respond / Update"
    fun saveResponse(isAr: Boolean) = if (isAr) "حفظ التوجيه وإرساله لولي الأمر" else "Save Response & Notify Parent"

    // Status translations
    fun statusPending(isAr: Boolean) = if (isAr) "قيد المراجعة" else "Pending Review"
    fun statusInReview(isAr: Boolean) = if (isAr) "جاري الاطلاع" else "In Review"
    fun statusInProgress(isAr: Boolean) = if (isAr) "قيد المتابعة" else "In Progress"
    fun statusResolved(isAr: Boolean) = if (isAr) "تم الحل والإغلاق" else "Resolved"

    // Urgency translations
    fun urgencyNormal(isAr: Boolean) = if (isAr) "عادي" else "Normal"
    fun urgencyImportant(isAr: Boolean) = if (isAr) "هام" else "Important"
    fun urgencyUrgent(isAr: Boolean) = if (isAr) "عاجل جداً" else "Urgent"

    // Categories
    fun catAcademic(isAr: Boolean) = if (isAr) "الدعم الأكاديمي والتحصيلي" else "Academic Support"
    fun catBehavior(isAr: Boolean) = if (isAr) "السلوك والمواظبة" else "Behavioral & Conduct"
    fun catPsychology(isAr: Boolean) = if (isAr) "الدعم النفسي وإدارة القلق" else "Psychological & Stress"
    fun catBullying(isAr: Boolean) = if (isAr) "العلاقات الطلابية ومكافحة التنمر" else "Peer Relations & Bullying"
    fun catHealth(isAr: Boolean) = if (isAr) "الصحة والغياب المدرسي" else "Attendance & Health"
    fun catGeneral(isAr: Boolean) = if (isAr) "استفسارات عامة" else "General Inquiries"
}
