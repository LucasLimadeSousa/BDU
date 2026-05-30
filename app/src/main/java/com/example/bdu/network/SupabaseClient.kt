package com.example.bdu.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseConfig {
    // Cliente original (usado para login e banco atual)
    val client = createSupabaseClient(
        supabaseUrl = "https://lorihszkrdjskiaaqjqq.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imxvcmloc3prcmRqc2tpYWFxanFxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzg2MzU0NzIsImV4cCI6MjA5NDIxMTQ3Mn0.lXA9VIH4GlVPJLWlp8-Cx1slIVw5zAuJlkNtIPIlIxI"
    ) {
        install(Postgrest)
        install(Auth)
        install(Storage)
    }
}
