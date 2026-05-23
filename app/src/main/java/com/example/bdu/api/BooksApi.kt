package com.example.bdu.api

import com.example.bdu.model.BooksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface BooksApi {

    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("key") apiKey: String? = null,
        @Query("hl") lang: String = "pt",
        @Query("lr") langRestrict: String = "lang_pt"
    ): BooksResponse
}