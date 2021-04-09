package com.example.newsappmvvm.data

data class Article(
    val author: String,
    val title: String,
    val description: String,
    val source: Source,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String,
)