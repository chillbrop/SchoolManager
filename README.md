# 🏫 SchoolManager — Android App

A complete role-based school management Android app built with **Kotlin + Jetpack Compose + Supabase**.

---

## ✅ Features

| Feature | Status |
|---|---|
| Splash Screen (animated) | ✅ |
| Onboarding (4 slides, swipeable) | ✅ |
| Auth — Register | ✅ Supabase Auth |
| Auth — Login | ✅ Supabase Auth |
| Auth — Logout | ✅ |
| Auth — Visible in Supabase dashboard | ✅ `profiles` table |
| CREATE data → Supabase | ✅ Assignments + Announcements |
| UPDATE data in Supabase | ✅ Edit assignments |
| DELETE data from Supabase | ✅ Assignments + Announcements |
| Role-based dashboards | ✅ Student / Teacher / Admin |
| Beautiful Material 3 UI | ✅ |

---

## 🚀 Setup Instructions

### Step 1 — Create a Supabase Project
1. Go to [https://supabase.com](https://supabase.com) and create a free account
2. Create a new project
3. Go to **Settings → API** and copy:
   - **Project URL** (looks like `https://xxxx.supabase.co`)
   - **anon/public key**

### Step 2 — Set Up the Database
1. In your Supabase project, go to **SQL Editor**
2. Open `SUPABASE_SETUP.sql` from this folder
3. Paste the entire contents and click **Run**
4. This creates: `profiles`, `assignments`, `announcements` tables with RLS policies

### Step 3 — Add Your Credentials to the App
Open this file:
```
app/src/main/java/com/example/schoolmanager/SupabaseClient.kt
```
Replace:
```kotlin
private const val SUPABASE_URL = "https://YOUR_PROJECT_ID.supabase.co"
private const val SUPABASE_ANON_KEY = "YOUR_ANON_KEY_HERE"
```

### Step 4 — Build & Run
Open the project in Android Studio and click Run.

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
