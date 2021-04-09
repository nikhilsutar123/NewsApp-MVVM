package com.example.newsappmvvm.data

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)