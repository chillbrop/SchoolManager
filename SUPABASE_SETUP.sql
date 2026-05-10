-- ================================================================
-- SchoolManager — Supabase Database Setup
-- Run this in your Supabase SQL Editor (Dashboard → SQL Editor)
-- ================================================================

-- 1. PROFILES TABLE
-- Stores user profile data linked to Supabase Auth users
CREATE TABLE IF NOT EXISTS public.profiles (
    id          UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email       TEXT NOT NULL,
    full_name   TEXT NOT NULL DEFAULT '',
    role        TEXT NOT NULL DEFAULT 'student' CHECK (role IN ('student', 'teacher', 'admin')),
    created_at  TIMESTAMPTZ DEFAULT NOW()
);

-- 2. ASSIGNMENTS TABLE
CREATE TABLE IF NOT EXISTS public.assignments (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title        TEXT NOT NULL,
    description  TEXT DEFAULT '',
    subject      TEXT NOT NULL DEFAULT '',
    due_date     TEXT DEFAULT '',
    teacher_id   UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    teacher_name TEXT DEFAULT '',
    created_at   TIMESTAMPTZ DEFAULT NOW()
);

-- 3. ANNOUNCEMENTS TABLE
CREATE TABLE IF NOT EXISTS public.announcements (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title       TEXT NOT NULL,
    content     TEXT NOT NULL DEFAULT '',
    author_id   UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    author_name TEXT DEFAULT '',
    created_at  TIMESTAMPTZ DEFAULT NOW()
);

-- ================================================================
-- ROW LEVEL SECURITY (RLS) — IMPORTANT for security
-- ================================================================

-- Enable RLS on all tables
ALTER TABLE public.profiles     ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.assignments  ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.announcements ENABLE ROW LEVEL SECURITY;

-- PROFILES: Users can read all profiles, only update their own
CREATE POLICY "profiles_select_all"   ON public.profiles FOR SELECT USING (true);
CREATE POLICY "profiles_insert_own"   ON public.profiles FOR INSERT WITH CHECK (auth.uid() = id);
CREATE POLICY "profiles_update_own"   ON public.profiles FOR UPDATE USING (auth.uid() = id);

-- ASSIGNMENTS: Everyone can read; only authenticated users can insert/update/delete
CREATE POLICY "assignments_select_all"    ON public.assignments FOR SELECT USING (true);
CREATE POLICY "assignments_insert_auth"   ON public.assignments FOR INSERT WITH CHECK (auth.role() = 'authenticated');
CREATE POLICY "assignments_update_auth"   ON public.assignments FOR UPDATE USING (auth.role() = 'authenticated');
CREATE POLICY "assignments_delete_auth"   ON public.assignments FOR DELETE USING (auth.role() = 'authenticated');

-- ANNOUNCEMENTS: Everyone can read; only authenticated users can insert/delete
CREATE POLICY "announcements_select_all"  ON public.announcements FOR SELECT USING (true);
CREATE POLICY "announcements_insert_auth" ON public.announcements FOR INSERT WITH CHECK (auth.role() = 'authenticated');
CREATE POLICY "announcements_delete_auth" ON public.announcements FOR DELETE USING (auth.role() = 'authenticated');

-- ================================================================
-- OPTIONAL: Auto-create profile on signup trigger
-- ================================================================
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
    -- Profile is inserted from the app after signup, but this is a safety net
    INSERT INTO public.profiles (id, email, full_name, role)
    VALUES (NEW.id, NEW.email, COALESCE(NEW.raw_user_meta_data->>'full_name', ''), 'student')
    ON CONFLICT (id) DO NOTHING;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- ================================================================
-- SAMPLE DATA (optional — uncomment to insert test data)
-- ================================================================

-- INSERT INTO public.assignments (title, subject, description, due_date, teacher_name)
-- VALUES
--   ('Math Homework', 'Mathematics', 'Complete exercises 1–10 on page 45.', '2025-06-01', 'Mr. Smith'),
--   ('Science Report', 'Science', 'Write a 500-word essay on photosynthesis.', '2025-06-05', 'Mrs. Johnson');

-- INSERT INTO public.announcements (title, content, author_name)
-- VALUES
--   ('School Open Day', 'Parents are invited to our Open Day on 15th June.', 'Admin'),
--   ('Term 2 Exams', 'Term 2 exams begin on 20th June. Good luck!', 'Principal');

-- ================================================================
-- VERIFICATION QUERIES
-- ================================================================
-- SELECT * FROM public.profiles;
-- SELECT * FROM public.assignments;
-- SELECT * FROM public.announcements;
