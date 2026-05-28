package com.example.bdu.livros

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object WaitlistManager {
    private const val PREFS_NAME = "waitlist_prefs"
    private const val WAITLIST_KEY = "waitlist_items"

    fun addToWaitlist(context: Context, item: WaitlistItem) {
        val list = getWaitlist(context).toMutableList()
        if (list.none { it.title == item.title }) {
            list.add(item)
            saveWaitlist(context, list)
        }
    }

    fun removeFromWaitlist(context: Context, title: String) {
        val list = getWaitlist(context).toMutableList()
        list.removeAll { it.title == title }
        saveWaitlist(context, list)
    }

    fun getWaitlist(context: Context): List<WaitlistItem> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(WAITLIST_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<WaitlistItem>>() {}.type
        return Gson().fromJson(json, type)
    }

    private fun saveWaitlist(context: Context, list: List<WaitlistItem>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(list)
        prefs.edit().putString(WAITLIST_KEY, json).apply()
    }
}

data class WaitlistItem(
    val title: String,
    val author: String?,
    val image: String?,
    val position: String,
    val date: String,
    val isAvailable: Boolean = false
)
