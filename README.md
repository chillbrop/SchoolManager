# 🏫 SchoolManager — Android App

A complete role-based school management Android app built with **Kotlin + Jetpack Compose + Supabase**.

---

## 🗂️ App Architecture

```
MainActivity.kt          → Entry point, Navigation host
├── SplashScreen.kt      → Animated splash with logo
├── OnboardingScreen.kt  → 4-page swipeable onboarding
├── AuthScreens.kt       → Login + Register screens
├── StudentDashboard.kt  → View assignments + announcements
├── TeacherDashboard.kt  → Full CRUD on assignments + announcements
├── AdminDashboard.kt    → Full CRUD + school overview
├── Components.kt        → Shared UI components
├── AuthViewModel.kt     → Auth state management (Supabase Auth)
├── DataViewModel.kt     → CRUD operations (Supabase Postgrest)
├── Models.kt            → Data classes
├── Screen.kt            → Navigation routes
└── SupabaseClient.kt    → Supabase client singleton
```

---

## 🔐 Role-Based Access

| Role | Can Do |
|---|---|
| **Student** | View assignments, view announcements |
| **Teacher** | All of the above + Create/Edit/Delete assignments + Post/Delete announcements |
| **Admin** | Everything teachers can + School overview dashboard |

---

## 🗃️ Supabase Tables

| Table | Columns |
|---|---|
| `profiles` | id, email, full_name, role, created_at |
| `assignments` | id, title, description, subject, due_date, teacher_id, teacher_name, created_at |
| `announcements` | id, title, content, author_id, author_name, created_at |

---

## 🎨 Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Navigation**: Compose Navigation
- **Backend**: Supabase (Auth + Postgrest + Realtime)
- **Architecture**: MVVM (ViewModel + StateFlow)
- **Min SDK**: 24 (Android 7.0)
# SchoolManager
