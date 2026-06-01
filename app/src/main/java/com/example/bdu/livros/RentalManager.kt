package com.example.bdu.livros

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

object RentalManager {
    // Session variables (Lost when app closes)
    var sessionRentedTitle: String? = null
    var sessionRentedImage: String? = null

    private const val PREFS_NAME = "rental_history_prefs"
    private const val HISTORY_KEY = "permanent_history"

    fun hasRentedInSession(): Boolean = sessionRentedTitle != null

    fun rentBook(context: Context, title: String, imageUrl: String?, author: String? = null) {
        sessionRentedTitle = title
        sessionRentedImage = imageUrl

        // Save to permanent history
        val history = getPersistentHistory(context).toMutableList()
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        // Add new rental to the beginning of the list
        history.add(0, RentalHistoryItem(title, date, (1..15).random(), author, imageUrl))
        saveHistory(context, history)
    }

    private fun saveHistory(context: Context, history: List<RentalHistoryItem>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(history)
        prefs.edit().putString(HISTORY_KEY, json).apply()
    }

    fun getPersistentHistory(context: Context): List<RentalHistoryItem> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<RentalHistoryItem>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun hasOverdueBook(context: Context): Boolean {
        // Para fins de demonstração, consideramos atraso se o livro foi alugado há mais de 10 dias no histórico
        val history = getPersistentHistory(context)
        return history.any { it.daysAgo > 10 }
    }

    fun isBookRented(title: String): Boolean {
        return sessionRentedTitle == title
    }
}

data class RentalHistoryItem(
    val title: String,
    val date: String,
    val daysAgo: Int,
    val author: String? = null,
    val imageUrl: String? = null
)