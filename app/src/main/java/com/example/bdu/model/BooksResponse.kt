package com.example.bdu.model

data class BooksResponse(
    val items: List<BookItem>?
)

data class BookItem(
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String?,
    val authors: List<String>?,
    val description: String?,
    val categories: List<String>?,
    val publishedDate: String?,
    val pageCount: Int?,
    val publisher: String?,
    val language: String?,
    val industryIdentifiers: List<IndustryIdentifier>?,
    val imageLinks: ImageLinks?
)

data class IndustryIdentifier(
    val type: String?,
    val identifier: String?
)

data class ImageLinks(
    val thumbnail: String?
)