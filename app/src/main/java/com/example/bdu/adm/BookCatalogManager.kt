package com.example.bdu.adm

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object BookCatalogManager {
    private const val PREFS_NAME = "catalog_prefs"
    private const val EXCLUDED_BOOKS_KEY = "excluded_titles"
    private const val ADDED_BOOKS_KEY = "added_books_list"

    fun getExcludedBooks(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(EXCLUDED_BOOKS_KEY, emptySet()) ?: emptySet()
    }

    fun excludeBook(context: Context, title: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val normalizedTitle = title.lowercase().trim()
        
        // 1. Adiciona à lista de excluídos de forma segura e síncrona
        val excluded = getExcludedBooks(context).toMutableSet()
        excluded.add(normalizedTitle)
        
        // 2. Procura por qualquer edição/override desse livro
        val added = getAddedBooks(context).toMutableList()
        val bookToRemove = added.find { 
            it.title.lowercase().trim() == normalizedTitle || 
            it.originalTitle?.lowercase()?.trim() == normalizedTitle 
        }
        
        if (bookToRemove != null) {
            // Se o livro tinha um nome original diferente (ex: era da API), exclui o nome original também
            bookToRemove.originalTitle?.let { excluded.add(it.lowercase().trim()) }
            added.remove(bookToRemove)
            saveAddedBooks(context, added)
        }

        // Salva a lista de excluídos de forma síncrona para garantir persistência imediata
        prefs.edit().putStringSet(EXCLUDED_BOOKS_KEY, excluded).commit()

        // 3. Remove dos favoritos para "sumir de todas as formas"
        removerDosFavoritos(context, title)
        bookToRemove?.title?.let { removerDosFavoritos(context, it) }
        bookToRemove?.originalTitle?.let { removerDosFavoritos(context, it) }
    }

    private fun removerDosFavoritos(context: Context, title: String) {
        val favPrefs = context.getSharedPreferences("favoritos_prefs", Context.MODE_PRIVATE)
        val favoritesSet = favPrefs.getStringSet("favorites_list", null)?.toMutableSet() ?: return
        
        val normalizedTitle = title.lowercase().trim()
        val actualTitleInFavs = favoritesSet.find { it.lowercase().trim() == normalizedTitle }
        
        if (actualTitleInFavs != null) {
            favoritesSet.remove(actualTitleInFavs)
            favPrefs.edit().apply {
                putStringSet("favorites_list", favoritesSet)
                remove("fav_$actualTitleInFavs")
                remove("fav_$normalizedTitle")
                remove("author_$actualTitleInFavs")
                remove("image_$actualTitleInFavs")
                remove("genre_$actualTitleInFavs")
                remove("synopsis_$actualTitleInFavs")
                remove("publication_$actualTitleInFavs")
                remove("isbn_$actualTitleInFavs")
                remove("publisher_$actualTitleInFavs")
                remove("pages_$actualTitleInFavs")
            }.commit()
        }
    }

    fun isBookExcluded(context: Context, title: String?): Boolean {
        if (title.isNullOrBlank()) return false
        val excluded = getExcludedBooks(context)
        val normalized = title.lowercase().trim()
        
        // Agora usamos comparação EXATA para evitar que um livro bloqueado suma com outros parecidos
        return excluded.contains(normalized)
    }

    fun addCustomBook(context: Context, book: CustomBook) {
        val added = getAddedBooks(context).toMutableList()
        val normalizedTitle = book.title.lowercase().trim()

        // Remove duplicados antes de adicionar
        added.removeAll { it.title.lowercase().trim() == normalizedTitle }
        added.add(0, book)
        saveAddedBooks(context, added)

        // IMPORTANTE: Ao adicionar manualmente, o livro DEIXA de estar excluído
        val excluded = getExcludedBooks(context).toMutableSet()
        excluded.remove(normalizedTitle)
        book.originalTitle?.let { excluded.remove(it.lowercase().trim()) }
        
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putStringSet(EXCLUDED_BOOKS_KEY, excluded).commit()
    }

    fun updateCustomBook(context: Context, oldTitle: String, originalTitle: String?, updatedBook: CustomBook) {
        val added = getAddedBooks(context).toMutableList()
        val oldTitleNormalized = oldTitle.lowercase().trim()
        val newTitleNormalized = updatedBook.title.lowercase().trim()
        val originalTitleNormalized = (originalTitle ?: oldTitle).lowercase().trim()

        // 1. Limpa versões anteriores
        added.removeAll { it.title.lowercase().trim() == oldTitleNormalized }
        added.removeAll { it.originalTitle?.lowercase()?.trim() == originalTitleNormalized }
        
        // 2. Salva a nova versão vinculada ao original
        val bookToSave = updatedBook.copy(originalTitle = originalTitle ?: oldTitle)
        added.add(0, bookToSave)
        saveAddedBooks(context, added)

        // 3. Garante que o NOVO título não esteja na lista de excluídos
        val excluded = getExcludedBooks(context).toMutableSet()
        excluded.remove(newTitleNormalized)
        
        // Se mudou o nome, esconde o original da API
        if (newTitleNormalized != originalTitleNormalized) {
            excluded.add(originalTitleNormalized)
        }
        
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putStringSet(EXCLUDED_BOOKS_KEY, excluded).commit()
    }

    fun getAddedBooks(context: Context): List<CustomBook> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(ADDED_BOOKS_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<CustomBook>>() {}.type
        return try { Gson().fromJson(json, type) } catch (e: Exception) { emptyList() }
    }

    fun getBookOverride(context: Context, title: String?): CustomBook? {
        if (title.isNullOrBlank()) return null
        val normalizedTitle = title.lowercase().trim()
        
        // Não retorna override se o livro estiver explicitamente excluído
        if (isBookExcluded(context, normalizedTitle)) return null
        
        val allAdded = getAddedBooks(context)
        return allAdded.find { 
            it.title.lowercase().trim() == normalizedTitle || 
            it.originalTitle?.lowercase()?.trim() == normalizedTitle 
        }
    }

    private fun saveAddedBooks(context: Context, books: List<CustomBook>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(books)
        prefs.edit().putString(ADDED_BOOKS_KEY, json).commit()
    }
}

data class CustomBook(
    val title: String,
    val originalTitle: String? = null, 
    val author: String? = null,
    val genre: String? = null,
    val synopsis: String? = null,
    val date: String? = null,
    val publisher: String? = null,
    val pages: String? = null,
    val language: String? = null,
    val isbn: String? = null,
    val image: String? = null
)