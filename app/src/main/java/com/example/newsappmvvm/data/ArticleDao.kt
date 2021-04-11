package com.example.newsappmvvm.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Query("select * from articles")
     fun getAllArticles(): LiveData<List<Article>>

     @Delete
     suspend fun deleteArticle(article: Article)
}