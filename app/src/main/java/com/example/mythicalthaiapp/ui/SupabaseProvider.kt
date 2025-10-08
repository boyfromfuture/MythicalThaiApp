package com.example.mythicalthaiapp.ui

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseProvider {
    private const val SUPABASE_URL = "https://bqksjaigrsixhhvuzkoa.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJxa3NqYWlncnNpeGhodnV6a29hIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTk0MTM5ODcsImV4cCI6MjA3NDk4OTk4N30.Gy-A7R2Yk4-GMuFDcqQ2eFG770rw72rHYjkPzSZd2Zo"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest)
        // install(GoTrue) only if you plan login later
    }
}
