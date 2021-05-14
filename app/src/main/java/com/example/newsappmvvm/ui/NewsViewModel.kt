package com.example.newsappmvvm.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsappmvvm.NewsApplication
import com.example.newsappmvvm.api.NewsResponse
import com.example.newsappmvvm.data.Article
import com.example.newsappmvvm.repository.NewsRepository
import com.example.newsappmvvm.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application,
    val repository: NewsRepository
) : AndroidViewModel(app) {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("in")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { newsResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = newsResponse
                } else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = newsResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: newsResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { newsResponse ->
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = newsResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = newsResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: newsResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun getSavedNews() = repository.getSavedNews()

    fun saveArticle(article: Article) = viewModelScope.launch {
        repository.upsert(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        repository.deleteArticle(article)
    }

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        /**
         * Pass loading state to livedata before emitting request for data
         */
        breakingNews.postValue(Resource.Loading())
        try {
            /**
             * get a response from newApi and update our livedata with a response
             * if there is an internet connection
             */
            if (hasInternetConnection()) {
                val response = repository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            } else {
                breakingNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNews.postValue(Resource.Error("Network failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion error"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.searchNews(searchQuery, breakingNewsPage)
                searchNews.postValue(handleBreakingNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Resource.Error("Network failure"))
                else -> searchNews.postValue(Resource.Error("Conversion error"))
            }
        }
    }


    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_ETHERNET -> true
                    TYPE_MOBILE -> true
                    else -> false
                }
            }
        }
        return false
    }
}