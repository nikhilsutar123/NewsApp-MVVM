package com.example.newsappmvvm.ui.searchnews

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsappmvvm.MainActivity
import com.example.newsappmvvm.R
import com.example.newsappmvvm.adapters.NewsAdapter
import com.example.newsappmvvm.data.Article
import com.example.newsappmvvm.databinding.SearchNewsFragmentBinding
import com.example.newsappmvvm.ui.NewsViewModel
import com.example.newsappmvvm.util.Constants
import com.example.newsappmvvm.util.Constants.Companion.SEARCH_NEWS_DELAY
import com.example.newsappmvvm.util.Resource
import kotlinx.android.synthetic.main.search_news_fragment.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.search_news_fragment),
    NewsAdapter.OnItemClickListener {

    lateinit var viewModel: NewsViewModel
    private val TAG = "SearchNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        val binding = SearchNewsFragmentBinding.bind(view)

        val newsAdapter = NewsAdapter(this)

        binding.apply {
            searchViewRecyclerView.apply {
                adapter = newsAdapter
                layoutManager = LinearLayoutManager(activity)
                addOnScrollListener(this@SearchNewsFragment.scrollListener)
                setHasFixedSize(true)
            }

            var job: Job? = null
            binding.searchViewEditText.addTextChangedListener { editable ->
                job?.cancel()
                job = MainScope().launch {
                    delay(SEARCH_NEWS_DELAY)
                    editable?.let {
                        if (editable.toString().isNotEmpty()) {
                            viewModel.searchNews(editable.toString())
                        }
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsresponse ->
                        newsAdapter.submitList(newsresponse.articles)
                        val totalPages = newsresponse.totalResults / Constants.SEARCH_QUERY_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                        if(isLastPage){
                            binding.searchViewRecyclerView.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity,message,Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    fun hideProgressBar() {
        progressBar_search_view.visibility = View.INVISIBLE
        isLoading = false
    }

    fun showProgressBar() {
        progressBar_search_view.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrolling = true
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLastPage && !isLoading
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isTotalMoreThanVisible = totalItemCount >= Constants.SEARCH_QUERY_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.searchNews(search_view_edit_text.text.toString())
                isScrolling = false
            }
        }
    }

    override fun onItemClick(article: Article) {
        val bundle = Bundle().apply {
            putSerializable("article", article)
        }
        findNavController().navigate(R.id.action_searchNewsFragment_to_readArticleFragment, bundle)
    }
}