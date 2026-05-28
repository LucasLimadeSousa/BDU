package com.example.bdu.livros

import android.content.Context

object RecommendationManager {
    private const val PREFS_NAME = "recommendation_prefs"
    private const val KEY_COURSE_INTERESTS = "course_interests"
    private const val KEY_GENERAL_INTERESTS = "general_interests"
    private const val KEY_USER_COURSE = "user_course"
    private const val KEY_ALGORITHM_ENABLED = "algorithm_enabled"
    private const val KEY_FAVORITES_PRIVATE = "favorites_private"

    /**
     * Define se o algoritmo de recomendação está ativado ou desativado.
     */
    fun setAlgorithmEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ALGORITHM_ENABLED, enabled).apply()
    }

    fun isAlgorithmEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ALGORITHM_ENABLED, true) // Ativado por padrão
    }

    /**
     * Define se os favoritos são privados (não influenciam o algoritmo).
     */
    fun setFavoritesPrivate(context: Context, isPrivate: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_FAVORITES_PRIVATE, isPrivate).apply()
    }

    fun isFavoritesPrivate(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_FAVORITES_PRIVATE, true) // Privado por padrão como solicitado na UI? 
        // Na verdade, na imagem o switch está ligado e diz "Ativado". Geralmente isso significa que a privacidade está ativada.
    }

    /**
     * Limpa todo o histórico de gostos e interesses do usuário.
     */
    fun clearInterests(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .remove(KEY_COURSE_INTERESTS)
            .remove(KEY_GENERAL_INTERESTS)
            .apply()
    }

    /**
     * Salva o curso do usuário para recomendações específicas.
     */
    fun setUserCourse(context: Context, course: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USER_COURSE, course.trim()).apply()
    }

    fun getUserCourse(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USER_COURSE, null)
    }

    /**
     * Adiciona um termo de interesse, classificando-o como relacionado ao curso ou geral.
     */
    fun addInterest(context: Context, term: String) {
        if (!isAlgorithmEnabled(context)) return
        if (term.isBlank() || term.length < 3) return
        
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userCourse = getUserCourse(context)?.lowercase() ?: ""
        val normalizedTerm = term.lowercase().trim()

        // Lógica de afinidade: se o termo tem palavras do curso ou vice-versa
        val isRelatedToCourse = userCourse.isNotEmpty() && (
            normalizedTerm.contains(userCourse) || 
            userCourse.contains(normalizedTerm) ||
            userCourse.split(" ").any { it.length > 3 && normalizedTerm.contains(it) }
        )

        if (isRelatedToCourse) {
            val interests = prefs.getStringSet(KEY_COURSE_INTERESTS, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
            interests.add(normalizedTerm)
            if (interests.size > 10) interests.remove(interests.first())
            prefs.edit().putStringSet(KEY_COURSE_INTERESTS, interests).apply()
        } else {
            val interests = prefs.getStringSet(KEY_GENERAL_INTERESTS, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
            interests.add(normalizedTerm)
            if (interests.size > 15) interests.remove(interests.first())
            prefs.edit().putStringSet(KEY_GENERAL_INTERESTS, interests).apply()
        }
    }

    /**
     * Retorna termos de busca focados no CURSO do usuário.
     */
    fun getCourseSearchTerms(context: Context): List<String> {
        val userCourse = getUserCourse(context)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val courseInterests = prefs.getStringSet(KEY_COURSE_INTERESTS, emptySet())?.toList() ?: emptyList()
        
        val terms = mutableListOf<String>()
        if (!userCourse.isNullOrBlank() && userCourse != "Todos os Cursos") {
            // Variações focadas no curso para garantir resultados pertinentes
            terms.add("subject:\"$userCourse\"")
            terms.add("intitle:\"$userCourse\"")
            terms.add("livro $userCourse")
            terms.add("estudo $userCourse")
            terms.add("fundamentos $userCourse")
            terms.add("manual $userCourse")
            terms.add("tecnico $userCourse")
            terms.add("ciência $userCourse")
        }
        
        // Adiciona interesses que o usuário demonstrou afinidade com o curso
        terms.addAll(courseInterests.map { "intitle:\"$it\"" })
        
        if (terms.isEmpty()) {
            return listOf("tecnologia", "administração", "direito", "medicina", "engenharia", "ciência", "educação").shuffled()
        }
        
        return terms.distinct().shuffled()
    }

    /**
     * Retorna termos de busca para a seção POPULARES, priorizando o gosto do usuário.
     */
    fun getGeneralSearchTerms(context: Context): List<String> {
        val userCourse = getUserCourse(context)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        // Interesses específicos que o usuário demonstrou (cliques, favoritos, etc)
        val generalInterests = prefs.getStringSet(KEY_GENERAL_INTERESTS, emptySet())?.toList() ?: emptyList()
        val courseInterests = prefs.getStringSet(KEY_COURSE_INTERESTS, emptySet())?.toList() ?: emptyList()
        
        val defaultTerms = listOf("ficção", "história", "romance", "biografia", "ciência", "aventura", "suspense", "suspense", "terror", "autoajuda")
        val terms = mutableListOf<String>()

        // 1. Prioridade máxima: Interesses diretos do usuário (embaralhados para não ser sempre igual)
        terms.addAll(generalInterests.shuffled())
        
        // 2. Interesses relacionados ao curso (também contam como gosto)
        terms.addAll(courseInterests.shuffled().take(3))

        // 3. O curso em si (versões variadas)
        if (!userCourse.isNullOrBlank() && userCourse != "Todos os Cursos") {
            terms.add("$userCourse avançado")
            terms.add("literatura $userCourse")
        }

        // 4. Fallback com termos gerais para garantir que a lista nunca fique vazia
        val finalTerms = (terms + defaultTerms.shuffled()).distinct()
        
        // Retornamos uma lista onde os primeiros itens são mais prováveis de serem baseados no gosto
        return finalTerms
    }
}