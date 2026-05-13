package com.example.bdu.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseConfig{
    val client = createSupabaseClient(
        supabaseUrl = "https://lorihszkrdjskiaaqjqq.supabase.co",
        supabaseKey = "sb_publishable_opk8Hq7zSjUbCBXR3h2etQ_0Fdk3e5y"
    ) {
        install(Postgrest)

        install(Auth)
    }
}