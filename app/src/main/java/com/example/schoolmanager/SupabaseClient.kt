package com.example.schoolmanager

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseClientProvider {
    private const val SUPABASE_URL = "https://duvhlacbtbuvgpvywcto.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImR1dmhsYWNidGJ1dmdwdnl3Y3RvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzg0Mzc4MTYsImV4cCI6MjA5NDAxMzgxNn0.QxEDf8KwWl9eqV9Ag4Q406iWecda85cuSm4e4PmQ5BY"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Realtime)
    }
}
