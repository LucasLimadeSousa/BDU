package com.example.bdu.usuario

import android.content.Context
import com.example.bdu.network.SupabaseConfig
import io.github.jan.supabase.auth.auth

object NotificationPrefsManager {
    private const val PREFS_NAME = "notification_prefs"
    private const val MARKETING_KEY_PREFIX = "marketing_enabled_"

    private fun getUserEmail(): String {
        return SupabaseConfig.client.auth.currentUserOrNull()?.email ?: "default_user"
    }

    fun isMarketingEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(MARKETING_KEY_PREFIX + getUserEmail(), true)
    }

    fun setMarketingEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(MARKETING_KEY_PREFIX + getUserEmail(), enabled).apply()
    }
}