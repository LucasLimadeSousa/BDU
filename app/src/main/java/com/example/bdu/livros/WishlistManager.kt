package com.example.bdu.livros

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

object WishlistManager {
    private const val PREFS_NAME = "favoritos_prefs"
    private const val FAVORITES_LIST_KEY = "favorites_list"

    fun addToWishlist(context: Context, item: WaitlistItem) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        
        val favoritesSet = prefs.getStringSet(FAVORITES_LIST_KEY, null)?.toMutableSet() ?: mutableSetOf()
        
        val title = item.title
        favoritesSet.add(title)
        
        editor.putBoolean("fav_$title", true)
        editor.putString("author_$title", item.author)
        editor.putString("image_$title", item.image)
        
        // As WaitlistItem doesn't have all details, we save what we have.
        // If it was already there with more details, this will only overwrite title/author/image.
        
        editor.putStringSet(FAVORITES_LIST_KEY, favoritesSet)
        editor.apply()
    }

    fun isFavorited(context: Context, title: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean("fav_$title", false)
    }
}
