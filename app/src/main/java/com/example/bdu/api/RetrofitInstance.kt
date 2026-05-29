package com.example.bdu.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    const val API_KEY = "AIzaSyBaLvjc1Ky2-L9OqJOv9lxICkslUQ6x-wY"

    val api: BooksApi by lazy {

        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BooksApi::class.java)
    }
}