package com.example.newsappmvvm.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val author: String?,
    val title: String?,
    val description: String?,
    val source: Source?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?,
): Serializable