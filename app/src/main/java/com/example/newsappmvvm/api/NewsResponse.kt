package com.example.newsappmvvm.api

import com.example.newsappmvvm.data.Article

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)